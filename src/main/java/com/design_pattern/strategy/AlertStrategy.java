package com.design_pattern.strategy;
import com.data_management.Patient;

public interface AlertStrategy {
    boolean checkAlert(Patient patient);
}
