package com.sliit.sms.service;

import com.sliit.sms.entity.Payslip;

import java.util.List;

public interface PayrollService {
    Payslip generateForEmployee(Long employeeId, int month, int year, Long generatedByUserId);
    List<Payslip> generateForAll(int month, int year, Long generatedByUserId);
    List<Payslip> findByEmployee(Long employeeId);
    Payslip findById(Long id);
    List<Payslip> findByPeriod(int month, int year);
}
