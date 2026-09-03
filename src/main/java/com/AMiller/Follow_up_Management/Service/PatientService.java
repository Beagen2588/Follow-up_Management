package com.AMiller.Follow_up_Management.Service;

import com.AMiller.Follow_up_Management.Repository.AuditLogsRepository;
import com.AMiller.Follow_up_Management.Repository.PatientRepository;
import com.AMiller.Follow_up_Management.Repository.UsersRepository;
import com.AMiller.Follow_up_Management.entities.AuditLogs;
import com.AMiller.Follow_up_Management.entities.Patient;
import com.AMiller.Follow_up_Management.entities.Users;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final  PatientRepository patientRepository;
    private final UsersRepository usersRepository;
    private final AuditLogsService auditLogsService;

    public PatientService(PatientRepository patientRepository, UsersRepository usersRepository,  AuditLogsService auditLogsService) {
        this.patientRepository = patientRepository;
        this.usersRepository = usersRepository;
        this.auditLogsService = auditLogsService;
    }

    public List<Patient> getAllPatients()
    {
        return patientRepository.findAll();
    }

    public Patient addNewPatient(Patient patient, String userName){

        Optional<Users> users = usersRepository.findByUserName(userName);
        if(users.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        boolean duplicateExists =
                patientRepository.existsByFirstNameAndLastNameAndBirthDateAndPhone(
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getBirthDate(),
                        patient.getPhone()
                );
        if (duplicateExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A patient with the same identifying information already exists"
            );
        }
        patient.setActive(true);
        Patient savedPatient = patientRepository.save(patient);
        auditLogsService.addAuditLog(users.get().getId(),"Patient", savedPatient.getId(), "Create","Patient added"
        );
        return savedPatient;

    }

    public Patient updatePatient(int id, Patient patient, String userName){
        Optional<Users> users = usersRepository.findByUserName(userName);
        if(users.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        Optional<Patient> existingPatient = patientRepository.findById(id);
        if(existingPatient.isPresent()){
             Patient existing = existingPatient.get();
             existing.setFirstName(patient.getFirstName());
             existing.setLastName(patient.getLastName());
             existing.setEmail(patient.getEmail());
             existing.setPhone(patient.getPhone());
             existing.setBirthDate(patient.getBirthDate());
             existing.setActive(patient.isActive());
             Patient savedPatient = patientRepository.save(existing);
             auditLogsService.addAuditLog(existing.getId(),"Patient", savedPatient.getId(), "Update","Patient updated");
             return savedPatient;
        }
        else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Patient not found"
            );
        }
    }

    public Patient deactivatePatient(int id, String userName) {

        Optional<Patient> existingPatient = patientRepository.findById(id);
        Optional<Users> users = usersRepository.findByUserName(userName);
        if(users.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (existingPatient.isPresent()) {

            Patient existing = existingPatient.get();

            existing.setActive(false);

            Patient savedPatient = patientRepository.save(existing);
            auditLogsService.addAuditLog(users.get().getId(),"Patient", savedPatient.getId(), "Deactivate","Patient deactivated");
            return savedPatient;
        }
        else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Patient not found"
            );
        }
    }

    public List<Patient> searchPatient(String lastName) {
        return patientRepository.findByLastNameAndActiveTrue(lastName);


    }

    public List<Patient> searchByLastNameBirthDate(String lastName, LocalDate birthDate) {
        return patientRepository.findByLastNameAndBirthDateAndActiveTrue(lastName, birthDate);
    }

    public Patient getPatientById(int id) {
        Optional<Patient>  existingPatient = patientRepository.findById(id);
        if(existingPatient.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found");
        }
        return existingPatient.get();
    }
}
