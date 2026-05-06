// ======================================
// 🚀 APP START
// ======================================
console.clear();
console.log("🚀 Dashboard JS Loaded");

// ======================================
// 🔐 AUTH CHECK
// ======================================
const token = localStorage.getItem("token");

if (!token || token === "undefined" || token === "null") {
    window.location.href = "/pages/login.html";
}

// ======================================
// 📡 LOAD DASHBOARD
// ======================================
async function loadDashboard() {

    try {

        console.log("📡 Fetching dashboard data...");

        const response = await fetch("/api/incidents?page=0&size=100", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        // 🔴 AUTH FAILURE
        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        if (!response.ok) {
            throw new Error("API Error: " + response.status);
        }

        const data = await response.json();

        console.log("✅ API Response:", data);

        const incidents = Array.isArray(data)
            ? data
            : (Array.isArray(data?.content) ? data.content : []);

        // ======================================
        // UI UPDATES
        // ======================================
        updateDashboardCards(incidents);
        renderRecentIncidents(incidents);
        updateAnalyticsBars(incidents);

    } catch (error) {

        console.error("❌ Dashboard Error:", error);

        showErrorState();

    }
}

// ======================================
// 📊 DASHBOARD CARDS
// ======================================
function updateDashboardCards(incidents) {

    const total = incidents.length;

    const open = countByStatus(incidents, "OPEN");
    const inProgress = countByStatus(incidents, "IN_PROGRESS");
    const resolved = countByStatus(incidents, "RESOLVED");
    const closed = countByStatus(incidents, "CLOSED");

    const sla = calculateSLA(incidents);

    const openedToday = calculateOpenedToday(incidents);
    const resolvedToday = calculateResolvedToday(incidents);

    // ======================================
    // UPDATE UI
    // ======================================
    setText("totalCount", total);
    setText("openCount", open);
    setText("inProgressCount", inProgress);
    setText("resolvedCount", resolved);
    setText("closedCount", closed);

    setText("slaCount", sla);
    setText("openedToday", openedToday);
    setText("resolvedToday", resolvedToday);
}

// ======================================
// 📋 RECENT INCIDENTS TABLE
// ======================================
function renderRecentIncidents(incidents) {

    const table = document.getElementById("recentIncidents");

    if (!table) return;

    table.innerHTML = "";

    const sorted = [...incidents]
        .sort((a, b) => b.id - a.id)
        .slice(0, 5);

    if (sorted.length === 0) {

        table.innerHTML = `
            <tr>
                <td colspan="4" class="empty">
                    No recent incidents
                </td>
            </tr>
        `;

        return;
    }

    sorted.forEach(i => {

        table.innerHTML += `
            <tr>

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

            </tr>
        `;
    });
}

// ======================================
// 📈 ANALYTICS BARS
// ======================================
function updateAnalyticsBars(incidents) {

    const total = incidents.length || 1;

    // ======================================
    // STATUS COUNTS
    // ======================================
    const open = countByStatus(incidents, "OPEN");
    const progress = countByStatus(incidents, "IN_PROGRESS");
    const resolved = countByStatus(incidents, "RESOLVED");
    const closed = countByStatus(incidents, "CLOSED");

    // ======================================
    // PRIORITY COUNTS
    // ======================================
    const high = countByPriority(incidents, "HIGH");
    const medium = countByPriority(incidents, "MEDIUM");
    const low = countByPriority(incidents, "LOW");

    // ======================================
    // UPDATE COUNTS
    // ======================================
    setText("openBarCount", open);
    setText("progressBarCount", progress);
    setText("resolvedBarCount", resolved);
    setText("closedBarCount", closed);

    setText("highCount", high);
    setText("mediumCount", medium);
    setText("lowCount", low);

    // ======================================
    // UPDATE WIDTHS
    // ======================================
    setBarWidth("openBar", open, total);
    setBarWidth("progressBar", progress, total);
    setBarWidth("resolvedBar", resolved, total);
    setBarWidth("closedBar", closed, total);

    setBarWidth("highBar", high, total);
    setBarWidth("mediumBar", medium, total);
    setBarWidth("lowBar", low, total);
}

// ======================================
// 🔥 SLA CALCULATION
// ======================================
function calculateSLA(incidents) {

    let sla = 0;

    const now = new Date();

    incidents.forEach(i => {

        const status = i.status?.toUpperCase();
        const priority = i.priority?.toUpperCase();

        // only active incidents
        if (
            status !== "OPEN" &&
            status !== "IN_PROGRESS"
        ) {
            return;
        }

        if (!i.createdAt) return;

        const createdTime = new Date(i.createdAt);

        const diffHours =
            (now - createdTime) / (1000 * 60 * 60);

        let limit = 0;

        if (priority === "HIGH") limit = 2;
        else if (priority === "MEDIUM") limit = 6;
        else if (priority === "LOW") limit = 12;

        if (diffHours > limit) {
            sla++;
        }

    });

    return sla;
}

// ======================================
// 📅 OPENED TODAY
// ======================================
function calculateOpenedToday(incidents) {

    const today = new Date()
        .toISOString()
        .split("T")[0];

    return incidents.filter(i =>
        i.createdAt &&
        i.createdAt.startsWith(today)
    ).length;
}

// ======================================
// ✅ RESOLVED TODAY
// ======================================
function calculateResolvedToday(incidents) {

    const today = new Date()
        .toISOString()
        .split("T")[0];

    return incidents.filter(i =>

        i.updatedAt &&
        i.updatedAt.startsWith(today) &&
        i.status?.toUpperCase() === "RESOLVED"

    ).length;
}

// ======================================
// 🔢 COUNT BY STATUS
// ======================================
function countByStatus(incidents, status) {

    return incidents.filter(i =>
        i.status?.toUpperCase() === status
    ).length;
}

// ======================================
// 🔢 COUNT BY PRIORITY
// ======================================
function countByPriority(incidents, priority) {

    return incidents.filter(i =>
        i.priority?.toUpperCase() === priority
    ).length;
}

// ======================================
// 📏 SET BAR WIDTH
// ======================================
function setBarWidth(id, value, total) {

    const el = document.getElementById(id);

    if (!el) return;

    const percent = (value / total) * 100;

    el.style.width = percent + "%";
}

// ======================================
// 📝 SAFE TEXT UPDATE
// ======================================
function setText(id, value) {

    const el = document.getElementById(id);

    if (el) {
        el.innerText = value;
    }
}

// ======================================
// ❌ ERROR STATE
// ======================================
function showErrorState() {

    const table = document.getElementById("recentIncidents");

    if (table) {

        table.innerHTML = `
            <tr>
                <td colspan="4" class="empty">
                    Failed to load dashboard
                </td>
            </tr>
        `;
    }
}

// ======================================
// 🔁 NAVIGATION
// ======================================
function goToIncidents() {
    window.location.href = "/pages/incidents.html";
}

// ======================================
// 🚪 LOGOUT
// ======================================
function logout() {

    localStorage.removeItem("token");

    window.location.href = "/pages/login.html";
}

// ======================================
// 🔄 AUTO REFRESH
// ======================================
setInterval(loadDashboard, 30000);

// ======================================
// 🚀 INIT
// ======================================
loadDashboard();