// ======================================
// 🚀 INCIDENTS PAGE
// ======================================

console.clear();
console.log("🚀 Incidents JS Loaded");

// ======================================
// 🔐 AUTH CHECK
// ======================================

const token = localStorage.getItem("token");

if (!token || token === "undefined" || token === "null") {
    window.location.href = "/pages/login.html";
}

// ======================================
// 🔁 REDIRECT FLAG
// ======================================

let isRedirecting = false;

// ======================================
// 📡 LOAD INCIDENTS
// ======================================

async function loadIncidents() {

    try {

        console.log("📡 Fetching incidents...");

        showLoading();

        const response = await fetch(
            "/api/incidents?page=0&size=100",
            {
                method: "GET",
                headers: {
                    "Authorization":
                        "Bearer " + token
                }
            }
        );

        // ❌ Unauthorized
        if (
            response.status === 401 ||
            response.status === 403
        ) {

            logout();
            return;
        }

        if (!response.ok) {

            throw new Error(
                "API Error: " + response.status
            );
        }

        const data = await response.json();

        console.log(
            "✅ API Response:",
            data
        );

        const incidents = Array.isArray(data)
            ? data
            : (
                Array.isArray(data?.content)
                    ? data.content
                    : []
            );

        renderTable(incidents);

    } catch (error) {

        console.error(
            "❌ Error loading incidents:",
            error
        );

        showError();
    }
}

// ======================================
// 🖥️ RENDER TABLE
// ======================================

function renderTable(incidents) {

    const table =
        document.getElementById(
            "incidentsTable"
        );

    if (!table) return;

    table.innerHTML = "";

    // ======================================
    // SORT LATEST FIRST
    // ======================================

    const sorted = [...incidents]
        .sort((a, b) => b.id - a.id);

    // ======================================
    // EMPTY STATE
    // ======================================

    if (sorted.length === 0) {

        table.innerHTML = `
            <tr>
                <td colspan="8" class="empty">
                    No incidents found
                </td>
            </tr>
        `;

        return;
    }

    // ======================================
    // TABLE ROWS
    // ======================================

    sorted.forEach(i => {

        const priority =
            (i.priority || "")
                .toUpperCase();

        const status =
            (i.status || "")
                .toUpperCase();

        const sla =
            getSlaStatus(i);

        table.innerHTML += `

            <tr>

                <!-- ID -->
                <td class="incident-id">
                    #INC-${i.id}
                </td>

                <!-- TITLE -->
                <td>

                    <div class="incident-title">
                        ${i.title || "-"}
                    </div>

                    <div class="incident-sub">
                        ${formatDescription(
                            i.description
                        )}
                    </div>

                </td>

                <!-- PRIORITY -->
                <td>

                    <span class="
                        priority
                        ${priority}
                    ">
                        ${priority}
                    </span>

                </td>

                <!-- STATUS -->
                <td>

                    <span class="
                        status
                        ${status}
                    ">
                        ${formatStatus(status)}
                    </span>

                </td>

                <!-- ASSIGNED -->
                <td>
                    ${i.assignedTo || "N/A"}
                </td>

                <!-- SLA -->
                <td>

                    <span class="
                        status
                        ${sla.className}
                    ">
                        ${sla.label}
                    </span>

                </td>

                <!-- CREATED -->
                <td>
                    ${formatDate(
                        i.createdAt
                    )}
                </td>

                <!-- ACTIONS -->
                <td>

                    <div class="action-group">

                        <!-- VIEW -->
                        <button
                            class="view-btn"
                            onclick="
                                openIncidentDetail(
                                    ${i.id}
                                )
                            "
                        >
                            View
                        </button>

                        <!-- STATUS -->
                        ${renderActions(i)}

                    </div>

                </td>

            </tr>

        `;
    });
}

// ======================================
// ⚙️ ACTION BUTTONS
// ======================================

function renderActions(i) {

    const status =
        i.status
            ? i.status.toUpperCase()
            : "";

    // OPEN
    if (status === "OPEN") {

        return `
            <button
                class="
                    action-btn
                    start-btn
                "
                onclick="
                    updateStatus(
                        ${i.id},
                        'IN_PROGRESS',
                        this
                    )
                "
            >
                Start
            </button>
        `;
    }

    // IN_PROGRESS
    if (status === "IN_PROGRESS") {

        return `
            <button
                class="
                    action-btn
                    resolve-btn
                "
                onclick="
                    updateStatus(
                        ${i.id},
                        'RESOLVED',
                        this
                    )
                "
            >
                Resolve
            </button>
        `;
    }

    // RESOLVED
    if (status === "RESOLVED") {

        return `
            <button
                class="
                    action-btn
                    close-btn
                "
                onclick="
                    updateStatus(
                        ${i.id},
                        'CLOSED',
                        this
                    )
                "
            >
                Close
            </button>
        `;
    }

    // CLOSED
    return `
        <span class="completed-text">
            Completed
        </span>
    `;
}

// ======================================
// 🔄 UPDATE STATUS
// ======================================

async function updateStatus(
    id,
    status,
    btn
) {

    try {

        console.log(
            `🔄 Updating ${id} → ${status}`
        );

        // DISABLE BUTTON
        if (btn) {

            btn.disabled = true;

            btn.innerText =
                "Updating...";
        }

        // API CALL
        const response = await fetch(
            `/api/incidents/${id}/status`,
            {
                method: "PUT",

                headers: {

                    "Authorization":
                        "Bearer " + token,

                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify({
                    status: status
                })
            }
        );

        // AUTH FAILURE
        if (
            response.status === 401 ||
            response.status === 403
        ) {

            logout();
            return;
        }

        if (!response.ok) {

            throw new Error(
                "Update failed"
            );
        }

        console.log(
            "✅ Status updated"
        );

        // RELOAD
        await loadIncidents();

    } catch (error) {

        console.error(
            "❌ Update error:",
            error
        );

        alert(
            "Update failed ❌"
        );

        // RESTORE BUTTON
        if (btn) {

            btn.disabled = false;

            btn.innerText = "Retry";
        }
    }
}

// ======================================
// 🔍 OPEN DETAIL PAGE
// ======================================

function openIncidentDetail(id) {

    window.location.href =
        `/pages/incident-detail.html?id=${id}`;
}

// ======================================
// 🧠 SLA STATUS
// ======================================

function getSlaStatus(i) {

    const status =
        (i.status || "")
            .toUpperCase();

    if (
        status === "RESOLVED" ||
        status === "CLOSED"
    ) {

        return {
            label: "Met",
            className: "RESOLVED"
        };
    }

    if (
        i.priority?.toUpperCase() === "HIGH" &&
        status === "OPEN"
    ) {

        return {
            label: "Breached",
            className: "CRITICAL"
        };
    }

    return {
        label: "Active",
        className: "OPEN"
    };
}

// ======================================
// 📅 FORMAT DATE
// ======================================

function formatDate(date) {

    if (!date) return "-";

    try {

        return new Date(date)
            .toLocaleDateString(
                "en-IN",
                {
                    day: "2-digit",
                    month: "short"
                }
            );

    } catch {

        return "-";
    }
}

// ======================================
// 📝 FORMAT DESCRIPTION
// ======================================

function formatDescription(desc) {

    if (!desc) {
        return "No description";
    }

    if (desc.length > 40) {
        return (
            desc.substring(0, 40)
            + "..."
        );
    }

    return desc;
}

// ======================================
// 🎨 FORMAT STATUS
// ======================================

function formatStatus(status) {

    return status.replaceAll(
        "_",
        " "
    );
}

// ======================================
// ⏳ LOADING STATE
// ======================================

function showLoading() {

    const table =
        document.getElementById(
            "incidentsTable"
        );

    if (!table) return;

    table.innerHTML = `
        <tr>
            <td colspan="8" class="empty">
                Loading incidents...
            </td>
        </tr>
    `;
}

// ======================================
// ❌ ERROR STATE
// ======================================

function showError() {

    const table =
        document.getElementById(
            "incidentsTable"
        );

    if (!table) return;

    table.innerHTML = `
        <tr>
            <td colspan="8" class="empty">
                Failed to load incidents
            </td>
        </tr>
    `;
}

// ======================================
// 🚪 LOGOUT
// ======================================

function logout() {

    if (isRedirecting) return;

    isRedirecting = true;

    localStorage.removeItem("token");

    window.location.href =
        "/pages/login.html";
}

// ======================================
// 🔄 AUTO REFRESH
// ======================================

setInterval(
    loadIncidents,
    30000
);

// ======================================
// 🚀 INIT
// ======================================

loadIncidents();