package com.sliit.sms.common.patterns.observer;

import com.sliit.sms.entity.LeaveRequest;

/**
 * Observer interface for the OBSERVER pattern applied to leave decisions.
 */
public interface LeaveStatusObserver {
    void onLeaveStatusChanged(LeaveRequest leaveRequest);
}
