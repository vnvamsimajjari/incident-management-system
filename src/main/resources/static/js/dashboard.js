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
// 📡 FETCH INCIDENTS
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
            localStorage.removeItem("token");
            window.location.href = "/pages/login.html";
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

        // 🔥 ALL UPDATES
        updateDashboard(incidents);
        renderRecentIncidents(incidents);
        updateCharts(incidents);

    } catch (error) {
        console.error("❌ Dashboard error:", error);

        document.body.innerHTML += `
            <p style="color:red;text-align:center;">
                Failed to load dashboard
            </p>
        `;
    }
}

// ================================
// 🧮 UPDATE CARDS
// ================================
function updateDashboard(incidents) {

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

    document.getElementById("totalCount").innerText = total;
    document.getElementById("openCount").innerText = open;
    document.getElementById("inProgressCount").innerText = inProgress;
    document.getElementById("resolvedCount").innerText = resolved;
    document.getElementById("closedCount").innerText = closed;
}

// ================================
// 📋 RECENT INCIDENTS
// ================================
function renderRecentIncidents(incidents) {

    const table = document.getElementById("recentIncidents");

    table.innerHTML = "";

    const sorted = [...incidents].sort((a, b) => b.id - a.id);
    const recent = sorted.slice(0, 5);

    if (recent.length === 0) {
        table.innerHTML = `
            <tr>
                <td colspan="4" class="empty">
                    No recent incidents
                </td>
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

// ================================
// 📊 CHARTS (THIS WAS MISSING)
// ================================
function updateCharts(incidents) {

    const total = incidents.length;

    const open = incidents.filter(i => i.status === "OPEN").length;
    const progress = incidents.filter(i => i.status === "IN_PROGRESS").length;
    const resolved = incidents.filter(i => i.status === "RESOLVED").length;
    const closed = incidents.filter(i => i.status === "CLOSED").length;

    const high = incidents.filter(i => i.priority === "HIGH").length;
    const medium = incidents.filter(i => i.priority === "MEDIUM").length;
    const low = incidents.filter(i => i.priority === "LOW").length;

    // 🔢 COUNTS
    document.getElementById("openBarCount").innerText = open;
    document.getElementById("progressBarCount").innerText = progress;
    document.getElementById("resolvedBarCount").innerText = resolved;
    document.getElementById("closedBarCount").innerText = closed;

    document.getElementById("highCount").innerText = high;
    document.getElementById("mediumCount").innerText = medium;
    document.getElementById("lowCount").innerText = low;

    // 📏 WIDTH %
    const calc = (val) => total === 0 ? 0 : (val / total) * 100;

    document.getElementById("openBar").style.width = calc(open) + "%";
    document.getElementById("progressBar").style.width = calc(progress) + "%";
    document.getElementById("resolvedBar").style.width = calc(resolved) + "%";
    document.getElementById("closedBar").style.width = calc(closed) + "%";

    document.getElementById("highBar").style.width = calc(high) + "%";
    document.getElementById("mediumBar").style.width = calc(medium) + "%";
    document.getElementById("lowBar").style.width = calc(low) + "%";
}

// ================================
// 🔁 NAVIGATION
// ================================
function goToIncidents() {
    window.location.href = "/pages/incidents.html";
}

// ================================
// 🚀 INIT
// ================================
loadDashboard();