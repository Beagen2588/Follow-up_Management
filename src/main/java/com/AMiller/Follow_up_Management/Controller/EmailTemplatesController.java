package com.AMiller.Follow_up_Management.Controller;

import com.AMiller.Follow_up_Management.DTO.EmailTemplatesDTO;
import com.AMiller.Follow_up_Management.Repository.EmailTemplatesRepository;
import com.AMiller.Follow_up_Management.Service.EmailTemplatesService;
import com.AMiller.Follow_up_Management.entities.EmailTemplates;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email-templates")
public class EmailTemplatesController {

    private final EmailTemplatesService emailTemplatesService;
    public  EmailTemplatesController(EmailTemplatesService emailTemplatesService) {
        this.emailTemplatesService = emailTemplatesService;
    }

    @GetMapping
    public List<EmailTemplates> getAllEmailTemplates() {
        return emailTemplatesService.getEmailTemplates();
    }

    @PostMapping
    public EmailTemplates addEmailTemplate(@RequestBody EmailTemplatesDTO emailTemplatesDTO, Authentication authentication) {

        return emailTemplatesService.addEmailTemplate(emailTemplatesDTO.getDepartmentId(), emailTemplatesDTO.getName(),
                emailTemplatesDTO.getSubjectTemplate(), emailTemplatesDTO.getBodyTemplate(), emailTemplatesDTO.isActive(), authentication.getName());
    }

    @PutMapping("/{id}")
    public EmailTemplates updateEmailTemplate(@PathVariable int id, @RequestBody EmailTemplatesDTO emailTemplatesDTO, Authentication authentication) {

        return emailTemplatesService.updateEmailTemplate(id,emailTemplatesDTO.getDepartmentId(), emailTemplatesDTO.getName(),
                                                            emailTemplatesDTO.getSubjectTemplate(),emailTemplatesDTO.getBodyTemplate(),
                                                            emailTemplatesDTO.isActive(), authentication.getName());

    }

    @DeleteMapping("/{id}")
    public void deleteEmailTemplate(@PathVariable int id, Authentication authentication) {

        emailTemplatesService.deleteEmailTemplate(id,  authentication.getName());

    }

    @PutMapping("/{id}/deactivate")
    public EmailTemplates deactivateTemplate(@PathVariable int id, Authentication authentication) {

        return emailTemplatesService.deactivateTemplate(id,authentication.getName());
    }

    @GetMapping("/search")
    public List<EmailTemplates> searchEmailTemplates(@RequestParam String departmentName) {

        return emailTemplatesService.searchEmailTemplates(departmentName);
    }

}
