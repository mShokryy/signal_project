package com.design_pattern.strategy;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

public class OxygenSaturationStrategy implements AlertStrategy {

    @Override
    public boolean checkAlert(Patient patient) {
        long now = System.currentTimeMillis();

        List<PatientRecord> records = patient.getRecords(now - 10000, now); // last 10 seconds
        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase("OxygenSaturation")) {
                double value = record.getMeasurementValue();
                if (value < 90) {
                    return true;
                }
            }
        }
        return false;
    }
}