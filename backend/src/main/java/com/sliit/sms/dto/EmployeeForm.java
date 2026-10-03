package com.sliit.sms.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Web form backing object for creating/editing an Employee (FR2).
 * Kept separate from the Employee entity so validation annotations and
 * screen-only fields (e.g. optional "create a login too" checkbox) don't
 * leak into the persistence model.
 */
@Getter @Setter
public class EmployeeForm {

    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @Pattern(regexp = "^$|^[0-9+\\-\\s()]{7,20}$", message = "Enter a valid phone number")
    private String phone;

    private String address;

    private LocalDate dateOfBirth;

    @NotNull(message = "Date joined is required")
    private LocalDate dateJoined;

    private String designation;

    private Long departmentId;

    @NotNull(message = "Basic salary is required")
    @Positive(message = "Basic salary must be greater than zero")
    private Double basicSalary;

    private Long supervisorId;

    // Optional: create a portal login for this employee at the same time
    private boolean createLogin;
    private String username;
    private String role; // Role enum name, e.g. EMPLOYEE, SUPERVISOR
}
