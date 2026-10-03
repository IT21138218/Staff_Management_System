package com.sliit.sms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LeaveDecisionForm {
    @NotNull(message = "Leave request id is required")
    private Long leaveRequestId;
    private boolean approve; // true = approve, false = reject
    private String comments;
}
