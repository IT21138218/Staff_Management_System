package com.sliit.sms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DepartmentForm {
    private Long id;

    @NotBlank(message = "Department name is required")
    private String name;

    private String description;
}
