package com.AMiller.Follow_up_Management.Service;

import com.AMiller.Follow_up_Management.Repository.CommunicationRepository;
import com.AMiller.Follow_up_Management.Repository.DepartmentRepository;
import com.AMiller.Follow_up_Management.Repository.EmailTemplatesRepository;
import com.AMiller.Follow_up_Management.Repository.UsersRepository;
import com.AMiller.Follow_up_Management.entities.Department;
import com.AMiller.Follow_up_Management.entities.EmailTemplates;
import com.AMiller.Follow_up_Management.entities.Users;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

@Service
public class EmailTemplatesService {

    private final EmailTemplatesRepository emailTemplatesRepository;
    private final DepartmentRepository departmentRepository;
    private final CommunicationRepository communicationRepository;
    private final AuditLogsService auditLogsService;
    private final UsersRepository usersRepository;

    public EmailTemplatesService(EmailTemplatesRepository emailTemplatesRepository, DepartmentRepository departmentRepository,
                                 CommunicationRepository communicationRepository, AuditLogsService auditLogsService, UsersRepository usersRepository) {
        this.emailTemplatesRepository = emailTemplatesRepository;
        this.departmentRepository = departmentRepository;
        this.communicationRepository = communicationRepository;
        this.auditLogsService = auditLogsService;
        this.usersRepository = usersRepository;
    }

    public List<EmailTemplates> searchEmailTemplates(String departmentName) {
        return emailTemplatesRepository.findByDepartmentId_name(departmentName);
    }

    public List<EmailTemplates> getEmailTemplates() {
        return emailTemplatesRepository.findAll();
    }

    public EmailTemplates addEmailTemplate(int departmentId, String name, String subjectTemplate, String bodyTemplate,
                                           boolean active, String userName) {
        Optional<Department> departmentOptional = departmentRepository.findById(departmentId);
        Optional<Users> users = usersRepository.findByUserName(userName);

        if(users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found");

        }

        if (departmentOptional.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Department not found");
        }
        Department department = departmentOptional.get();
        EmailTemplates emailTemplates = new EmailTemplates();
        emailTemplates.setDepartmentId(department);
        emailTemplates.setName(name);
        emailTemplates.setSubjectTemplate(subjectTemplate);
        emailTemplates.setBodyTemplate(bodyTemplate);
        emailTemplates.setActive(active);
        EmailTemplates savedTemplate = emailTemplatesRepository.save(emailTemplates);
        auditLogsService.addAuditLog(users.get().getId(),"EmailTemplate", savedTemplate.getId(),"Create","Added EmailTemplate");
        return savedTemplate;
    }

    public EmailTemplates updateEmailTemplate(int id, int departmentId, String name, String subjectTemplate, String bodyTemplate,
                                              boolean active, String userName) {
        Optional<EmailTemplates> optionalEmailTemplates = emailTemplatesRepository.findById(id);
        Optional<Department> departmentOptional = departmentRepository.findById(departmentId);
        Optional<Users> users = usersRepository.findByUserName(userName);

        if(users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found");

        }
        if (departmentOptional.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Department not found");
        }
        if (optionalEmailTemplates.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Email templates not found");
        }
            Department department = departmentOptional.get();
            EmailTemplates existingTemplates = optionalEmailTemplates.get();
            existingTemplates.setDepartmentId(department);
            existingTemplates.setName(name);
            existingTemplates.setBodyTemplate(bodyTemplate);
            existingTemplates.setSubjectTemplate(subjectTemplate);
            existingTemplates.setActive(active);
            EmailTemplates savedTemplate = emailTemplatesRepository.save(existingTemplates);
            auditLogsService.addAuditLog(users.get().getId(),"EmailTemplate", savedTemplate.getId(),"Update","Updated EmailTemplate");
            return savedTemplate;


        }


    public void deleteEmailTemplate(int id, String userName) {
        Optional<Users> users = usersRepository.findByUserName(userName);

        if(users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found");

        }
        if (!emailTemplatesRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Email template not found"
            );
        }

        boolean templateInUse =
                communicationRepository.existsByTemplateId_Id(id);

        if (templateInUse) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Template is currently used by existing communications"
            );
        }

        emailTemplatesRepository.deleteById(id);
        auditLogsService.addAuditLog(users.get().getId(),"EmailTemplate", id,"Delete","Deleted EmailTemplate");
    }

    public EmailTemplates deactivateTemplate(int id,  String userName) {
        Optional<Users> users = usersRepository.findByUserName(userName);

        if(users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found");

        }

        Optional<EmailTemplates> optionalTemplate =
                emailTemplatesRepository.findById(id);

        if (optionalTemplate.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Email template not found"
            );
        }

        EmailTemplates template = optionalTemplate.get();

        template.setActive(false);

        EmailTemplates savedTemplate = emailTemplatesRepository.save(template);
        auditLogsService.addAuditLog(users.get().getId(),"EmailTemplate", savedTemplate.getId(),"Deactivated","Deactivated EmailTemplate");
        return savedTemplate;
    }

    public EmailTemplates activateTemplate(int id,  String userName) {
        Optional<Users> users = usersRepository.findByUserName(userName);

        if(users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found");

        }

        Optional<EmailTemplates> optionalTemplate =
                emailTemplatesRepository.findById(id);

        if (optionalTemplate.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Email template not found"
            );
        }

        EmailTemplates template = optionalTemplate.get();

        template.setActive(true);

        EmailTemplates savedTemplate = emailTemplatesRepository.save(template);
        auditLogsService.addAuditLog(users.get().getId(),"EmailTemplate", savedTemplate.getId(),"Reactivate","Reactivated EmailTemplate");
        return savedTemplate;
    }





}

