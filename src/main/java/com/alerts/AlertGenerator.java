package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private Map<String, Boolean> alertStates;


    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage, Map<String, Boolean> alertStates) {
        this.dataStorage = dataStorage;
        this.alertStates = alertStates;
    }


    /**
     *Evaluates the patient's recent medical data to determine if an alert should be triggered
     *or resolved based on specific health criteria.
     *
     * Criteria used below each has their reference.
     *
     * Alerts are triggered or resolved only if there's a change in the patient's alert state.
     *
     *
     * @param patient the patient whose data will be evaluated
     */
    public void evaluateData(Patient patient) {
        String patientId = String.valueOf(patient.getPatientId());
        long currentTime = System.currentTimeMillis();
        long tenMinutesAgo = currentTime - 10*60*1000;

        List<PatientRecord> patientRecords = patient.getRecords(tenMinutesAgo, currentTime);
        boolean alertNeeded = false;
        List<Double> systolicValues = new ArrayList<>();
        List<Double> diastolicValues = new ArrayList<>();

        boolean hasLowSystolic = false;
        boolean hasLowSaturation = false;

        for(PatientRecord record : patientRecords) {
            String type = record.getRecordType();
            double value = record.getMeasurementValue();


          if(type.equals("Systolic") && value < 90) {
              hasLowSystolic = true;
          }

          if(type.equals("Saturation") && value < 92) {
              hasLowSaturation = true;
          }

          if(hasLowSystolic && hasLowSaturation) {
              System.out.println("Hypotensive Hypoxemia Alert for patient: " + patientId);
              alertNeeded = true;
              break;
          }



            // * I chose these criteria based ona forum on the internet with ref: https://www.verywellhealth.com/dangerous-heart-rate-5215509
            if (type.equals("HeartRate") && (value < 60 || value > 100)) {
                alertNeeded = true;
                break;
            }
            // * Criteria based on ref: https://www.verywellhealth.com/systolic-and-diastolic-blood-pressure-1746075
            if (type.equals("Systolic")) {
                systolicValues.add(value);
                if(value > 180 || value < 90) {
                    System.out.println("Critical systolic BP for patient: " + patientId );
                    alertNeeded = true;
                }
            } else if (type.equals("Diastolic")) {
                diastolicValues.add(value);
                if( value > 120 || value < 60) {
                    System.out.println("Critical diastolic BP for patient " + patientId);
                    alertNeeded = true;
                }
            }

        }

        boolean hasSaturation = false;

        for (PatientRecord patientRecord : patientRecords) {
            if ("Saturation".equals(patientRecord.getRecordType())) {
                hasSaturation = true;
                break;
            }
        }

        if (!alertNeeded && hasSaturation && checkSaturationAlerts(patientRecords, patientId)) {
            alertNeeded = true;
        }




        if (!alertNeeded && hasTrendAlert(systolicValues)) {
            System.out.println("Systolic trend alert for patient " + patientId);
            alertNeeded = true;
        }

        if (!alertNeeded && hasTrendAlert(diastolicValues)) {
            System.out.println("Diastolic trend alert for patient " + patientId);
            alertNeeded = true;
        }


        boolean currentState = alertStates.getOrDefault(patientId, false);
        long timestamp = System.currentTimeMillis();

        if (alertNeeded && !currentState) {
            alertStates.put(patientId, true);
            Alert alert = new Alert(patientId, "ALERT TRIGGERED based on vital signs", timestamp);
            triggerAlert(alert);
        } else if (!alertNeeded && currentState) {
            alertStates.put(patientId, false);
            Alert alert = new Alert(patientId, "Alert RESOLVED: readings back to normal", timestamp);
            triggerAlert(alert);
        }


        List<Double> ecgValues = new ArrayList<>();
        for (PatientRecord record : patientRecords) {
            if ("ECG".equals(record.getRecordType())) {
                ecgValues.add(record.getMeasurementValue());
            }
        }

        if (!alertNeeded && hasEcgAlert(ecgValues, patientId)) {
            alertNeeded = true;
        }




    }

    /**
     * HelperMethod that triggers an alert if the patient's blood pressure (systolic or diastolic) shows a
     * consistent increase or decrease across three consecutive readings where each reading
     * changes by more than 10 mmHg from the last.
     *
     *
     * @param values of readings.
     * @return boolean
     */
    private boolean hasTrendAlert(List<Double> values) {
        if (values.size() < 3) return false;

        for (int i = 0; i <= values.size() - 3; i++) {
            double v1 = values.get(i);
            double v2 = values.get(i + 1);
            double v3 = values.get(i + 2);

            boolean increasing = (v2 - v1 > 10) && (v3 - v2 > 10);
            boolean decreasing = (v1 - v2 > 10) && (v2 - v3 > 10);

            if (increasing || decreasing) {
                return true;
            }
        }

        return false;
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        System.out.println(">> [ALERT] Patient " + alert.getPatientId() + ": " + alert.getCondition());
    }

    /**
     *  Evaluates a patient's saturation records to determine if an alert should be triggered
     *  based on two specific criteria:
     *
     *   1. low Saturation alert, triggered if any saturation readings is below 92%
     *   2. Rapid drop alert, triggered if a drop of 5 to 10 percent is happening in time interval of 10 minutes.
     *
     *
     * @param records the list of all recent entries for the patient
     * @param patientId the unique identifier for the patient (used for logging)
     * @ * @return true if any saturation alert condition is met; return false otherwise
     */
    private boolean checkSaturationAlerts(List<PatientRecord> records, String patientId) {
        List<PatientRecord> saturationRecords = new ArrayList<>();

        for(PatientRecord record : records) {
            if("Saturation".equals(record.getRecordType())) {
                saturationRecords.add(record);
            }
        }

        // * Sorting by oldest to newest so the rapid drop alert works accurately.
        saturationRecords.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        for( int i = 0 ; i< saturationRecords.size(); i++) {

            double currentValue = saturationRecords.get(i).getMeasurementValue();

            if(currentValue < 92.0) {
                System.out.println("Low saturation alert thrown!: " + patientId + " :" + currentValue);
                return true;
            }

            for( int j = i+1; j<saturationRecords.size(); j++) {
                long time = (saturationRecords.get(j).getTimestamp() - saturationRecords.get(i).getTimestamp());
                if( time > 10*60*1000) {
                    break; // skipping if time is more than 10 minutes.
                }
                double theDrop = (saturationRecords.get(i).getMeasurementValue() - saturationRecords.get(j).getMeasurementValue());
                if( theDrop >= 5.0) {
                    System.out.println("There's a rapid saturation drop for the patient " + patientId + ": " + saturationRecords.get(i).getMeasurementValue() + "% to " + saturationRecords.get(j).getMeasurementValue() + "%");
                    return true;
                }
            }
        }
        return false;
    }

    public static void throwImediateAlert(Alert alert) {
        String patientId = alert.getPatientId();
        String condition = alert.getCondition();
        long timestamp = alert.getTimestamp();

        String readableTime = Instant.ofEpochMilli(alert.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println(">> [ALERT BUTTON IS PUSHED!! ] " + alert.getPatientId() + ": " +
                alert.getCondition() + " at " + readableTime);


    }

    /**
     * Checks for ECG anomalies using a sliding average and threshold for spike detection.
     *
     * Choosing 1.2 and -0.4 as our thresholds (ASSUMPTION)
     * Why? those values are based on characteristics of the ECGDataGenerator class
     * Bigger than 1.2 is a spike above max Rwave and below -0.4 is a dip deeper.
     *
     *
     * @param ecgValues the list of ECG values
     * @return true if an ECG peak significantly deviates from the average
     */
    public boolean hasEcgAlert(List<Double> ecgValues, String patientId) {
        // * Assumption: we choose 5 values just in case of the spike/anomaly detection is to reduce the chance of false positives.
        if (ecgValues.size() < 5) return false;

        double sum = 0;
        for (double val : ecgValues) {
            sum += val;
        }

        double average = sum / ecgValues.size();

        for (double value : ecgValues) {
            if (value > 1.2 || value < -0.4) {
                System.out.println("ECG anomaly detected for patient: " + patientId);
                return true;
            }

        }

        return false;
    }

}


