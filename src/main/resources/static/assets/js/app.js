// LOGIN HANDLER
async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
        const res = await loginUser({ username, password });

        localStorage.setItem("token", res.token);

        alert("Login successful");

        window.location.href = "view-incidents.html";

    } catch (e) {
        alert("Login failed");
        console.error(e);
    }
}


// LOAD INCIDENTS
async function loadIncidents() {
    try {
        const data = await getIncidents();

        const table = document.getElementById("incidentTable");

        data.content.forEach(inc => {
            const row = `<tr>
                <td>${inc.id}</td>
                <td>${inc.title}</td>
                <td>${inc.priority}</td>
                <td>${inc.status}</td>
            </tr>`;
            table.innerHTML += row;
        });

    } catch (e) {
        alert("Failed to load incidents");
    }
}


// CREATE INCIDENT
async function handleCreateIncident(event) {
    event.preventDefault();

    const title = document.getElementById("title").value;
    const description = document.getElementById("description").value;
    const priority = document.getElementById("priority").value;

    try {
        await createIncident({ title, description, priority });

        alert("Incident created");

    } catch (e) {
        alert("Failed to create incident");
    }
}