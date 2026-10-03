package com.sliit.sms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class RosterForm {
    @NotNull(message = "Employee is required")
    private Long employeeId;

    @NotNull(message = "Shift is required")
    private Long shiftId;

    @NotNull(message = "Date is required")
    private LocalDate rosterDate;
}
