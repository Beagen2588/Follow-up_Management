package com.AMiller.Follow_up_Management.Repository;

import com.AMiller.Follow_up_Management.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface PatientRepository extends JpaRepository<Patient, Integer> {
    List<Patient> findByLastNameAndActiveTrue(String lastName);

    List<Patient> findByLastNameAndBirthDateAndActiveTrue(String lastName, LocalDate birthDate);

    boolean existsByFirstNameAndLastNameAndBirthDateAndPhone(
            String firstName,
            String lastName,
            LocalDate birthDate,
            String phone
    );
}
