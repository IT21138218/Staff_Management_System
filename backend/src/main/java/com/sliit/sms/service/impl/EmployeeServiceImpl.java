package com.sliit.sms.service.impl;

import com.sliit.sms.dto.EmployeeForm;
import com.sliit.sms.entity.*;
import com.sliit.sms.exception.BusinessRuleException;
import com.sliit.sms.exception.ResourceNotFoundException;
import com.sliit.sms.repository.DepartmentRepository;
import com.sliit.sms.repository.EmployeeRepository;
import com.sliit.sms.repository.UserRepository;
import com.sliit.sms.service.EmployeeService;
import com.sliit.sms.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * FR2 - Employee Records Management.
 * Primary user: System Administrator & HR Manager (see proposal, Six Major Functions).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> search(String keyword, Long departmentId, EmployeeStatus status) {
        String k = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return employeeRepository.search(k, departmentId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findByUsername(String username) {
        return employeeRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("No employee record linked to user: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findBySupervisor(Long supervisorId) {
        return employeeRepository.findBySupervisorId(supervisorId);
    }

    @Override
    public Employee create(EmployeeForm form) {
        if (employeeRepository.findAll().stream().anyMatch(e -> e.getEmail().equalsIgnoreCase(form.getEmail()))) {
            throw new BusinessRuleException("An employee with this email already exists: " + form.getEmail());
        }

        Department department = form.getDepartmentId() != null
                ? departmentRepository.findById(form.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + form.getDepartmentId()))
                : null;

        Employee supervisor = form.getSupervisorId() != null
                ? employeeRepository.findById(form.getSupervisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found: " + form.getSupervisorId()))
                : null;

        Employee employee = Employee.builder()
                .employeeCode(generateEmployeeCode())
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .email(form.getEmail())
                .phone(form.getPhone())
                .address(form.getAddress())
                .dateOfBirth(form.getDateOfBirth())
                .dateJoined(form.getDateJoined())
                .designation(form.getDesignation())
                .department(department)
                .basicSalary(form.getBasicSalary())
                .status(EmployeeStatus.ACTIVE)
                .supervisor(supervisor)
                .build();

        if (form.isCreateLogin()) {
            if (form.getUsername() == null || form.getUsername().isBlank()) {
                throw new BusinessRuleException("Username is required to create a portal login");
            }
            if (userRepository.existsByUsername(form.getUsername())) {
                throw new BusinessRuleException("Username already taken: " + form.getUsername());
            }
            Role role = form.getRole() != null ? Role.valueOf(form.getRole()) : Role.EMPLOYEE;
            // temporary password = EmployeeCode@123 ; the employee should change it on first login
            String tempPassword = employee.getEmployeeCode() + "@123";
            User user = userRepository.save(User.builder()
                    .username(form.getUsername())
                    .password(passwordEncoder.encode(tempPassword))
                    .email(form.getEmail())
                    .role(role)
                    .enabled(true)
                    .build());
            employee.setUser(user);
        }

        return employeeRepository.save(employee);
    }

    @Override
    public Employee update(Long id, EmployeeForm form) {
        Employee employee = findById(id);

        employee.setFirstName(form.getFirstName());
        employee.setLastName(form.getLastName());
        employee.setEmail(form.getEmail());
        employee.setPhone(form.getPhone());
        employee.setAddress(form.getAddress());
        employee.setDateOfBirth(form.getDateOfBirth());
        employee.setDateJoined(form.getDateJoined());
        employee.setDesignation(form.getDesignation());
        employee.setBasicSalary(form.getBasicSalary());

        if (form.getDepartmentId() != null) {
            employee.setDepartment(departmentRepository.findById(form.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + form.getDepartmentId())));
        } else {
            employee.setDepartment(null);
        }

        if (form.getSupervisorId() != null) {
            if (form.getSupervisorId().equals(id)) {
                throw new BusinessRuleException("An employee cannot be their own supervisor");
            }
            employee.setSupervisor(employeeRepository.findById(form.getSupervisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found: " + form.getSupervisorId())));
        } else {
            employee.setSupervisor(null);
        }

        return employeeRepository.save(employee);
    }

    @Override
    public void deactivate(Long id) {
        Employee employee = findById(id);
        employee.setStatus(EmployeeStatus.INACTIVE);
        if (employee.getUser() != null) {
            employee.getUser().setEnabled(false);
        }
        employeeRepository.save(employee);
    }

    @Override
    public void activate(Long id) {
        Employee employee = findById(id);
        employee.setStatus(EmployeeStatus.ACTIVE);
        if (employee.getUser() != null) {
            employee.getUser().setEnabled(true);
        }
        employeeRepository.save(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findPotentialSupervisors() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee updatePhoto(Long id, MultipartFile file) {
        Employee employee = findById(id);
        String url = fileStorageService.storeEmployeePhoto(file, employee.getPhotoUrl());
        employee.setPhotoUrl(url);
        return employeeRepository.save(employee);
    }

    private String generateEmployeeCode() {
        long count = employeeRepository.count() + 1;
        String candidate = String.format("EMP%04d", count);
        while (employeeRepository.findByEmployeeCode(candidate).isPresent()) {
            count++;
            candidate = String.format("EMP%04d", count);
        }
        return candidate;
    }
}
