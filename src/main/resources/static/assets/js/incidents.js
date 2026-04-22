const API_URL = "http://localhost:8080/api";

// ================= LOAD INCIDENTS =================
async function loadIncidents() {
  try {
    const res = await fetch(`${API_URL}/incidents`);
    const data = await res.json();

    console.log("FULL RESPONSE:", data);

    const table = document.getElementById("incidentsTable");
    table.innerHTML = "";

    const incidents = data.content ? data.content : data;

    if (!incidents || incidents.length === 0) {
      table.innerHTML = `<tr><td colspan="6" style="text-align:center;">No incidents found</td></tr>`;
      return;
    }

    incidents.forEach(i => {
      table.innerHTML += `
        <tr>
          <td>${i.id}</td>
          <td>${i.title}</td>
          <td>${i.priority}</td>
          <td>${i.status}</td>
          <td>${i.assignedTo || 'N/A'}</td>
          <td>${renderActions(i)}</td>
        </tr>
      `;
    });

  } catch (err) {
    console.error("Error:", err);
  }
}

// ================= ACTION BUTTONS =================
function renderActions(i) {

  if (i.status === "OPEN") {
    return `<button onclick="updateStatus(${i.id}, 'IN_PROGRESS')">Start</button>`;
  }

  if (i.status === "IN_PROGRESS") {
    return `<button onclick="updateStatus(${i.id}, 'RESOLVED')">Resolve</button>`;
  }

  if (i.status === "RESOLVED") {
    return `<button onclick="updateStatus(${i.id}, 'CLOSED')">Close</button>`;
  }

  return `<span>Done</span>`;
}

// ================= UPDATE STATUS =================
async function updateStatus(id, status) {
  try {
    const res = await fetch(
      `${API_URL}/incidents/${id}/status`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          status: status
        })
      }
    );

    if (!res.ok) {
      const err = await res.text();
      alert(err);
      return;
    }

    alert("Status updated ✅");
    loadIncidents();

  } catch (e) {
    console.error(e);
  }
}
// ================= INIT =================
loadIncidents();