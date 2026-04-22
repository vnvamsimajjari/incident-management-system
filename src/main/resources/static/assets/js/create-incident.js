const API_URL = "http://localhost:8080/api";

document.getElementById("incidentForm")
  .addEventListener("submit", async function (e) {

    e.preventDefault();

    const incident = {
      title: document.getElementById("title").value,
      description: document.getElementById("description").value,
      priority: document.getElementById("priority").value,

      // 🔥 IMPORTANT: must be username
      assignedTo: document.getElementById("assignedTo").value,

      slaMinutes: Number(document.getElementById("slaMinutes").value)
    };

    console.log("SENDING:", incident);

    try {
      const res = await fetch(`${API_URL}/incidents`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(incident)
      });

      console.log("STATUS:", res.status);

      if (!res.ok) {
        const errorText = await res.text();
        console.error("ERROR:", errorText);
        alert("Error creating incident: " + errorText);
        return;
      }

      alert("Incident created successfully ✅");

      // Redirect
      window.location.href = "/pages/incidents.html";

    } catch (err) {
      console.error("FINAL ERROR:", err);
      alert("Something went wrong ❌");
    }

});