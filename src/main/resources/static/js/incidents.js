// ======================================
// 🚀 APP START
// ======================================
console.clear();
console.log("🚀 Incidents JS Loaded");

// ======================================
// 🔐 AUTH CHECK
// ======================================
const token = localStorage.getItem("token");

if (!token || token === "undefined" || token === "null") {
    window.location.href = "/pages/login.html";
}

// ======================================
// 🔁 REDIRECT FLAG
// ======================================
let isRedirecting = false;

// ======================================
// 📡 LOAD INCIDENTS
// ======================================
async function loadIncidents() {

    try {

        console.log("📡 Fetching incidents...");

        showLoading();

        const response = await fetch(
            "/api/incidents?page=0&size=100",
            {
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        );

        // 🔴 Unauthorized
        if (
            response.status === 401 ||
            response.status === 403
        ) {
            logout();
            return;
        }

        if (!response.ok) {
            throw new Error(
                "API Error: " + response.status
            );
        }

        const data = await response.json();

        console.log("✅ API Response:", data);

        const incidents = Array.isArray(data)
            ? data
            : (
                Array.isArray(data?.content)
                    ? data.content
                    : []
            );

        renderTable(incidents);

    } catch (error) {

        console.error(
            "❌ Error loading incidents:",
            error
        );

        showError();

    }
}

// ======================================
// 🖥️ RENDER TABLE
// ======================================
function renderTable(incidents) {

    const table =
        document.getElementById("incidentsTable");

    if (!table) return;

    table.innerHTML = "";

    // ======================================
    // SORT LATEST FIRST
    // ======================================
    const sorted = [...incidents]
        .sort((a, b) => b.id - a.id);

    // ======================================
    // EMPTY STATE
    // ======================================
    if (sorted.length === 0) {

        table.innerHTML = `
            <tr>
                <td colspan="6" class="empty">
                    No incidents found
                </td>
            </tr>
        `;

        return;
    }

    // ======================================
    // TABLE ROWS
    // ======================================
    sorted.forEach(i => {

        table.innerHTML += `
            <tr>

                <td>${i.id}</td>

                <td>${i.title || "-"}</td>

                <td>
                    <span class="priority ${i.priority}">
                        ${i.priority || "-"}
                    </span>
                </td>

                <td>
                    <span class="status ${i.status}">
                        ${i.status || "-"}
                    </span>
                </td>

                <td>${i.assignedTo || "N/A"}</td>

                <td>
                    ${renderActions(i)}
                </td>

            </tr>
        `;
    });
}

// ======================================
// ⚙️ ACTION BUTTONS
// ======================================
function renderActions(i) {

    const status =
        i.status
            ? i.status.toUpperCase()
            : "";

    // ======================================
    // OPEN → START
    // ======================================
    if (status === "OPEN") {

        return `
            <button
                class="action-btn start-btn"
                onclick="updateStatus(
                    ${i.id},
                    'IN_PROGRESS',
                    this
                )">

                Start

            </button>
        `;
    }

    // ======================================
    // IN_PROGRESS → RESOLVE
    // ======================================
    if (status === "IN_PROGRESS") {

        return `
            <button
                class="action-btn resolve-btn"
                onclick="updateStatus(
                    ${i.id},
                    'RESOLVED',
                    this
                )">

                Resolve

            </button>
        `;
    }

    // ======================================
    // RESOLVED → CLOSE
    // ======================================
    if (status === "RESOLVED") {

        return `
            <button
                class="action-btn close-btn"
                onclick="updateStatus(
                    ${i.id},
                    'CLOSED',
                    this
                )">

                Close

            </button>
        `;
    }

    // ======================================
    // CLOSED
    // ======================================
    return `
        <span style="color:#94a3b8;">
            Completed
        </span>
    `;
}

// ======================================
// 🔄 UPDATE STATUS
// ======================================
async function updateStatus(
    id,
    status,
    btn
) {

    try {

        console.log(
            `🔄 Updating ${id} → ${status}`
        );

        // ======================================
        // DISABLE BUTTON
        // ======================================
        if (btn) {

            btn.disabled = true;

            btn.innerText = "Updating...";
        }

        // ======================================
        // API CALL
        // ======================================
        const response = await fetch(
            `/api/incidents/${id}/status`,
            {
                method: "PUT",
                headers: {
                    "Authorization":
                        "Bearer " + token,

                    "Content-Type":
                        "application/json"
                },
                body: JSON.stringify({
                    status: status
                })
            }
        );

        // ======================================
        // AUTH FAILURE
        // ======================================
        if (
            response.status === 401 ||
            response.status === 403
        ) {
            logout();
            return;
        }

        if (!response.ok) {
            throw new Error("Update failed");
        }

        console.log("✅ Status updated");

        // ======================================
        // RELOAD TABLE
        // ======================================
        await loadIncidents();

    } catch (error) {

        console.error(
            "❌ Update error:",
            error
        );

        alert("Update failed ❌");

        // ======================================
        // RESTORE BUTTON
        // ======================================
        if (btn) {

            btn.disabled = false;

            btn.innerText = "Retry";
        }
    }
}

// ======================================
// ⏳ LOADING STATE
// ======================================
function showLoading() {

    const table =
        document.getElementById("incidentsTable");

    if (!table) return;

    table.innerHTML = `
        <tr>
            <td colspan="6" class="empty">
                Loading incidents...
            </td>
        </tr>
    `;
}

// ======================================
// ❌ ERROR STATE
// ======================================
function showError() {

    const table =
        document.getElementById("incidentsTable");

    if (!table) return;

    table.innerHTML = `
        <tr>
            <td colspan="6" class="empty">
                Failed to load incidents
            </td>
        </tr>
    `;
}

// ======================================
// 🚪 LOGOUT
// ======================================
function logout() {

    if (isRedirecting) return;

    isRedirecting = true;

    localStorage.removeItem("token");

    window.location.href =
        "/pages/login.html";
}

// ======================================
// 🔄 AUTO REFRESH
// ======================================
setInterval(loadIncidents, 30000);

// ======================================
// 🚀 INIT
// ======================================
loadIncidents();