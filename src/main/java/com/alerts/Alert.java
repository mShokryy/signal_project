package com.alerts;

// Represents an alert
public class Alert {
    public static final String UNTRIGGERED_ALERT = "UNTRIGGERED_ALERT";
    public static final String TRIGGERED_ALERT = "TRIGGERED_ALERT";
    private String patientId;
    private String condition;
    private static long timestamp;

    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public static long getTimestamp() {
        return timestamp;
    }
}
