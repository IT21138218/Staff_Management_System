package com.sliit.sms.repository;

import com.sliit.sms.entity.Employee;
import com.sliit.sms.entity.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUser_Username(String username);

    Optional<Employee> findByEmployeeCode(String employeeCode);

    List<Employee> findBySupervisorId(Long supervisorId);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByStatus(EmployeeStatus status);

    // FR10 - Search & Filter: by name, department, or designation in one query
    @Query("SELECT e FROM Employee e WHERE " +
           "(:keyword IS NULL OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:departmentId IS NULL OR e.department.id = :departmentId) " +
           "AND (:status IS NULL OR e.status = :status)")
    List<Employee> search(@Param("keyword") String keyword,
                           @Param("departmentId") Long departmentId,
                           @Param("status") EmployeeStatus status);
}
