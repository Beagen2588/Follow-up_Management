package com.AMiller.Follow_up_Management.Service;

import com.AMiller.Follow_up_Management.Repository.DepartmentRepository;
import com.AMiller.Follow_up_Management.Repository.FollowUpRequestsRepository;
import com.AMiller.Follow_up_Management.Repository.PatientRepository;
import com.AMiller.Follow_up_Management.Repository.UsersRepository;
import com.AMiller.Follow_up_Management.entities.Department;
import com.AMiller.Follow_up_Management.entities.FollowUpRequests;
import com.AMiller.Follow_up_Management.entities.Patient;
import com.AMiller.Follow_up_Management.entities.Users;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FollowUpRequestsServiceTest {

    @Test
    void addFollowUpRequestWithCorrectValues() {
        PatientRepository patientRepository = Mockito.mock(PatientRepository.class);

        DepartmentRepository departmentRepository = Mockito.mock(DepartmentRepository.class);

        FollowUpRequestsRepository followUpRequestsRepository = Mockito.mock(FollowUpRequestsRepository.class);

        AuditLogsService auditLogsService = Mockito.mock(AuditLogsService.class);

        UsersRepository usersRepository = Mockito.mock(UsersRepository.class);

        FollowUpRequestsService followUpRequestsService = new FollowUpRequestsService(followUpRequestsRepository,departmentRepository,patientRepository,
                                                                                    auditLogsService, usersRepository);

        Patient patient = new Patient();
        patient.setFirstName("Cricket");
        patient.setLastName("Green");

        Department department = new Department();
        department.setName("Cardiology");


        when(patientRepository.findById(1))
        .thenReturn(Optional.of(patient));

        when(departmentRepository.findById(1))
        .thenReturn(Optional.of(department));

        Users user = new Users();
        user.setUserName("CAdmin");

        when(usersRepository.findByUserName("CAdmin"))
        .thenReturn(Optional.of(user));

        when(followUpRequestsRepository.save(any(FollowUpRequests.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        FollowUpRequests result =
                followUpRequestsService.addFollowUpRequest(
                        "CAdmin",
                        1,
                        1,
                        "Follow-up appointment",
                        new Date(),
                        "Pending",
                        "Patient needs follow-up"
                );

        assertEquals("Pending", result.getStatus());
        assertEquals("Follow-up appointment", result.getReason());
        assertNotNull(result.getCreatedAt());
        assertSame(patient, result.getPatientId());
        assertSame(department, result.getDepartmentId());

    }
}