package com.sliit.sms.service.impl;

import com.sliit.sms.common.patterns.observer.LeaveStatusObserver;
import com.sliit.sms.common.patterns.observer.LeaveStatusPublisher;
import com.sliit.sms.entity.LeaveRequest;
import com.sliit.sms.entity.NotificationType;
import com.sliit.sms.service.NotificationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Concrete OBSERVER: reacts to leave status changes published by
 * LeaveServiceImpl (via LeaveStatusPublisher) by raising a Notification for
 * the employee. Registers itself with the publisher on startup so
 * LeaveServiceImpl never needs to know this class exists.
 */
@Component
@RequiredArgsConstructor
public class LeaveNotificationObserver implements LeaveStatusObserver {

    private final LeaveStatusPublisher publisher;
    private final NotificationService notificationService;

    @PostConstruct
    public void register() {
        publisher.subscribe(this);
    }

    @Override
    public void onLeaveStatusChanged(LeaveRequest leaveRequest) {
        var employee = leaveRequest.getEmployee();
        if (employee.getUser() == null) {
            return; // no portal login to notify
        }
        String title = "Leave request " + leaveRequest.getStatus().name().toLowerCase();
        String message = String.format("Your %s leave request for %s to %s is now %s.",
                leaveRequest.getLeaveType().getName(),
                leaveRequest.getStartDate(), leaveRequest.getEndDate(),
                leaveRequest.getStatus().name());
        notificationService.notify(employee.getUser(), title, message, NotificationType.IN_APP);
    }
}
