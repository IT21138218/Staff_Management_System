package com.sliit.sms.controller;

import com.sliit.sms.dto.DepartmentForm;
import com.sliit.sms.entity.Department;
import com.sliit.sms.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public List<Department> list() {
        return departmentService.findAll();
    }

    @PostMapping
    public Department create(@Valid @RequestBody DepartmentForm form) {
        return departmentService.create(form);
    }

    @PutMapping("/{id}")
    public Department update(@PathVariable Long id, @Valid @RequestBody DepartmentForm form) {
        return departmentService.update(id, form);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        departmentService.delete(id);
    }
}
