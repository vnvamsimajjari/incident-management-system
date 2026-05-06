// ======================================
// 🚀 INCIDENT DETAIL PAGE
// ======================================

console.clear();
console.log("🚀 Incident Detail JS Loaded");

// ======================================
// 🔐 AUTH CHECK
// ======================================

const token = localStorage.getItem("token");

if (!token || token === "undefined" || token === "null") {

    window.location.href =
        "/pages/login.html";
}

// ======================================
// 📌 GET INCIDENT ID
// ======================================

const params =
    new URLSearchParams(window.location.search);

const incidentId =
    params.get("id");

if (!incidentId) {

    alert("Incident ID missing");

    window.location.href =
        "/pages/incidents.html";
}

// ======================================
// 📡 LOAD INCIDENT DETAILS
// ======================================

async function loadIncidentDetail() {

    try {

        console.log(
            `📡 Loading incident ${incidentId}`
        );

        const response = await fetch(
            `/api/incidents/${incidentId}`,
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
                "Failed to load incident"
            );
        }

        const incident =
            await response.json();

        console.log(
            "✅ Incident Loaded:",
            incident
        );

        renderIncident(incident);

        renderTimeline(incident);

        calculateSLA(incident);

    } catch (error) {

        console.error(
            "❌ Error loading incident:",
            error
        );

        alert(
            "Failed to load incident"
        );
    }
}

// ======================================
// 🖥️ RENDER INCIDENT
// ======================================

function renderIncident(i) {

    // ======================================
    // TITLE
    // ======================================

    setText(
        "incidentTitle",
        i.title || "Untitled Incident"
    );

    // ======================================
    // DESCRIPTION
    // ======================================

    setText(
        "incidentDescription",
        i.description ||
        "No description provided"
    );

    // ======================================
    // INCIDENT ID
    // ======================================

    setText(
        "incidentCode",
        `#INC-${i.id}`
    );

    setText(
        "incidentSubId",
        `#INC-${i.id}`
    );

    // ======================================
    // ASSIGNED TO
    // ======================================

    setText(
        "assignedTo",
        (
            i.assignedTo ||
            "engineer"
        ).toLowerCase()
    );

    // ======================================
    // CREATED DATE
    // ======================================

    setText(
        "createdAt",
        formatFullDate(i.createdAt)
    );

    // ======================================
    // SLA DUE
    // ======================================

    setText(
        "slaDue",
        calculateSlaDue(i)
    );

    // ======================================
    // PRIORITY BADGE
    // ======================================

    const priorityBadge =
        document.getElementById(
            "priorityBadge"
        );

    if (priorityBadge) {

        priorityBadge.innerText =
            i.priority || "-";

        priorityBadge.className =
            `badge ${(
                i.priority || ""
            ).toUpperCase()}`;
    }

    // ======================================
    // STATUS BADGE
    // ======================================

    const statusBadge =
        document.getElementById(
            "statusBadge"
        );

    if (statusBadge) {

        statusBadge.innerText =
            formatStatus(i.status);

        statusBadge.className =
            `badge ${(
                i.status || ""
            ).toUpperCase()}`;
    }

    // ======================================
    // SLA TARGET
    // ======================================

    setText(
        "slaTarget",
        getSlaHours(i.priority)
        + " Hours"
    );
}

// ======================================
// 📜 RENDER TIMELINE
// ======================================

function renderTimeline(i) {

    const timeline =
        document.getElementById(
            "timeline"
        );

    if (!timeline) return;

    timeline.innerHTML = `

        <div class="timeline-item">

            <div class="timeline-dot"></div>

            <div class="timeline-content">

                <div class="timeline-title">
                    Incident created
                </div>

                <div class="timeline-time">
                    ${formatFullDate(
                        i.createdAt
                    )}
                </div>

            </div>

        </div>

        <div class="timeline-item">

            <div class="timeline-dot"></div>

            <div class="timeline-content">

                <div class="timeline-title">
                    Assigned to
                    ${(
                        i.assignedTo ||
                        "engineer"
                    ).toLowerCase()}
                </div>

                <div class="timeline-time">
                    ${formatFullDate(
                        i.createdAt
                    )}
                </div>

            </div>

        </div>

        <div class="timeline-item">

            <div class="timeline-dot"></div>

            <div class="timeline-content">

                <div class="timeline-title">
                    Added to Standard Queue
                </div>

                <div class="timeline-time">
                    ${formatFullDate(
                        i.updatedAt ||
                        i.createdAt
                    )}
                </div>

            </div>

        </div>

        <div class="timeline-item">

            <div class="timeline-dot"></div>

            <div class="timeline-content">

                <div class="timeline-title">
                    Status changed to
                    ${formatStatus(
                        i.status
                    )}
                </div>

                <div class="timeline-time">
                    ${formatFullDate(
                        i.updatedAt ||
                        i.createdAt
                    )}
                </div>

            </div>

        </div>

    `;
}

// ======================================
// ⏱️ SLA CALCULATION
// ======================================

function calculateSLA(i) {

    const created =
        new Date(i.createdAt);

    const now =
        new Date();

    const diffMs =
        now - created;

    const diffHours =
        diffMs /
        (1000 * 60 * 60);

    const diffMinutes =
        Math.floor(
            (diffMs / (1000 * 60)) % 60
        );

    const target =
        getSlaHours(i.priority);

    // ======================================
    // TIME ELAPSED
    // ======================================

    setText(
        "timeElapsed",
        `${Math.floor(diffHours)}h ${diffMinutes}m`
    );

    // ======================================
    // BREACH STATUS
    // ======================================

    const breach =
        document.getElementById(
            "breachStatus"
        );

    if (!breach) return;

    breach.classList.remove(
        "danger",
        "success"
    );

    if (diffHours > target) {

        breach.innerText =
            "Breached";

        breach.classList.add(
            "danger"
        );

    } else {

        breach.innerText =
            "Within SLA";

        breach.classList.add(
            "success"
        );
    }

    // ======================================
    // ETA
    // ======================================

    const eta =
        document.getElementById(
            "eta"
        );

    if (!eta) return;

    eta.classList.remove(
        "danger",
        "success"
    );

    const remaining =
        target - diffHours;

    if (remaining <= 0) {

        eta.innerText =
            "Escalation Required";

        eta.classList.add(
            "danger"
        );

    } else {

        eta.innerText =
            `${remaining.toFixed(1)}h Remaining`;

        eta.classList.add(
            "success"
        );
    }
}

// ======================================
// 📂 FILE UPLOAD
// ======================================

const fileUpload =
    document.getElementById(
        "fileUpload"
    );

if (fileUpload) {

    fileUpload.addEventListener(
        "change",
        handleFiles
    );
}

// ======================================
// 📸 SCREENSHOT PASTE
// ======================================

document.addEventListener(
    "paste",
    function (event) {

        const items =
            event.clipboardData.items;

        for (const item of items) {

            if (
                item.type.indexOf(
                    "image"
                ) !== -1
            ) {

                const file =
                    item.getAsFile();

                const reader =
                    new FileReader();

                reader.onload =
                    function (e) {

                    const container =
                        document.getElementById(
                            "attachmentContainer"
                        );

                    if (!container) return;

                    const div =
                        document.createElement(
                            "div"
                        );

                    div.className =
                        "attachment";

                    div.innerHTML = `

                        <div>

                            <div class="
                                attachment-name
                            ">
                                pasted-image.png
                            </div>

                            <div class="
                                attachment-size
                            ">
                                Screenshot
                            </div>

                            <img
                                src="${e.target.result}"
                                style="
                                    width:120px;
                                    margin-top:10px;
                                    border-radius:8px;
                                    border:1px solid #333;
                                "
                            >

                        </div>

                        <button class="
                            btn btn-dark
                        ">
                            Uploaded
                        </button>

                    `;

                    container.prepend(div);

                    console.log(
                        "✅ Screenshot Added"
                    );
                };

                reader.readAsDataURL(file);
            }
        }
    }
);
// ======================================
// 📎 HANDLE FILES
// ======================================

function handleFiles(event) {

    const files =
        event.target.files;

    const container =
        document.getElementById(
            "attachmentContainer"
        );

    if (!container) return;

    for (const file of files) {

        const div =
            document.createElement("div");

        div.className =
            "attachment";

        div.innerHTML = `

            <div>

                <div class="
                    attachment-name
                ">
                    ${file.name}
                </div>

                <div class="
                    attachment-size
                ">
                    ${Math.round(
                        file.size / 1024
                    )} KB
                </div>

            </div>

            <button class="
                btn btn-dark
            ">
                Uploaded
            </button>

        `;

        container.prepend(div);
    }

    console.log(
        "✅ Files Rendered"
    );
}

// ======================================
// ⏳ SLA DUE CALCULATION
// ======================================

function calculateSlaDue(i) {

    if (!i.createdAt) return "-";

    const created =
        new Date(i.createdAt);

    const slaHours =
        getSlaHours(i.priority);

    created.setHours(
        created.getHours() + slaHours
    );

    return formatFullDate(created);
}

// ======================================
// 🔢 SLA HOURS
// ======================================

function getSlaHours(priority) {

    const p =
        (priority || "")
            .toUpperCase();

    if (p === "HIGH") return 2;

    if (p === "MEDIUM") return 6;

    return 12;
}

// ======================================
// 📅 FORMAT DATE
// ======================================

function formatFullDate(date) {

    if (!date) return "-";

    try {

        return new Date(date)
            .toLocaleString(
                "en-IN",
                {
                    day: "2-digit",
                    month: "short",
                    year: "numeric",
                    hour: "2-digit",
                    minute: "2-digit"
                }
            );

    } catch {

        return "-";
    }
}

// ======================================
// 🎨 FORMAT STATUS
// ======================================

function formatStatus(status) {

    return (status || "")
        .replaceAll("_", " ");
}

// ======================================
// 📝 SAFE TEXT UPDATE
// ======================================

function setText(id, value) {

    const el =
        document.getElementById(id);

    if (el) {

        el.innerText = value;
    }
}

// ======================================
// 🚪 LOGOUT
// ======================================

function logout() {

    localStorage.removeItem("token");

    window.location.href =
        "/pages/login.html";
}

// ======================================
// 💬 COMMENT SYSTEM
// ======================================

const commentBtn =
    document.getElementById(
        "postCommentBtn"
    );

if (commentBtn) {

    commentBtn.addEventListener(
        "click",
        postComment
    );
}

function postComment() {

    const textarea =
        document.getElementById(
            "commentInput"
        );

    if (!textarea) return;

    const text =
        textarea.value.trim();

    if (!text) {

        alert(
            "Enter comment first"
        );

        return;
    }

    const timeline =
        document.getElementById(
            "timeline"
        );

    if (!timeline) return;

    const div =
        document.createElement("div");

    div.className =
        "timeline-item";

    div.innerHTML = `

        <div class="timeline-dot"></div>

        <div class="timeline-content">

            <div class="timeline-title">
                Comment added:
                ${text}
            </div>

            <div class="timeline-time">
                ${new Date()
                    .toLocaleString()}
            </div>

        </div>

    `;

    timeline.prepend(div);

    textarea.value = "";

    console.log(
        "✅ Comment Added"
    );
}
// ======================================
// 🚀 INIT
// ======================================

loadIncidentDetail();