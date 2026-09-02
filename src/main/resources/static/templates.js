let editingTemplateId = null;

function loadDepartments() {

    fetch("/api/departments")
        .then(response => {

            if (!response.ok) {
                throw new Error("Departments could not be loaded.");
            }

            return response.json();
        })
        .then(data => {

            const templateDepartment =
                document.getElementById("templateDepartment");

            const searchDepartment =
                document.getElementById("searchDepartment");


            templateDepartment.innerHTML =
                `<option value="">Select Department</option>`;

            searchDepartment.innerHTML =
                `<option value="">Select Department</option>`;


            data.forEach(department => {

                // Add/Edit dropdown
                const formOption =
                    document.createElement("option");

                formOption.value = department.id;
                formOption.textContent = department.name;

                templateDepartment.appendChild(formOption);


                // Search dropdown
                const searchOption =
                    document.createElement("option");

                searchOption.value = department.name;
                searchOption.textContent = department.name;

                searchDepartment.appendChild(searchOption);
            });
        })
        .catch(error => {
            console.error("Error loading departments:", error);
        });
}


loadDepartments();


function loadTemplates(url) {

    fetch(url)
        .then(response => {

            if (!response.ok) {
                throw new Error("Email templates could not be loaded.");
            }

            return response.json();
        })
        .then(data => {

            const templateResults =
                document.getElementById("templateResults");

            templateResults.innerHTML = "";


            data.forEach(template => {

                const row =
                    document.createElement("tr");


                row.innerHTML = `
                    <td>${template.name}</td>

                    <td>${template.departmentId.name}</td>

                    <td>${template.subjectTemplate}</td>

                    <td>
                        ${template.active ? "Active" : "Inactive"}
                    </td>

                    <td>

                        <button
                            type="button"
                            onclick="editTemplate(${template.id})">
                            Edit
                        </button>

                        <button
                            type="button"
                            onclick="deactivateTemplate(${template.id})">
                            Deactivate
                        </button>

                        <button
                            type="button"
                            onclick="deleteTemplate(${template.id})">
                            Delete
                        </button>

                    </td>
                `;


                templateResults.appendChild(row);
            });
        })
        .catch(error => {
            console.error("Error loading templates:", error);
        });
}


loadTemplates("/api/email-templates");


document.getElementById("showAddTemplateButton")
    .addEventListener("click", function () {

        editingTemplateId = null;

        clearTemplateForm();

        document.getElementById("templateFormTitle")
            .textContent = "Add Email Template";

        document.getElementById("templateForm")
            .style.display = "block";
    });


document.getElementById("cancelTemplateButton")
    .addEventListener("click", function () {

        clearTemplateForm();

        document.getElementById("templateForm")
            .style.display = "none";

        editingTemplateId = null;
    });


document.getElementById("saveTemplateButton")
    .addEventListener("click", function () {

        const departmentId =
            Number(
                document.getElementById("templateDepartment").value
            );

        const name =
            document.getElementById("templateName").value;

        const subjectTemplate =
            document.getElementById("templateSubject").value;

        const bodyTemplate =
            document.getElementById("templateBody").value;

        const active =
            document.getElementById("templateActive").value === "true";


        if (!departmentId) {
            alert("Please select a department.");
            return;
        }

        if (name === "") {
            alert("Please enter a template name.");
            return;
        }

        if (subjectTemplate === "") {
            alert("Please enter a subject.");
            return;
        }

        if (bodyTemplate === "") {
            alert("Please enter a template body.");
            return;
        }


        const templateData = {
            departmentId: departmentId,
            name: name,
            subjectTemplate: subjectTemplate,
            bodyTemplate: bodyTemplate,
            active: active
        };


        let url;
        let method;


        if (editingTemplateId === null) {

            url = "/api/email-templates";
            method = "POST";

        } else {

            url = `/api/email-templates/${editingTemplateId}`;
            method = "PUT";
        }


        fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(templateData)
        })
            .then(response => {

                if (!response.ok) {
                    throw new Error(
                        "Email template could not be saved."
                    );
                }

                return response.json();
            })
            .then(data => {

                console.log("Template saved:", data);

                alert("Email template saved successfully.");

                clearTemplateForm();

                document.getElementById("templateForm")
                    .style.display = "none";

                editingTemplateId = null;

                loadTemplates("/api/email-templates");
            })
            .catch(error => {

                console.error(
                    "Error saving email template:",
                    error
                );

                alert(error.message);
            });
    });


function editTemplate(id) {

    fetch("/api/email-templates")
        .then(response => {

            if (!response.ok) {
                throw new Error("Templates could not be loaded.");
            }

            return response.json();
        })
        .then(data => {

            const template =
                data.find(template => template.id === id);


            if (!template) {
                alert("Email template could not be found.");
                return;
            }


            editingTemplateId = id;


            document.getElementById("templateForm")
                .style.display = "block";


            document.getElementById("templateFormTitle")
                .textContent = "Edit Email Template";


            document.getElementById("templateDepartment").value =
                template.departmentId.id;

            document.getElementById("templateName").value =
                template.name;

            document.getElementById("templateSubject").value =
                template.subjectTemplate;

            document.getElementById("templateBody").value =
                template.bodyTemplate;

            document.getElementById("templateActive").value =
                String(template.active);
        })
        .catch(error => {

            console.error(
                "Error retrieving email template:",
                error
            );
        });
}


function deactivateTemplate(id) {

    const confirmed =
        confirm(
            "Are you sure you want to deactivate this email template?"
        );


    if (!confirmed) {
        return;
    }


    fetch(`/api/email-templates/${id}/deactivate`, {
        method: "PUT"
    })
        .then(response => {

            if (!response.ok) {
                throw new Error(
                    "Email template could not be deactivated."
                );
            }

            return response.json();
        })
        .then(data => {

            console.log(
                "Template deactivated:",
                data
            );

            alert(
                "Email template deactivated successfully."
            );

            loadTemplates("/api/email-templates");
        })
        .catch(error => {

            console.error(
                "Error deactivating template:",
                error
            );

            alert(error.message);
        });
}


function deleteTemplate(id) {

    const confirmed =
        confirm(
            "Are you sure you want to permanently delete this email template?"
        );


    if (!confirmed) {
        return;
    }


    fetch(`/api/email-templates/${id}`, {
        method: "DELETE"
    })
        .then(response => {

            if (response.status === 409) {

                throw new Error(
                    "This template cannot be deleted because it is already being used by existing communications. Deactivate it instead."
                );
            }


            if (!response.ok) {

                throw new Error(
                    "Email template could not be deleted."
                );
            }


            alert(
                "Email template deleted successfully."
            );


            loadTemplates("/api/email-templates");
        })
        .catch(error => {

            console.error(
                "Error deleting email template:",
                error
            );

            alert(error.message);
        });
}


document.getElementById("searchTemplateButton")
    .addEventListener("click", function () {

        const departmentName =
            document.getElementById("searchDepartment").value;


        if (departmentName === "") {

            alert("Please select a department.");

            return;
        }


        loadTemplates(
            `/api/email-templates/search?departmentName=${encodeURIComponent(departmentName)}`
        );
    });


document.getElementById("showAllTemplatesButton")
    .addEventListener("click", function () {

        document.getElementById("searchDepartment").value = "";

        loadTemplates("/api/email-templates");
    });


function clearTemplateForm() {

    document.getElementById("templateDepartment").value = "";

    document.getElementById("templateName").value = "";

    document.getElementById("templateSubject").value = "";

    document.getElementById("templateBody").value = "";

    document.getElementById("templateActive").value = "true";
}