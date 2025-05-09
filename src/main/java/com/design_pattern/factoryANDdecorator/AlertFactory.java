package com.design_pattern.factoryANDdecorator;

public abstract class AlertFactory {
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}
