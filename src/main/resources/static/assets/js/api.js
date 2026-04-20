const BASE_URL = "http://localhost:8080/api";

// LOGIN
async function loginUser(data) {
    const res = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });

    return res.json();
}

// GET TOKEN HEADER
function getAuthHeaders() {
    const token = localStorage.getItem("token");

    return {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token
    };
}

// GET INCIDENTS
async function getIncidents() {
    const res = await fetch(`${BASE_URL}/incidents`, {
        headers: getAuthHeaders()
    });

    return res.json();
}

// CREATE INCIDENT
async function createIncident(data) {
    const res = await fetch(`${BASE_URL}/incidents`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(data)
    });

    return res.json();
}