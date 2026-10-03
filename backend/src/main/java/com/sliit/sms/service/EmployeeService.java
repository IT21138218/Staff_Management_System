package com.sliit.sms.service;

import com.sliit.sms.dto.EmployeeForm;
import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.EmployeeStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
    List<Employee> findAll();
    List<Employee> search(String keyword, Long departmentId, EmployeeStatus status);
    Employee findById(Long id);
    Employee findByUsername(String username);
    List<Employee> findBySupervisor(Long supervisorId);
    Employee create(EmployeeForm form);
    Employee update(Long id, EmployeeForm form);
    void deactivate(Long id);
    void activate(Long id);
    List<Employee> findPotentialSupervisors();
    Employee updatePhoto(Long id, MultipartFile file);
}
