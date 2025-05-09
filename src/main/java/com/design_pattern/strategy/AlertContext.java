package com.design_pattern.strategy;
import com.data_management.Patient;

public class AlertContext {
    private AlertStrategy alertStrategy;

    public void setAlertStrategy(AlertStrategy alertStrategy) {
        this.alertStrategy = alertStrategy;
    }

    public boolean triggerAlert(Patient patient) {
        return alertStrategy.checkAlert(patient);
    }
}
