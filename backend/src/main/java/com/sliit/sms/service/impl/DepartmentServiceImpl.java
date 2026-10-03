package com.sliit.sms.service.impl;

import com.sliit.sms.dto.DepartmentForm;
import com.sliit.sms.entity.Department;
import com.sliit.sms.exception.BusinessRuleException;
import com.sliit.sms.exception.ResourceNotFoundException;
import com.sliit.sms.repository.DepartmentRepository;
import com.sliit.sms.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }

    @Override
    public Department create(DepartmentForm form) {
        if (departmentRepository.existsByName(form.getName())) {
            throw new BusinessRuleException("A department named '" + form.getName() + "' already exists");
        }
        return departmentRepository.save(Department.builder()
                .name(form.getName())
                .description(form.getDescription())
                .build());
    }

    @Override
    public Department update(Long id, DepartmentForm form) {
        Department department = findById(id);
        department.setName(form.getName());
        department.setDescription(form.getDescription());
        return departmentRepository.save(department);
    }

    @Override
    public void delete(Long id) {
        Department department = findById(id);
        departmentRepository.delete(department);
    }
}
