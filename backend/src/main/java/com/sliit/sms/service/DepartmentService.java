package com.sliit.sms.service;

import com.sliit.sms.dto.DepartmentForm;
import com.sliit.sms.entity.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> findAll();
    Department findById(Long id);
    Department create(DepartmentForm form);
    Department update(Long id, DepartmentForm form);
    void delete(Long id);
}
