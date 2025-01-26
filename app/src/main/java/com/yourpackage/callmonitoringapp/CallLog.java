package com.yourpackage.callmonitoringapp;

public class CallLog {
    private String phoneNumber;
    private String callType;
    private long duration;

    public CallLog(String phoneNumber, String callType, long duration) {
        this.phoneNumber = phoneNumber;
        this.callType = callType;
        this.duration = duration;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCallType() {
        return callType;
    }

    public long getDuration() {
        return duration;
    }
}
