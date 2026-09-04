let currentPage = 0;
const pageSize = 20;
let baseAuditUrl = "/api/audit-logs"
let currentAuditUrl = baseAuditUrl;
function loadAuditLogs(url) {

    fetch(url)
        .then(response => {

            if (!response.ok) {
                throw new Error("Audit logs could not be loaded.");
            }

            return response.json();
        })
        .then(data => {

            document.getElementById("reportGeneratedAt").textContent =
                new Date().toLocaleString();

            const auditLogResults =
                document.getElementById("auditLogResults");

            auditLogResults.innerHTML = "";

                let logs;
                logs = data.content;
                currentPage = data.number;
                updatePagination(data);

            logs.forEach(log =>{
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
function updatePagination(data) {

    document.getElementById("pageInformation").textContent =
        `Page ${data.number + 1} of ${data.totalPages}`;

    document.getElementById("previousPageButton").disabled =
        data.first;

    document.getElementById("nextPageButton").disabled =
        data.last;
}
document.getElementById("previousPageButton")
    .addEventListener("click", function () {

        if (currentPage > 0) {

            const separator = currentAuditUrl.includes("?") ? "&" : "?";
            loadAuditLogs(
                `${currentAuditUrl}${separator}page=${currentPage - 1}&size=${pageSize}`
            );
        }
    });


document.getElementById("nextPageButton")
    .addEventListener("click", function () {

        const separator = currentAuditUrl.includes("?") ? "&" : "?";
        loadAuditLogs(
            `${currentAuditUrl}${separator}page=${currentPage + 1}&size=${pageSize}`

        );
    });





document.getElementById("searchAuditByUserButton")
    .addEventListener("click", function() {

        const userName =
            document.getElementById("auditUserSearch").value;

        if (userName === "") {
            alert("Please enter a user name.");
            return;
        }
        currentPage = 0;

        currentAuditUrl = `${baseAuditUrl}/search/user?userName=${encodeURIComponent(userName)}`

        loadAuditLogs(
            `${currentAuditUrl}&page=${currentPage}&size=${pageSize}`
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
        currentPage = 0;


        currentAuditUrl = `${baseAuditUrl}/search/action?action=${encodeURIComponent(action)}`


        loadAuditLogs(
            `${currentAuditUrl}&page=${currentPage}&size=${pageSize}`
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

        currentPage = 0;

        currentAuditUrl = `${baseAuditUrl}/search/date?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`

        loadAuditLogs(
            `${currentAuditUrl}&page=${currentPage}&size=${pageSize}`
        );
    });



document.getElementById("showAllAuditLogsButton")
    .addEventListener("click", function() {

        document.getElementById("auditUserSearch").value = "";
        document.getElementById("auditActionSearch").value = "";
        document.getElementById("auditStartDate").value = "";
        document.getElementById("auditEndDate").value = "";


        currentPage = 0;
        currentAuditUrl = baseAuditUrl;

        loadAuditLogs(
            `${currentAuditUrl}?page=${currentPage}&size=${pageSize}`
        );
    });

document.getElementById("clearSearchButton")
    .addEventListener("click", function () {

        document.getElementById("auditUserSearch").value = "";
        document.getElementById("auditActionSearch").value = "";
        document.getElementById("auditStartDate").value = "";
        document.getElementById("auditEndDate").value = "";

        currentPage = 0;
        currentAuditUrl = baseAuditUrl;

        loadAuditLogs(
            `${currentAuditUrl}?page=${currentPage}&size=${pageSize}`
        );
    });