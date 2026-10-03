package com.sliit.sms.common.patterns.observer;

import com.sliit.sms.entity.LeaveRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * DESIGN PATTERN: OBSERVER
 * ----------------------------------------------------------------------
 * LeaveService (the "subject") calls publish() every time a LeaveRequest's
 * status changes (submitted, approved, rejected, cancelled). Any number of
 * observers can register interest without LeaveService knowing or caring who
 * they are or what they do with the event - today that's a single
 * NotificationLeaveObserver that raises a Notification, but a future
 * "audit log" or "Slack webhook" observer could be added with zero changes
 * to LeaveService itself (Open/Closed principle).
 */
@Component
public class LeaveStatusPublisher {

    private final List<LeaveStatusObserver> observers = new ArrayList<>();

    public void subscribe(LeaveStatusObserver observer) {
        observers.add(observer);
    }

    public void publish(LeaveRequest leaveRequest) {
        for (LeaveStatusObserver observer : observers) {
            observer.onLeaveStatusChanged(leaveRequest);
        }
    }
}
