// ================================
// 🚀 APP START
// ================================
console.log("🚀 Create Incident JS Loaded");

// ================================
// 🔐 AUTH CHECK
// ================================
const token = localStorage.getItem("token");

if (!token || token === "undefined" || token === "null") {
    window.location.href = "/pages/login.html";
}


// ================================
// 📡 SUBMIT FORM
// ================================
document.getElementById("incidentForm")
.addEventListener("submit", async function (e) {

    e.preventDefault();

    // ============================
    // 📥 COLLECT DATA
    // ============================
    const incident = {
        title: document.getElementById("title").value.trim(),
        description: document.getElementById("description").value.trim(),
        priority: document.getElementById("priority").value,
        assignedTo: document.getElementById("assignedTo").value,
        slaMinutes: Number(document.getElementById("slaMinutes").value)
    };

    console.log("📤 Sending:", incident);

    try {

        // ========================
        // 📡 API CALL
        // ========================
        const response = await fetch("/api/incidents", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(incident)
        });

        // 🔴 Unauthorized handling
        if (response.status === 401 || response.status === 403) {
            logout();
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            console.error("❌ API Error:", errorText);
            alert("Error: " + errorText);
            return;
        }

        console.log("✅ Incident created");

        alert("Incident created successfully ✅");

        // ========================
        // 🔁 REDIRECT
        // ========================
        window.location.href = "/pages/incidents.html";

    } catch (error) {
        console.error("❌ Network Error:", error);
        alert("Something went wrong ❌");
    }
});


// ================================
// 🚪 LOGOUT
// ================================
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/pages/login.html";
}