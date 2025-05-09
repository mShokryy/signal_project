package com.design_pattern.factoryANDdecorator;

public class BloodOxygenAlert implements Alert {
    private String patientId;
    private String condition;
    private long timestamp;

    public BloodOxygenAlert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    @Override
    public void trigger() {
        System.out.println("Blood Oxygen Alert for " + patientId + ": " + condition + " at " + timestamp);
    }
}