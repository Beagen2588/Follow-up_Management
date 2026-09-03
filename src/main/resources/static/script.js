

fetch("api/auth/me")
    .then(response => response.json())
    .then(user => {
        if(user.role !== "ROLE_ADMIN"){
            document.getElementById("templatesLink").style.display = "none";
            document.getElementById("auditLink").style.display = "none";
        }
    })

let currentEmailTemplates = [];
let currentFollowUpRequests = null;
function loadFollowUpRequests(url) {

    fetch(url)
        .then(response => response.json())
        .then(data => {

            const tableBody = document.getElementById("followUpTableBody");

            // Clear the existing rows before displaying new results
            tableBody.innerHTML = "";

            data.sort((a,b) => {
                return new Date(a.dueDate) - new Date(b.dueDate);
            });

            data.forEach(followUpRequest => {

                if(followUpRequest.status !== "Pending"){
                    return;
                }
                const row = document.createElement("tr");

                const dueDate = new Date(followUpRequest.dueDate);
                const formattedDueDate = dueDate.toLocaleDateString();

                const today = new Date();

                dueDate.setHours(0, 0, 0, 0);
                today.setHours(0, 0, 0, 0);

                if (dueDate <= today) {
                    row.classList.add("overdue-follow-up");
                }



                row.innerHTML = `
                    <td>${followUpRequest.patientId.firstName} ${followUpRequest.patientId.lastName}</td>
                    <td>${followUpRequest.departmentId.name}</td>
                    <td>${followUpRequest.reason}</td>
                    <td>${formattedDueDate}</td>
                    <td>${followUpRequest.status}</td>
                    <td>${followUpRequest.notes}</td>
                    <td> <button onclick="viewFollowUp(${followUpRequest.id})">View</button>
                    <button onclick="cancelFollowUp(${followUpRequest.id})">Cancel</button></td>
                `;

                tableBody.appendChild(row);
            });

        })
        .catch(error => {
            console.error("Error loading follow-up requests:", error);
        });
}
loadFollowUpRequests("/api/follow-up-requests");


function viewFollowUp(id){
    fetch(`/api/follow-up-requests/${id}`)
        .then(response => response.json())
        .then(followUpRequest =>{
            currentFollowUpRequests = followUpRequest;

            document.getElementById("detailPatient").textContent =
                `${followUpRequest.patientId.firstName} ${followUpRequest.patientId.lastName}`;

            document.getElementById("detailDepartment").textContent =
                followUpRequest.departmentId.name;

            document.getElementById("detailReason").textContent =
                followUpRequest.reason;

            loadEmailTemplates(followUpRequest.departmentId.name);

            const dueDate = new Date(followUpRequest.dueDate);

            document.getElementById("detailDueDate").textContent =
                dueDate.toLocaleDateString();

            document.getElementById("detailStatus").textContent =
                followUpRequest.status;

            document.getElementById("detailNotes").textContent =
                followUpRequest.notes;

            document.getElementById("followUpDetails").style.display = "block";
        })
        .catch(error => {
            console.error("Error loading follow-up requests:", error);
        });

}

function loadEmailTemplates(departmentName){
    fetch(
        `/api/email-templates/search?departmentName=${encodeURIComponent(departmentName)}`
    )
        .then(response =>response.json())
        .then(data => {

            currentEmailTemplates = data;

            const templateSelect =
                document.getElementById("templateSelect")

            templateSelect.innerHTML=
                `<option value = "">Select a template</option>`;

            data.forEach(emailTemplate =>{

                const option = document.createElement("option");

                option.value = emailTemplate.id;
                option.textContent = emailTemplate.name;

                templateSelect.appendChild(option);
            });
        })
        .catch(error => {
        console.error("Error loading an email templates:", error);
    });
}

document.getElementById("templateSelect")
    .addEventListener("change", function() {

    const templateId = Number(this.value);

    if(!templateId) {
        document.getElementById("emailPreview").style.display = "none";
        return;
    }

    const selectedTemplate = currentEmailTemplates.find(
        template => template.id === templateId
    );
    console.log("Selected template object:", selectedTemplate);
    if(!selectedTemplate || !currentFollowUpRequests){
        return;

    }
    document.getElementById("previewRecipient").textContent =
        currentFollowUpRequests.patientId.email;

    document.getElementById("previewSubject").textContent =
        selectedTemplate.subjectTemplate;

        const patientName =
            currentFollowUpRequests.patientId.firstName;

        const previewBody =
            `Hello ${patientName},\n\n${selectedTemplate.bodyTemplate}`;

        document.getElementById("previewBody").textContent =
            previewBody;


    document.getElementById("emailPreview").style.display = "block";

});

document.getElementById("createCommunicationButton")
    .addEventListener("click", function(){

        const templateId =
            Number(document.getElementById("templateSelect").value);
        const selectedTemplate = currentEmailTemplates.find(template => template.id === templateId);

        if(!currentFollowUpRequests){
            return;
        }
        if(!templateId){
            alert("Please select and email template.");
            return;
        }

        const communicationData = {
            followUpId: currentFollowUpRequests.id,
            templateId: templateId,
            status: "Sent"
        };

       let mailtoLink;
        fetch("/api/communications", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(communicationData)
        })
            .then(response => {
                if(!response.ok){
                    throw new Error("Communication could not be created");
                }
                return response.json();
            })
            .then(data => {
                console.log("communication created:", data);

                mailtoLink =
                    `mailto:${encodeURIComponent(data.recipientEmail)}` +
                    `?subject=${encodeURIComponent(data.subject)}` +
                    `&body=${encodeURIComponent(data.body)}`;

                return fetch(`/api/follow-up-requests/${currentFollowUpRequests.id}/status?status=Completed`,
                    {method:"PUT"
            });

            })
            .then(response => {
                if(!response.ok){
                    throw new Error("Follow-up status could not be updated");
                }
                return response.json();
            })
            .then(updatedFollowUp =>{
                console.log("Follow-up updated", updatedFollowUp);
                window.location.href = mailtoLink;
                alert("Communication created successfully.");
                loadFollowUpRequests("/api/follow-up-requests");
            })
            .catch(error => {
                console.error("Error creating communication:", error);
            });

    });


document.getElementById("patientSearchButton")
    .addEventListener("click", function(){
        const patientLastName = document.getElementById("patientLastNameSearch").value;
        const patientBirthDate = document.getElementById("patientBirthDateSearch").value;
        loadPatient(patientLastName,patientBirthDate)
    })
function loadPatient(lastName, birthDate){
    fetch(`/api/patients/search/by-name-and-birthdate?lastName=${encodeURIComponent(lastName)}&birthDate=${encodeURIComponent(birthDate)}`)
        .then(response => response.json() )
        .then(data => {

            const patientSearchResults = document.getElementById("patientSearchResults");
            patientSearchResults.innerHTML = "";


            data.forEach(patient => {

                const row = document.createElement("tr");

                row.innerHTML = `
                <td>
                <input type="radio" name="selectedPatient" value="${patient.id}">
                </td>
                <td>${patient.firstName} ${patient.lastName}</td>
                <td>${patient.birthDate}</td>
                <td>${patient.phone} </td>
                `;
                patientSearchResults.appendChild(row);
            });
        })
            .catch(error => {
            console.error("Error retrieving Patient", error);
        });

}
document.getElementById("createFollowUpButton")
    .addEventListener("click", function(){

        const selectedPatient = document.querySelector(`input[name = "selectedPatient"]:checked`);
        const selectedDepartment = document.getElementById("departmentSelect").value;
        const reason = document.getElementById("followUpReason").value;
        const dueDate = document.getElementById("followUpDueDate").value;
        const status = "Pending";
        const notes = document.getElementById("followUpNotes").value;

        if(!selectedPatient){
            alert("please select a patient");
            return;
        }
        if(selectedDepartment === "") {
            alert("please select a department");
            return;
        }
        if(reason === ""){
            alert("please add a reason for the follow-up")
            return;
        }
        if(dueDate === ""){
            alert("please add a due date for the follow-up")
            return;
        }

        const patientId = Number(selectedPatient.value);
        const departmentId = Number(selectedDepartment);

        const followUpData = {
            patientId: patientId,
            departmentId: departmentId,
            reason: reason,
            dueDate: dueDate,
            status: status,
            notes: notes
        };
        fetch("/api/follow-up-requests", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(followUpData)
        })
            .then(response => {
                if(!response.ok){
                    throw new Error("Follow-up could not be created");
                }
                return response.json();
            })
            .then(data => {
                console.log("Follow-up created:", data);
                alert("Follow-up created successfully.");
                loadFollowUpRequests("/api/follow-up-requests")

                selectedPatient.checked = false;


                document.getElementById("patientLastNameSearch").value = "";
                document.getElementById("patientBirthDateSearch").value = "";
                document.getElementById("patientSearchResults").innerHTML = "";
                document.getElementById("departmentSelect").value = "";
                document.getElementById("followUpReason").value = "";
                document.getElementById("followUpDueDate").value = "";
                document.getElementById("followUpNotes").value = "";

                document.getElementById("createFollowUpSection").style.display ="none";
                document.getElementById("toggleCreateFollowUpButton").textContent = "+ Create New Follow-Up Request"

            })
            .catch(error => {
                console.error("Error creating Follow-up:", error);
            });


    })

function loadDepartments(){
    fetch(`/api/departments`)
        .then(response => response.json())
        .then(data => {

            const departmentSelect = document.getElementById("departmentSelect");
            departmentSelect.innerHTML = `<option value = "">Select a Department</option>`;

            data.forEach(department =>{
                const option = document.createElement("option");

                option.value = department.id;
                option.textContent = department.name;

                departmentSelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error("Error loading departments", error);
        });
}

function cancelFollowUp(followUpId){

   const confirmed = confirm("Are you sure you want to cancel this follow-up request?");

   if(!confirmed){
       return;
   }

    fetch(`/api/follow-up-requests/${followUpId}/status?status=Cancelled`,
    {
        method: "PUT"
    }
)
        .then(response =>{
            if(!response.ok){
                throw new Error("Follow-up could not be cancelled");
            }
           return response.json();
        })
        .then(updatedFollowUp =>{
            console.log("Follow-up cancelled: ", updatedFollowUp);

            alert("Follow-up cancelled successfully.");
            loadFollowUpRequests("/api/follow-up-requests");

        })
        .catch(error =>{
            console.error("Error cancelling Follow-up: ", error);
    });
}
document.getElementById("toggleCreateFollowUpButton")
    .addEventListener("click", function(){

        const createSection =
            document.getElementById("createFollowUpSection");

        if(createSection.style.display === "none"){

            createSection.style.display = "block";
            this.textContent = "− Hide New Follow-Up";

        } else {

            createSection.style.display = "none";
            this.textContent = "+ Create New Follow-Up";
        }
    });
loadDepartments();





