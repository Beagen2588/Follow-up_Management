function loadAuditLogs(url) {

    fetch(url)
        .then(response => {

            if (!response.ok) {
                throw new Error("Audit logs could not be loaded.");
            }

            return response.json();
        })
        .then(data => {

            document.getElementById("reportGeneratedAt").textContent = new Date().toLocaleString();

            const auditLogResults =
                document.getElementById("auditLogResults");

            auditLogResults.innerHTML = "";

            data.forEach(log => {

                const row = document.createElement("tr");

                const createdAt =
                    new Date(log.createdAt).toLocaleString();

                row.innerHTML = `
                    <td>${log.userId.userName}</td>
                    <td>${log.action}</td>
                    <td>${log.entityType}</td>
                    <td>${log.entityId}</td>
                    <td>${log.reason}</td>
                    <td>${createdAt}</td>
                `;

                auditLogResults.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error loading audit logs:", error);
            alert(error.message);
        });
}





document.getElementById("searchAuditByUserButton")
    .addEventListener("click", function() {

        const userName =
            document.getElementById("auditUserSearch").value;

        if (userName === "") {
            alert("Please enter a user name.");
            return;
        }

        loadAuditLogs(
            `/api/audit-logs/search/user?userName=${encodeURIComponent(userName)}`
        );
    });

document.getElementById("searchAuditByActionButton")
    .addEventListener("click", function() {

        const action =
            document.getElementById("auditActionSearch").value;

        if (action === "") {
            alert("Please select an action.");
            return;
        }

        loadAuditLogs(
            `/api/audit-logs/search/action?action=${encodeURIComponent(action)}`
        );
    });

document.getElementById("searchAuditByDateButton")
    .addEventListener("click", function() {

        const startDate =
            document.getElementById("auditStartDate").value;

        const endDate =
            document.getElementById("auditEndDate").value;

        if (startDate === "" || endDate === "") {
            alert("Please select both a start and end date.");
            return;
        }
        const start = startDate + "T00:00:00";
        const end = endDate + "T23:59:59"

        loadAuditLogs(
            `/api/audit-logs/search/date?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`
        );
    });



document.getElementById("showAllAuditLogsButton")
    .addEventListener("click", function() {

        document.getElementById("auditUserSearch").value = "";
        document.getElementById("auditActionSearch").value = "";
        document.getElementById("auditStartDate").value = "";
        document.getElementById("auditEndDate").value = "";

        loadAuditLogs("/api/audit-logs");
    });