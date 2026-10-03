package com.sliit.sms.repository;

import com.sliit.sms.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    List<Payslip> findByEmployeeIdOrderByPayYearDescPayMonthDesc(Long employeeId);
    Optional<Payslip> findByEmployeeIdAndPayMonthAndPayYear(Long employeeId, Integer month, Integer year);
    List<Payslip> findByPayMonthAndPayYear(Integer month, Integer year);
}
