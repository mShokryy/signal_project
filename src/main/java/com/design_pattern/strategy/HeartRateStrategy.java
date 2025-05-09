package com.design_pattern.strategy;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

public class HeartRateStrategy implements AlertStrategy {
    @Override
    public boolean checkAlert(Patient patient) {
        long now = System.currentTimeMillis();
        List<PatientRecord> records = patient.getRecords(now - 60000, now); // last 1 minute

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("HeartRate")) {
                double value = record.getMeasurementValue();
                if (value > 120 || value < 50) {
                    return true;
                }
            }
        }
        return false;
    }
}
