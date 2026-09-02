package com.AMiller.Follow_up_Management.Repository;

import com.AMiller.Follow_up_Management.entities.EmailTemplates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailTemplatesRepository extends JpaRepository<EmailTemplates, Integer> {
    List<EmailTemplates> findByDepartmentId_nameAndActiveTrue(String departmentName);
}
