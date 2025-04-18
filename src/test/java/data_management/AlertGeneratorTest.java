package data_management;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.Assert.assertTrue;

/**
 * This test verifies that the alert generation logic behaves correctly under various
 * clinical conditions based on blood pressure readings.
 *
 * The alert system is expected to:
 *
 *       -Trigger an alert when there is a critical high or low blood pressure value.
 *       -Trigger an alert when there is a trend of three consecutive increases or decreases
 *        by more than 10 mmHg in systolic or diastolic values.
 *
 * Assumptions:
 *
 * 1- Assuming systolic/diastolic are recordType values.
 * 2- Assuming all patient data used in tests is within the past 10 minutes
 *
 *
 *
 */
public class AlertGeneratorTest {

    private AlertGenerator alertGenerator;
    private Map<String, Boolean> alertStates;
    private DataStorage tempStorage;


    @BeforeEach
    public void setUp() {
        alertStates = new HashMap<>();
        tempStorage = new DataStorage();
        alertGenerator = new AlertGenerator(tempStorage, alertStates);
    }

    /**
     * Helper method to create a patient and populate it with a list of records.
     *
     * @param patientId The unique identifier of the patient.
     * @param records   The list of medical records to be associated with the patient.
     * @return an object with the given records added.
     */
    private Patient createPatientWithRecords(int patientId, List<PatientRecord> records) {
        Patient patient = new Patient(patientId);
        for (PatientRecord record : records) {
            patient.addRecord(record);
        }
        return patient;
    }

    /**
     * Tests whether an alert is triggered when systolic readings increase by more than 10 mmHg
     * across three consecutive readings.
     */
    @Test
    public void testIncreasingSystolicTrendTriggeringAlert() {
        long current = System.currentTimeMillis();

        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(123, 110, "Systolic", current - 30000),
                new PatientRecord(123, 121, "Systolic", current - 20000),
                new PatientRecord(123, 133, "Systolic", current - 10000)
        );

        Patient patient = createPatientWithRecords(123, records);
        alertGenerator.evaluateData(patient);
        Assertions.assertTrue(alertStates.getOrDefault("123", false), "Alert should be triggered for increasing systolic trend");
    }

    /**
     * Tests whether an alert is triggered when diastolic readings decrease by more than 10 mmHg
     * across three consecutive readings.
     */
    @Test
    public void testDecreasingDiastolicTrendTriggeringAlert() {
        long current = System.currentTimeMillis();
        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(1, 90, "Diastolic", current - 30000),
                new PatientRecord(1, 78, "Diastolic", current - 20000),
                new PatientRecord(1, 65, "Diastolic", current - 10000));

        Patient patient = createPatientWithRecords(1, records);
        alertGenerator.evaluateData(patient);

        Assertions.assertTrue(alertStates.getOrDefault("1", false), "Alert should be triggered for decreasing diastolic trend");
    }

    /**
     * Tests whether an alert is triggered when readings exceed critical high thresholds:
     * systolic > 180 mmHg or diastolic > 120 mmHg.
     */
    @Test
    public void testCriticalHighPressureTriggeringAlert() {
        long current = System.currentTimeMillis();
        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(3, 185, "Systolic", current),
                new PatientRecord(3, 125, "Diastolic", current));

        Patient patient = createPatientWithRecords(3, records);
        alertGenerator.evaluateData(patient);
        Assertions.assertTrue(alertStates.getOrDefault("3", false), "Alert should be triggered for critically high blood pressure");
    }

    /**
     * Tests whether an alert is triggered when readings fall below critical low thresholds:
     * systolic < 90 mmHg or diastolic < 60 mmHg.
     */
    @Test
    public void testCriticalLowPressureTriggeringAlert() {
        long now = System.currentTimeMillis();
        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(4, 85, "Systolic", now),
                new PatientRecord(4, 55, "Diastolic", now)
        );

        Patient patient = createPatientWithRecords(4, records);
        alertGenerator.evaluateData(patient);

        Assertions.assertTrue(alertStates.getOrDefault("4", false), "Alert should be triggered for critically low blood pressure");
    }

    /**
     * Tests whether an alert is triggered when saturation falls below 92%
     */
    @Test
    public void testLowSaturationTriggeringAlert() {
        long current = System.currentTimeMillis();
        List<PatientRecord> records = Collections.singletonList(new PatientRecord(5, 91.5, "Saturation", current));

        Patient patient = createPatientWithRecords(5, records);
        alertGenerator.evaluateData(patient);

        Assertions.assertTrue(alertStates.getOrDefault("5", false), "Alert should be thrown for low saturation");
    }

    /**
     * Tests whether an alert is triggered when there's a rapid saturation drop of 5% or more within 10 minutes.
     */
    @Test
    public void testRapidDropSaturationTriggeringAlert() {
        long current = System.currentTimeMillis();
        List<PatientRecord> records = Arrays.asList(
                new PatientRecord(3, 97.0, "Saturation", current-9*60*1000),
                new PatientRecord(3, 91.5,"Saturation", current));

        Patient patient = createPatientWithRecords(3, records);
        alertGenerator.evaluateData(patient);

        Assertions.assertTrue(alertStates.getOrDefault("3", false), "Alert should be thrown for rapid saturation drop.");
    }
}