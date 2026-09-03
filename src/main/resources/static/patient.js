let editingPatientId = null;
document.getElementById("showAddPatientButton")
    .addEventListener("click", function() {
        editingPatientId = null;
        document.getElementById("addPatientForm").style.display = "block";
    });


document.getElementById("cancelAddPatient")
    .addEventListener("click", function(){
            clearFormFields();

    });

document.getElementById("savePatientButton")
    .addEventListener("click", function(){
        const patientFirstName = document.getElementById("patientFirstName").value;
        const patientLastName = document.getElementById("patientLastName").value;
        const patientBirthDate = document.getElementById("patientBirthDate").value;
        const patientEmail = document.getElementById("patientEmail").value;
        const patientPhone = document.getElementById("patientPhone").value;

        if (patientFirstName === "") {
            alert("Please enter the patients first name")
            return;
        }
        if (patientLastName === "") {
            alert("Please enter the patients Last name")
            return;
        }
        if (patientBirthDate === "") {
            alert("Please enter the patients Birth Date")
            return;
        }
        const birthDate = new Date(patientBirthDate);
        const today = new Date();

        if (birthDate > today){
            alert("Patient birth date cannot be in the future. Please enter a valid birth date");
            return
        }
        if (patientEmail === "") {
            alert("Please enter the patients email name")
            return;
        }
        if(!patientEmail.includes("@")|| !patientEmail.includes(".")){
            alert("Please enter a valid email address");
            return
        }
        if (patientPhone === "") {
            alert("Please enter the patients phone number")
            return;
        }
        const phonePattern = /^\d{3}-\d{3}-\d{4}$/;
        if(!phonePattern.test(patientPhone)){
            alert("Please enter the phone number in the format of 555-123-4567")
            return;
        }


        const patientData = {
            firstName: patientFirstName,
            lastName: patientLastName,
            birthDate: patientBirthDate,
            email: patientEmail,
            phone: patientPhone,

        };
        if(editingPatientId === null) {
            fetch("/api/patients", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(patientData)
            })
                .then(response => {
                    if(response.status === 409){
                        throw new Error(
                            "A patient with the same identifying information already exists."
                        );
                    }
                    if (!response.ok) {
                        throw new Error("Patient could not be created");
                    }
                    return response.json();
                })
                .then(data => {
                    console.log("patient created:", data);
                    alert("patient created successfully.");
                    clearFormFields();
                })
                .catch(error => {
                    console.error("Error creating patient:", error);
                });
        }
        else{
            fetch(`/api/patients/${editingPatientId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(patientData)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Patient could not be created");
                    }
                    return response.json();
                })
                .then(data => {
                    console.log("patient updated:", data);
                    alert("patient updated successfully.");
                    clearFormFields();
                })
                .catch(error => {
                    console.error("Error updating patient:", error);
                });

        }

    });

function clearFormFields(){
    document.getElementById("addPatientForm").style.display = "none";
    document.getElementById("patientFirstName").value = "";
    document.getElementById("patientLastName").value = "";
    document.getElementById("patientBirthDate").value = "";
    document.getElementById("patientEmail").value = "";
    document.getElementById("patientPhone").value = "";

}
document.getElementById("SearchButton")
    .addEventListener("click", function(){
        const patientLastName = document.getElementById("LastNameSearch").value;
        const patientBirthDate = document.getElementById("BirthDateSearch").value;
        loadPatients(patientLastName,patientBirthDate)
    })
function loadPatients(lastName, birthDate){
    fetch(`/api/patients/search/by-name-and-birthdate?lastName=${encodeURIComponent(lastName)}&birthDate=${encodeURIComponent(birthDate)}`)
        .then(response => response.json() )
        .then(data => {

            const patientSearchResults = document.getElementById("patientSearchResults");
            patientSearchResults.innerHTML = "";


            data.forEach(patient => {

                const row = document.createElement("tr");

                row.innerHTML = `
                <td>${patient.firstName} ${patient.lastName}</td>
                <td>${patient.birthDate}</td>
                <td>${patient.phone} </td>
                <td>${patient.email}</td>
                <td>
                <button type = "button" onclick="editPatient(${patient.id})">Edit</button>
                <button type = "button" onclick="deactivatePatient(${patient.id})">Delete</button>
                </td>
                `;
                patientSearchResults.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error retrieving Patient", error);
        });

}
function editPatient(id){
    editingPatientId = id;
    fetch(`/api/patients/${id}`)
        .then(response => response.json())
        .then(data => {
           console.log("Patient returned by ID:", data);
            document.getElementById("addPatientForm").style.display = "block";
            document.getElementById("patientFirstName").value = data.firstName;
            document.getElementById("patientLastName").value = data.lastName;
            document.getElementById("patientBirthDate").value = data.birthDate;
            document.getElementById("patientEmail").value = data.email;
            document.getElementById("patientPhone").value = data.phone;
        })
}
function deactivatePatient(id){
    const confirmed =
        confirm("Are you sure you want to deactivate this patient?");

    if (!confirmed) {
        return;
    }

    fetch(`/api/patients/${id}/deactivate`, {
        method: "PUT"
    })
        .then(response => {

            if (!response.ok) {
                throw new Error("Patient could not be deactivated.");
            }

            return response.json();
        })
        .then(data => {

            console.log("Patient deactivated:", data);
            alert("Patient deactivated successfully.");

            const lastName =
                document.getElementById("LastNameSearch").value;

            const birthDate =
                document.getElementById("BirthDateSearch").value;

            loadPatients(lastName, birthDate);
        })
        .catch(error => {
            console.error("Error deactivating patient:", error);
            alert(error.message);
        });
}
