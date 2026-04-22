const BASE_URL = "http://localhost:8080/api";

// ================= LOGIN =================
async function loginUser(data) {
    const res = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });

    if (!res.ok) {
        throw new Error("Login failed: " + res.status);
    }

    return res.json();
}


// ================= AUTH HEADER =================
function getAuthHeaders() {
    const token = localStorage.getItem("token");

    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    };
}


// ================= GET INCIDENTS =================
async function getIncidents() {
    const res = await fetch(`${BASE_URL}/incidents`, {
        headers: getAuthHeaders()
    });

    if (!res.ok) {
        throw new Error("Failed to fetch incidents: " + res.status);
    }

    return res.json();
}


// ================= CREATE INCIDENT =================
async function createIncident(data) {
    const res = await fetch(`${BASE_URL}/incidents`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(data)
    });

    if (!res.ok) {
        throw new Error("Create failed: " + res.status);
    }

    return res.json();
}


// ================= UPDATE STATUS =================
async function updateIncidentStatus(id, status) {
    const res = await fetch(`${BASE_URL}/incidents/${id}/status`, {
        method: "PUT",
        headers: getAuthHeaders(),
        body: JSON.stringify({ status })
    });

    if (!res.ok) {
        throw new Error("Update failed: " + res.status);
    }

    return res.json();
}