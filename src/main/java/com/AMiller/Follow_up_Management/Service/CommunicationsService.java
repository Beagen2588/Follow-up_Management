package com.AMiller.Follow_up_Management.Service;

import com.AMiller.Follow_up_Management.Repository.CommunicationRepository;
import com.AMiller.Follow_up_Management.Repository.EmailTemplatesRepository;
import com.AMiller.Follow_up_Management.Repository.FollowUpRequestsRepository;
import com.AMiller.Follow_up_Management.Repository.UsersRepository;
import com.AMiller.Follow_up_Management.entities.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommunicationsService {

    private final CommunicationRepository communicationRepository;
    private final FollowUpRequestsRepository followUpRequestsRepository;
    private final EmailTemplatesRepository emailTemplatesRepository;
    private final UsersRepository usersRepository;
    private final AuditLogsService auditLogsService;
    private final CommunicationFormatter communicationFormatter;


    public CommunicationsService(CommunicationRepository communicationRepository, FollowUpRequestsRepository followUpRequestsRepository,
                                 EmailTemplatesRepository emailTemplatesRepository, UsersRepository usersRepository, AuditLogsService auditLogsService,
                                 CommunicationFormatter communicationFormatter) {
        this.communicationRepository = communicationRepository;
        this.followUpRequestsRepository = followUpRequestsRepository;
        this.emailTemplatesRepository = emailTemplatesRepository;
        this.usersRepository = usersRepository;
        this.auditLogsService = auditLogsService;
        this.communicationFormatter = communicationFormatter;
    }
    public List<Communication> findAllCommunications(){
        return communicationRepository.findAll();
    }

    public Communication addCommunication(int followUpRequestId, int templateId, String userName, String status) {
        Optional<FollowUpRequests> optionalFollowUpRequests = followUpRequestsRepository.findById(followUpRequestId);
        Optional<EmailTemplates> optionalEmailTemplates = emailTemplatesRepository.findById(templateId);
        Optional<Users> optionalUsers = usersRepository.findByUserName(userName);

        if(optionalUsers.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found!"
            );

        }
        if(optionalFollowUpRequests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Follow up requests not found!"
            );

        }
        if(optionalEmailTemplates.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Email templates not found!"
            );

        }


        Users user = optionalUsers.get();
        FollowUpRequests followUpRequests = optionalFollowUpRequests.get();
        EmailTemplates emailTemplates = optionalEmailTemplates.get();
        String patientName = followUpRequests.getPatientId().getFirstName();
        String formattedBody = communicationFormatter.formatMessage(patientName,emailTemplates.getBodyTemplate());
        Communication communication = new Communication();
        communication.setFollowUpId(followUpRequests);
        communication.setTemplateId(emailTemplates);
        communication.setCreatedBy(user);
        communication.setRecipientEmail(
                followUpRequests.getPatientId().getEmail()
        );
        communication.setSubject(emailTemplates.getSubjectTemplate());
        communication.setStatus(status);
        communication.setBody(formattedBody);
        communication.setCreatedAt(LocalDateTime.now());
        Communication savedCommunication = communicationRepository.save(communication);

        auditLogsService.addAuditLog(
                user.getId(),
                "Communication",
                savedCommunication.getId(),
                "Create",
                "Communication Created"
        );
        return savedCommunication;
    }

    public Communication updateCommunication(int communicationId, int followUpRequestId, int templateId, String userName,String status){
        Optional<Communication> communication = communicationRepository.findById(communicationId);
        Optional<FollowUpRequests> optionalFollowUpRequests = followUpRequestsRepository.findById(followUpRequestId);
        Optional<EmailTemplates> optionalEmailTemplates = emailTemplatesRepository.findById(templateId);
        Optional<Users> optionalUsers = usersRepository.findByUserName(userName);
        if(optionalUsers.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found!"
            );

        }

        if(communication.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Communication not found!"
            );
        }

        if(optionalFollowUpRequests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "follow up requests not found!"
            );

        }
        if(optionalEmailTemplates.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Template not found!"
            );

        }

        Users user = optionalUsers.get();
        Communication communication1 = communication.get();
        FollowUpRequests followUpRequests = optionalFollowUpRequests.get();
        EmailTemplates emailTemplates = optionalEmailTemplates.get();
        String patientName = followUpRequests.getPatientId().getFirstName();
        String formattedBody = communicationFormatter.formatMessage(patientName,emailTemplates.getBodyTemplate());

        communication1.setFollowUpId(followUpRequests);
        communication1.setTemplateId(emailTemplates);
        communication1.setCreatedBy(user);
        communication1.setRecipientEmail(followUpRequests.getPatientId().getEmail());
        communication1.setSubject(emailTemplates.getSubjectTemplate());
        communication1.setBody(formattedBody);
        communication1.setStatus(status);
        communication1.setCreatedAt(LocalDateTime.now());
        Communication savedCommunication = communicationRepository.save(communication1);

        auditLogsService.addAuditLog(
                user.getId(),
                "Communication",
                savedCommunication.getId(),
                "Update",
                "Communication Updated"
        );
        return savedCommunication;

    }

    public void deleteCommunication(int communicationId, String userName) {

        Optional<Users> users = usersRepository.findByUserName(userName);

        if(users.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );

        }
        Optional<Communication> communication = communicationRepository.findById(communicationId);

        if(communication.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Communication not found"
            );
        }
        communicationRepository.delete(communication.get());
        auditLogsService.addAuditLog(
                users.get().getId(),
                "Communication",
                communication.get().getId(),
                "Delete",
                "Communication Deleted"
        );


    }

    public List<Communication> findByPatientName(String patientLastName){
        return communicationRepository.findByFollowUpId_PatientId_LastName(patientLastName);
    }




}
