// ================================
// 🚀 APP START
// ================================
console.log("✅ Dashboard JS Loaded");

// ================================
// 🔐 AUTH CHECK
// ================================
const token = localStorage.getItem("token");

if (!token) {
    console.warn("❌ No token → redirecting to login");
    window.location.href = "/pages/login.html";
}

// ================================
// 📡 FETCH INCIDENTS
// ================================
async function loadDashboard() {
    try {
        console.log("📡 Fetching incidents...");

        const response = await fetch("/api/incidents", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        // 🔴 Handle auth failure
        if (response.status === 401 || response.status === 403) {
            console.warn("❌ Unauthorized → redirecting to login");
            localStorage.removeItem("token");
            window.location.href = "/pages/login.html";
            return;
        }

        if (!response.ok) {
            throw new Error("API Error: " + response.status);
        }

        const data = await response.json();

        console.log("✅ API Response:", data);

        updateDashboard(data);

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
// 🧮 UPDATE UI
// ================================
function updateDashboard(data) {

    // 🔥 Handle both cases (paginated + direct array)
    const incidents = Array.isArray(data)
        ? data
        : (data.content || []);

    if (!Array.isArray(incidents)) {
        console.error("❌ Invalid data format:", data);
        return;
    }

    // ============================
    // 📊 CALCULATIONS
    // ============================
    const total = incidents.length;

   const open = incidents.filter(i => {
       const s = i.status?.toUpperCase();
       return ["OPEN", "IN_PROGRESS"].includes(s);
   }).length;

    const resolved = incidents.filter(i => {
        const s = i.status?.toUpperCase();
        return ["RESOLVED", "CLOSED"].includes(s);
    }).length;

    console.log("📊 Stats:", { total, open, resolved });

    // ============================
    // 🖥️ UPDATE DOM
    // ============================
    document.getElementById("totalCount").innerText = total;
    document.getElementById("openCount").innerText = open;
    document.getElementById("resolvedCount").innerText = resolved;
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