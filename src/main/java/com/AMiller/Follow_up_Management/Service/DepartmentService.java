package com.AMiller.Follow_up_Management.Service;

import com.AMiller.Follow_up_Management.Repository.DepartmentRepository;
import com.AMiller.Follow_up_Management.Repository.EmailTemplatesRepository;
import com.AMiller.Follow_up_Management.Repository.FollowUpRequestsRepository;
import com.AMiller.Follow_up_Management.entities.Department;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;

    }

    public List<Department> getAllDepartments(){
        return departmentRepository.findAll();
    }

    public Department addDepartment(Department department){
        return departmentRepository.save(department);
    }
}
