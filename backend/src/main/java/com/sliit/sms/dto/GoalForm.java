package com.sliit.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class GoalForm {
    private Long id;

    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private LocalDate targetDate;
    private String status;
}
