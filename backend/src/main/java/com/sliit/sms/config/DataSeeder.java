package com.sliit.sms.config;

import com.sliit.sms.entity.*;
import com.sliit.sms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;

/**
 * Seeds reference data (departments, leave types, shifts) and one demo user
 * per role on first run, so the system is immediately demonstrable without
 * manual setup. Every check is "if not already present" so it is safe to
 * run again on every application restart (spring.jpa.hibernate.ddl-auto=update
 * does not wipe existing rows).
 *
 * DEMO LOGINS (see README for the full list):
 *   admin / Admin@123            -> System Administrator
 *   hr.manager / Hr@12345        -> HR Manager
 *   supervisor / Super@123       -> Department Supervisor
 *   payroll.officer / Payroll@123-> Payroll Officer
 *   employee / Employee@123      -> Employee
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Department hrDept = seedDepartment("Human Resources", "People operations, recruitment and HR policy");
        Department financeDept = seedDepartment("Finance", "Payroll, accounts and financial reporting");
        Department opsDept = seedDepartment("Operations", "Day-to-day service delivery and shift-based staff");
        seedDepartment("IT", "Systems, infrastructure and application support");

        LeaveType annual = seedLeaveType("Annual", 14);
        LeaveType sick = seedLeaveType("Sick", 7);
        seedLeaveType("Casual", 7);
        seedLeaveType("Unpaid", 0);

        seedShift("Morning", LocalTime.of(8, 0), LocalTime.of(16, 0));
        seedShift("Evening", LocalTime.of(14, 0), LocalTime.of(22, 0));
        seedShift("Night", LocalTime.of(22, 0), LocalTime.of(6, 0));

        Employee hrEmployee = seedUserAndEmployee("admin", "Admin@123", "admin@sms.local", Role.ADMIN,
                "System", "Administrator", "SYS-ADMIN", hrDept, 250000.0, null);

        Employee hrManagerEmployee = seedUserAndEmployee("hr.manager", "Hr@12345", "hr.manager@sms.local", Role.HR_MANAGER,
                "Hasini", "Perera", "HR Manager", hrDept, 220000.0, null);

        Employee supervisorEmployee = seedUserAndEmployee("supervisor", "Super@123", "supervisor@sms.local", Role.SUPERVISOR,
                "Sanjaya", "Fernando", "Department Supervisor", opsDept, 180000.0, null);

        seedUserAndEmployee("payroll.officer", "Payroll@123", "payroll.officer@sms.local", Role.PAYROLL_OFFICER,
                "Priyanka", "De Silva", "Payroll Officer", financeDept, 170000.0, null);

        Employee employee = seedUserAndEmployee("employee", "Employee@123", "employee@sms.local", Role.EMPLOYEE,
                "Kasun", "Jayasuriya", "Operations Assistant", opsDept, 120000.0, supervisorEmployee);

        int year = Year.now().getValue();
        seedLeaveBalance(employee, annual, year, 14.0);
        seedLeaveBalance(employee, sick, year, 7.0);
        seedLeaveBalance(supervisorEmployee, annual, year, 14.0);
        seedLeaveBalance(supervisorEmployee, sick, year, 7.0);

        log.info("Data seeding complete.");
    }

    private Department seedDepartment(String name, String description) {
        return departmentRepository.findAll().stream()
                .filter(d -> d.getName().equals(name))
                .findFirst()
                .orElseGet(() -> departmentRepository.save(Department.builder().name(name).description(description).build()));
    }

    private LeaveType seedLeaveType(String name, int defaultDays) {
        return leaveTypeRepository.findAll().stream()
                .filter(lt -> lt.getName().equals(name))
                .findFirst()
                .orElseGet(() -> leaveTypeRepository.save(LeaveType.builder().name(name).defaultDaysPerYear(defaultDays).build()));
    }

    private void seedShift(String name, LocalTime start, LocalTime end) {
        boolean exists = shiftRepository.findAll().stream().anyMatch(s -> s.getName().equals(name));
        if (!exists) {
            shiftRepository.save(Shift.builder().name(name).startTime(start).endTime(end).build());
        }
    }

    private Employee seedUserAndEmployee(String username, String rawPassword, String email, Role role,
                                          String firstName, String lastName, String designation,
                                          Department department, double basicSalary, Employee supervisor) {
        if (userRepository.existsByUsername(username)) {
            return employeeRepository.findByUser_Username(username).orElse(null);
        }

        User user = userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .email(email)
                .role(role)
                .enabled(true)
                .build());

        Employee emp = Employee.builder()
                .employeeCode(generateEmployeeCode())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .dateJoined(LocalDate.now().minusMonths(6))
                .designation(designation)
                .department(department)
                .basicSalary(basicSalary)
                .status(EmployeeStatus.ACTIVE)
                .supervisor(supervisor)
                .user(user)
                .build();

        return employeeRepository.save(emp);
    }

    private void seedLeaveBalance(Employee employee, LeaveType leaveType, int year, double allocated) {
        if (employee == null) return;
        boolean exists = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(employee.getId(), leaveType.getId(), year).isPresent();
        if (!exists) {
            leaveBalanceRepository.save(LeaveBalance.builder()
                    .employee(employee).leaveType(leaveType).year(year)
                    .allocatedDays(allocated).usedDays(0.0).build());
        }
    }

    private String generateEmployeeCode() {
        long count = employeeRepository.count() + 1;
        return String.format("EMP%04d", count);
    }
}
