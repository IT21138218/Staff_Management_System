package com.sliit.sms.controller;

import com.sliit.sms.dto.EmployeeForm;
import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.EmployeeStatus;
import com.sliit.sms.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * FR2 - Employee Records Management (System Administrator & HR Manager).
 * Access to /api/employees/** is restricted to ADMIN and HR_MANAGER in SecurityConfig.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<Employee> list(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Long departmentId,
                                @RequestParam(required = false) EmployeeStatus status) {
        return employeeService.search(keyword, departmentId, status);
    }

    @GetMapping("/supervisors")
    public List<Employee> potentialSupervisors() {
        return employeeService.findPotentialSupervisors();
    }

    @GetMapping("/{id}")
    public Employee view(@PathVariable Long id) {
        return employeeService.findById(id);
    }

    @PostMapping
    public Employee create(@Valid @RequestBody EmployeeForm form) {
        return employeeService.create(form);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @Valid @RequestBody EmployeeForm form) {
        return employeeService.update(id, form);
    }

    @PostMapping("/{id}/deactivate")
    public Employee deactivate(@PathVariable Long id) {
        employeeService.deactivate(id);
        return employeeService.findById(id);
    }

    @PostMapping("/{id}/activate")
    public Employee activate(@PathVariable Long id) {
        employeeService.activate(id);
        return employeeService.findById(id);
    }

    @PostMapping(value = "/{id}/photo", consumes = "multipart/form-data")
    public Employee uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return employeeService.updatePhoto(id, file);
    }
}
