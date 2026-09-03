package com.AMiller.Follow_up_Management.Controller;

import com.AMiller.Follow_up_Management.Service.PatientService;
import com.AMiller.Follow_up_Management.entities.AuditLogs;
import com.AMiller.Follow_up_Management.entities.Patient;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController

@RequestMapping("/api/patients")

public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<Patient> getAllPatients(){
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable int id){
        return patientService.getPatientById(id);
    }


    @PostMapping
    public Patient addNewPatient(@RequestBody Patient patient, Authentication authentication){
        return patientService.addNewPatient(patient, authentication.getName());

    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable int id, @RequestBody Patient patient, Authentication authentication){
        return patientService.updatePatient(id, patient, authentication.getName());

    }

    @PutMapping( "/{id}/deactivate")
    public Patient deactivatePatient(@PathVariable int id, Authentication authentication){
        return patientService.deactivatePatient(id, authentication.getName());

    }

    @GetMapping("/search")
    public List<Patient> searchPatient(@RequestParam String lastName){
        return patientService.searchPatient(lastName );
    }

    @GetMapping("/search/by-name-and-birthdate")
    public List<Patient> searchByLastNameBirthDate(@RequestParam String lastName,@RequestParam LocalDate birthDate){
        return patientService.searchByLastNameBirthDate(lastName,birthDate);
    }


}
