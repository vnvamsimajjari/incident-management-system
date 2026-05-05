// ================================
// 🚀 APP START
// ================================
console.clear();
console.log("🚀 Dashboard JS Loaded");

// ================================
// 🔐 AUTH CHECK
// ================================
const token = localStorage.getItem("token");

if (!token || token === "undefined" || token === "null") {
    console.warn("❌ No token → redirecting to login");
    window.location.href = "/pages/login.html";
}

// ================================
// 📡 FETCH DATA
// ================================
async function loadDashboard() {
    try {
        console.log("📡 Fetching incidents...");

        const response = await fetch("/api/incidents?page=0&size=100", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        if (!response.ok) {
            throw new Error("API Error: " + response.status);
        }

        const data = await response.json();

        console.log("✅ API Response:", data);

        updateDashboard(data);
        renderRecentIncidents(data);

    } catch (error) {
        console.error("❌ Dashboard error:", error);
    }
}

// ================================
// 📊 DASHBOARD CARDS
// ================================
function updateDashboard(data) {

    const incidents = Array.isArray(data)
        ? data
        : (Array.isArray(data?.content) ? data.content : []);

    if (!Array.isArray(incidents)) {
        console.error("❌ Invalid data format");
        return;
    }

    // ============================
    // BASIC COUNTS
    // ============================
    const total = incidents.length;

    const open = incidents.filter(i =>
        i.status?.toUpperCase() === "OPEN"
    ).length;

    const inProgress = incidents.filter(i =>
        i.status?.toUpperCase() === "IN_PROGRESS"
    ).length;

    const resolved = incidents.filter(i =>
        i.status?.toUpperCase() === "RESOLVED"
    ).length;

    const closed = incidents.filter(i =>
        i.status?.toUpperCase() === "CLOSED"
    ).length;

    // ============================
    // 🔥 TIME-BASED SLA (FINAL)
    // ============================
    let sla = 0;
    let resolvedToday = 0;
    let openedToday = 0;

    const now = new Date();
    const today = now.toISOString().split("T")[0];

    incidents.forEach(i => {

        const status = i.status?.toUpperCase();
        const priority = i.priority?.toUpperCase();

        // ============================
        // SLA LOGIC
        // ============================
        if (!i.createdAt) return;

        // Only active incidents
        if (status !== "OPEN" && status !== "IN_PROGRESS") return;

        const createdTime = new Date(i.createdAt);
        const diffHours = (now - createdTime) / (1000 * 60 * 60);

        let limit = 0;

        if (priority === "HIGH") limit = 2;
        else if (priority === "MEDIUM") limit = 6;
        else if (priority === "LOW") limit = 12;

        if (diffHours > limit) {
            sla++;
        }

        // ============================
        // OPENED TODAY
        // ============================
        if (i.createdAt && i.createdAt.startsWith(today)) {
            openedToday++;
        }

        // ============================
        // RESOLVED TODAY
        // ============================
        if (
            i.updatedAt &&
            i.updatedAt.startsWith(today) &&
            status === "RESOLVED"
        ) {
            resolvedToday++;
        }

    });

    console.log("🔥 FINAL SLA:", sla);

    // ============================
    // UPDATE UI
    // ============================
    document.getElementById("totalCount").innerText = total;
    document.getElementById("openCount").innerText = open;
    document.getElementById("inProgressCount").innerText = inProgress;
    document.getElementById("resolvedCount").innerText = resolved;
    document.getElementById("closedCount").innerText = closed;

    document.getElementById("slaCount").innerText = sla;
    document.getElementById("resolvedToday").innerText = resolvedToday;
    document.getElementById("openedToday").innerText = openedToday;
}

// ================================
// 📋 RECENT INCIDENTS
// ================================
function renderRecentIncidents(data) {

    const table = document.getElementById("recentIncidents");

    const incidents = Array.isArray(data)
        ? data
        : (Array.isArray(data?.content) ? data.content : []);

    if (!Array.isArray(incidents)) return;

    const sorted = [...incidents].sort((a, b) => b.id - a.id);
    const recent = sorted.slice(0, 5);

    table.innerHTML = "";

    if (recent.length === 0) {
        table.innerHTML = `
            <tr>
                <td colspan="4">No recent incidents</td>
            </tr>
        `;
        return;
    }

    recent.forEach(i => {
        table.innerHTML += `
            <tr>
                <td>${i.title || "-"}</td>
                <td>
                    <span class="priority ${i.priority}">
                        ${i.priority}
                    </span>
                </td>
                <td>
                    <span class="status ${i.status}">
                        ${i.status}
                    </span>
                </td>
                <td>${i.assignedTo || "N/A"}</td>
            </tr>
        `;
    });
}

// ================================
// 🔁 NAVIGATION
// ================================
function goToIncidents() {
    window.location.href = "/pages/incidents.html";
}

// ================================
// 🚪 LOGOUT
// ================================
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/pages/login.html";
}

// ================================
// 🚀 INIT
// ================================
loadDashboard();