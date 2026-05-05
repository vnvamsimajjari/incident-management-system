
// ================= LOGIN =================
async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
        const res = await loginUser({ username, password });

        console.log("LOGIN RESPONSE:", res);

        if (res && res.token) {
            localStorage.setItem("token", res.token);

            window.location.href = "/pages/incidents.html";
        } else {
            showError("Invalid credentials");
        }

    } catch (e) {
        console.error(e);
        showError("Login failed");
    }
}


// ================= LOAD INCIDENTS =================
async function loadIncidents() {

    const token = localStorage.getItem("token");

    if (!token || token === "undefined" || token === "null") {
        window.location.href = "/pages/login.html";
        return;
    }

    try {
        const data = await getIncidents(); // ✅ FIXED (no param)

        const table = document.getElementById("incidentsTable"); // ✅ FIXED
        table.innerHTML = "";

        const incidents = data.content || [];

        if (incidents.length === 0) {
            table.innerHTML = `<tr><td colspan="4">No incidents found</td></tr>`;
            return;
        }

        incidents.forEach(inc => {
            table.innerHTML += `
                <tr>
                    <td>${inc.id}</td>
                    <td>${inc.title}</td>
                    <td>${inc.priority}</td>
                    <td>${inc.status}</td>
                </tr>
            `;
        });

    } catch (e) {
        console.error(e);

        // silent redirect (no alert loop)
        logout();
    }
}


// ================= CREATE INCIDENT =================
async function handleCreateIncident(event) {
    event.preventDefault();

    const token = localStorage.getItem("token");

    if (!token || token === "undefined" || token === "null") {
        window.location.href = "/pages/login.html";
        return;
    }

    const title = document.getElementById("title").value;
    const description = document.getElementById("description").value;
    const priority = document.getElementById("priority").value;

    try {
        await createIncident({ title, description, priority }); // ✅ FIXED

        alert("Incident created");

    } catch (e) {
        console.error(e);
        logout();
    }
}


// ================= LOGOUT =================
function logout() {
    localStorage.removeItem("token");
    window.location.href = "/pages/login.html";
}


// ================= ERROR HANDLER =================
function showError(message) {
    const error = document.getElementById("error");
    if (error) error.innerText = message;
}