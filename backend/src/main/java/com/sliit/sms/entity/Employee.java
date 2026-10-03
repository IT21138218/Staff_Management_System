package com.sliit.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * FR2 - Employee Records Management.
 * The central record every other module (attendance, leave, payroll,
 * performance, scheduling) hangs off via a ManyToOne relationship.
 */
@Entity
@Table(name = "employees")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String employeeCode; // e.g. EMP0001

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private LocalDate dateJoined;

    @Column(length = 100)
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = false)
    private Double basicSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    // Optional reporting line, used by Supervisor-scoped views/approvals
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Employee supervisor;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user; // the login this employee uses, may be null for a record with no portal access yet

    // Public path (e.g. "/uploads/employees/<uuid>.jpg") the frontend renders
    // directly as an <img src>; null until a photo is uploaded.
    @Column(length = 255)
    private String photoUrl;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
