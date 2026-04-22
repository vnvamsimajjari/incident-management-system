// ================= AUTH CHECK =================
(function () {
  const token = localStorage.getItem("token");

  if (!token || token === "undefined" || token === "null") {
    window.location.href = "/pages/login.html";
  }
})();


// ================= REDIRECT FLAG =================
let isRedirecting = false;


// ================= LOAD INCIDENTS =================
async function loadIncidents() {
  try {
    const data = await getIncidents(); // from api.js

    const table = document.getElementById("incidentsTable");
    table.innerHTML = "";

    const incidents = data.content || [];

    if (incidents.length === 0) {
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

  } catch (e) {
    console.error(e);

    if (!isRedirecting) {
      logout();
    }
  }
}


// ================= ACTION BUTTONS =================
function renderActions(i) {

  switch (i.status) {

    case "OPEN":
      return `<button onclick="updateStatus(${i.id}, 'IN_PROGRESS')">Start</button>`;

    case "IN_PROGRESS":
      return `<button onclick="updateStatus(${i.id}, 'RESOLVED')">Resolve</button>`;

    case "RESOLVED":
      return `<button onclick="updateStatus(${i.id}, 'CLOSED')">Close</button>`;

    default:
      return `<span>Done</span>`;
  }
}


// ================= UPDATE STATUS =================
async function updateStatus(id, status) {
  try {
    await updateIncidentStatus(id, status);

    alert("Status updated ✅");
    loadIncidents();

  } catch (e) {
    console.error(e);

    if (!isRedirecting) {
      logout();
    }
  }
}


// ================= LOGOUT =================
function logout() {
  if (isRedirecting) return;

  isRedirecting = true;

  localStorage.removeItem("token");
  window.location.href = "/pages/login.html";
}


// ================= INIT =================
loadIncidents();