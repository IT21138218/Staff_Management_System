package com.sliit.sms.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PerformanceReviewForm {
    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotBlank(message = "Review period is required, e.g. 2026-H1")
    private String reviewPeriod;

    @NotNull(message = "Score is required")
    @Min(value = 1, message = "Score must be between 1 and 5")
    @Max(value = 5, message = "Score must be between 1 and 5")
    private Integer score;

    private String strengths;
    private String areasForImprovement;
    private String feedback;
}
