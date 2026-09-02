package com.AMiller.Follow_up_Management.Service;

import com.AMiller.Follow_up_Management.Repository.*;
import com.AMiller.Follow_up_Management.entities.*;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FollowUpRequestsService {

    private final FollowUpRequestsRepository followUpRequestsRepository;
    private final DepartmentRepository departmentRepository;
    private final PatientRepository patientRepository;
    private final AuditLogsService auditLogsService;
    private final UsersRepository usersRepository;

    public FollowUpRequestsService(FollowUpRequestsRepository followUpRequestsRepository, DepartmentRepository departmentRepository
            , PatientRepository patientRepository, AuditLogsService auditLogsService, UsersRepository usersRepository) {
        this.followUpRequestsRepository = followUpRequestsRepository;
        this.departmentRepository = departmentRepository;
        this.patientRepository = patientRepository;
        this.auditLogsService  = auditLogsService;
        this.usersRepository = usersRepository;
    }

    public List<FollowUpRequests> getAllFollowUpRequests() {
        return followUpRequestsRepository.findAll();
    }

    public FollowUpRequests addFollowUpRequest(String userName,int patientId, int departmentId, String reason,
                                                Date dueDate, String status, String notes) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);
        Optional<Users> users = usersRepository.findByUserName(userName);

        if (users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        if(optionalPatient.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Patient not found"
            );

        }
        if(optionalDepartment.isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Department not found"
            );

        }

            Patient patient = optionalPatient.get();
            Department department = optionalDepartment.get();
            FollowUpRequests followUpRequests = new FollowUpRequests();
            followUpRequests.setPatientId(patient);
            followUpRequests.setDepartmentId(department);
            followUpRequests.setReason(reason);
            followUpRequests.setDueDate(dueDate);
            followUpRequests.setStatus(status);
            followUpRequests.setNotes(notes);
            followUpRequests.setCreatedAt(LocalDateTime.now());
            FollowUpRequests savedRequest = followUpRequestsRepository.save(followUpRequests);
            auditLogsService.addAuditLog(users.get().getId(),"FollowupRequest", savedRequest.getId(),"Create","Follow-up request added"
                    );
            return savedRequest;
    }

   public FollowUpRequests updateFollowUpRequest(int followUpRequestId, String userName,int patientId, int departmentId, String reason,
                                                 Date dueDate, String status, String notes) {
        Optional<FollowUpRequests> followUpRequests = followUpRequestsRepository.findById(followUpRequestId);
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);
        Optional<Users> users = usersRepository.findByUserName(userName);

        if (users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        if(followUpRequests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Follow-up-Appointment not found"
            );
        }

        if(optionalPatient.isEmpty()) {
           throw new ResponseStatusException(
                   HttpStatus.NOT_FOUND,
                   "Patient not found"
           );

       }
       if(optionalDepartment.isEmpty()){
           throw new ResponseStatusException(
                   HttpStatus.NOT_FOUND,
                   "Department not found"
           );

       }

            FollowUpRequests followUpRequest = followUpRequests.get();
            followUpRequest.setPatientId(optionalPatient.get());
            followUpRequest.setDepartmentId(optionalDepartment.get());
            followUpRequest.setReason(reason);
            followUpRequest.setDueDate(dueDate);
            followUpRequest.setStatus(status);
            followUpRequest.setNotes(notes);
            FollowUpRequests savedRequest =  followUpRequestsRepository.save(followUpRequest);
            auditLogsService.addAuditLog(users.get().getId(),"FollowUpRequest", savedRequest.getId(), "Update","Follow-Up-Request updated");
            return savedRequest;
    }

    public void deleteFollowUpRequest(int followUpRequestId, String userName) {

        Optional<FollowUpRequests> followUpRequests = followUpRequestsRepository.findById(followUpRequestId);
        Optional<Users> users = usersRepository.findByUserName(userName);
        if (users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

            if(followUpRequests.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Follow-up-Appointment not found"
                );
            }
            followUpRequestsRepository.delete(followUpRequests.get());
            auditLogsService.addAuditLog(users.get().getId(),"FollowUpRequest", followUpRequestId, "Delete","Follow-Up-Request Deleted");

    }
    public List<FollowUpRequests> searchFollowUpRequests(String status){
        return followUpRequestsRepository.findByStatus(status);
    }

    public FollowUpRequests findById(int followUpRequestId){
        Optional<FollowUpRequests> followUpRequest =  followUpRequestsRepository.findById(followUpRequestId);
        if(followUpRequest.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Follow-up-Appointment not found"
            );
        }
        return followUpRequest.get();
    }

    public FollowUpRequests updateStatus(int followUpRequestId, String userName, String status){

        Optional<Users> users = usersRepository.findByUserName(userName);

                if(users.isEmpty()) {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User not found"
                    );
                }
        FollowUpRequests followUpRequest = followUpRequestsRepository.findById(followUpRequestId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow-up-Appointment not found")
                );
        followUpRequest.setStatus(status);
        FollowUpRequests savedRequest = followUpRequestsRepository.save(followUpRequest);

        auditLogsService.addAuditLog(
                users.get().getId(),
                "FollowUpRequest",
                savedRequest.getId(),
                "Update",
                "Follow-Up-Request status changed to " + status);
        return savedRequest;

    }
}
