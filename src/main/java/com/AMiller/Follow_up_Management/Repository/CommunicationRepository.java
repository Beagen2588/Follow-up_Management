package com.AMiller.Follow_up_Management.Repository;

import com.AMiller.Follow_up_Management.entities.Communication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunicationRepository  extends JpaRepository<Communication, Integer> {
    List<Communication> findByFollowUpId_PatientId_LastName(String patientLastName);

    boolean existsByTemplateId_Id(int templateId);
}
