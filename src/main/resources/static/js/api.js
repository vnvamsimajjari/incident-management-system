const BASE_URL = "http://localhost:8080/api";


// ================= LOGIN =================
async function loginUser(username, password) {
    const res = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username, password })
    });

    if (!res.ok) {
        throw new Error("Login failed");
    }

    const data = await res.json();
    localStorage.setItem("token", data.token);

    return data;
}


// ================= LOGOUT =================
function logout() {
    localStorage.removeItem("token");
    window.location.href = "login.html";
}


// ================= AUTH HEADER =================
function getAuthHeaders() {
    const token = localStorage.getItem("token");

    if (!token) {
        // 🔥 redirect if not logged in
        window.location.href = "login.html";
    }

    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    };
}


// ================= GET INCIDENTS =================
async function getIncidents() {
    try {
        const res = await fetch(`${BASE_URL}/incidents`, {
            headers: getAuthHeaders()
        });

        if (!res.ok) {
            throw new Error("Failed to fetch incidents");
        }

        return await res.json();

    } catch (error) {
        console.error(error);
        throw error;
    }
}


// ================= CREATE INCIDENT =================
async function createIncident(data) {
    try {
        const res = await fetch(`${BASE_URL}/incidents`, {
            method: "POST",
            headers: getAuthHeaders(),
            body: JSON.stringify(data)
        });

        if (!res.ok) {
            throw new Error("Create failed");
        }

        return await res.json();

    } catch (error) {
        console.error(error);
        throw error;
    }
}


// ================= UPDATE STATUS =================
async function updateIncidentStatus(id, status) {
    try {
        const res = await fetch(`${BASE_URL}/incidents/${id}/status`, {
            method: "PUT",
            headers: getAuthHeaders(),
            body: JSON.stringify({ status })
        });

        if (!res.ok) {
            throw new Error("Update failed");
        }

        return await res.json();

    } catch (error) {
        console.error(error);
        throw error;
    }
}