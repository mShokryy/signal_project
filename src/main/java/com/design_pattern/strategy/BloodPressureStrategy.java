package com.design_pattern.strategy;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {
    @Override
    public boolean checkAlert(Patient patient) {
        long now = System.currentTimeMillis();
        List<PatientRecord> records = patient.getRecords(now - 60000, now); // last 1 minute

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("BloodPressure")) {
                double value = record.getMeasurementValue();
                if (value > 180 || value < 90) {
                    return true; // Critical blood pressure
                }
            }
        }
        return false;
    }
}
