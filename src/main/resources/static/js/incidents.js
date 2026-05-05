// ================================
// 🚀 APP START
// ================================
console.log("🚀 Incidents JS Loaded");

// ================================
// 🔐 AUTH CHECK
// ================================
const token = localStorage.getItem("token");

if (!token || token === "undefined" || token === "null") {
    window.location.href = "/pages/login.html";
}

// ================================
// 🔁 REDIRECT FLAG
// ================================
let isRedirecting = false;


// ================================
// 📡 LOAD INCIDENTS
// ================================
async function loadIncidents() {
    try {
        console.log("📡 Fetching incidents...");

        const response = await fetch("/api/incidents?page=0&size=100", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        // 🔴 Unauthorized
        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        if (!response.ok) {
            throw new Error("API Error: " + response.status);
        }

        const data = await response.json();

        console.log("✅ API Response:", data);

        renderTable(data);

    } catch (error) {
        console.error("❌ Error loading incidents:", error);
        logout();
    }
}


// ================================
// 🖥️ RENDER TABLE
// ================================
function renderTable(data) {

    const table = document.getElementById("incidentsTable");
    table.innerHTML = "";

    const incidents = Array.isArray(data)
        ? data
        : (data?.content || []);

    if (incidents.length === 0) {
        table.innerHTML = `
            <tr>
                <td colspan="6" style="text-align:center;">
                    No incidents found
                </td>
            </tr>
        `;
        return;
    }

    incidents.forEach(i => {
        table.innerHTML += `
            <tr>
                <td>${i.id}</td>
                <td>${i.title || "-"}</td>
                <td>${i.priority || "-"}</td>
                <td>${i.status || "-"}</td>
                <td>${i.assignedTo || "N/A"}</td>
                <td>${renderActions(i)}</td>
            </tr>
        `;
    });
}


// ================================
// ⚙️ ACTION BUTTONS
// ================================
function renderActions(i) {

    const status = i.status ? i.status.toUpperCase() : "";

    if (status === "OPEN") {
        return `<button onclick="updateStatus(${i.id}, 'IN_PROGRESS', this)">Start</button>`;
    }

    if (status === "IN_PROGRESS") {
        return `<button onclick="updateStatus(${i.id}, 'RESOLVED', this)">Resolve</button>`;
    }

    if (status === "RESOLVED") {
        return `<button onclick="updateStatus(${i.id}, 'CLOSED', this)">Close</button>`;
    }

    return `<span>Done</span>`;
}


// ================================
// 🔄 UPDATE STATUS
// ================================
async function updateStatus(id, status, btn) {
    try {
        console.log(`🔄 Updating ${id} → ${status}`);

        // 🔒 disable button safely
        if (btn) {
            btn.disabled = true;
            btn.innerText = "Updating...";
        }

        const response = await fetch(`/api/incidents/${id}/status`, {
            method: "PUT",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                status: status
            })
        });

        // 🔴 Unauthorized
        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        if (!response.ok) {
            throw new Error("Update failed");
        }

        console.log("✅ Status updated");

        // 🔄 reload table
        await loadIncidents();

    } catch (error) {
        console.error("❌ Update error:", error);
        alert("Update failed ❌");

        // 🔓 re-enable button if failed
        if (btn) {
            btn.disabled = false;
            btn.innerText = "Retry";
        }
    }
}


// ================================
// 🚪 LOGOUT
// ================================
function logout() {
    if (isRedirecting) return;

    isRedirecting = true;

    localStorage.removeItem("token");
    window.location.href = "/pages/login.html";
}


// ================================
// 🚀 INIT
// ================================
loadIncidents();