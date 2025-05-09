package com.design_pattern.factoryANDdecorator;

public class ECGAlert implements Alert {
    private String patientId;
    private String condition;
    private long timestamp;

    public ECGAlert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    @Override
    public void trigger() {
        System.out.println("ECG Alert for " + patientId + ": " + condition + " at " + timestamp);
    }
}