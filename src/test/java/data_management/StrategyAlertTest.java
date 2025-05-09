package data_management;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.design_pattern.strategy.AlertContext;
import com.design_pattern.strategy.BloodPressureStrategy;
import com.design_pattern.strategy.HeartRateStrategy;
import com.design_pattern.strategy.OxygenSaturationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests the alert triggering logic using various health strategies on a mock Patient record.
 */

public class StrategyAlertTest {

  private Patient patient;

    @BeforeEach
    void initial() {
        long now = System.currentTimeMillis();
        int patientId = 666;

        List<PatientRecord> records = new ArrayList<>();
        // * This one with high HeartRate
        records.add(new PatientRecord(patientId,130 , "HeartRate", now - 10000));
        // * This one has normal HeartRate at 75
        records.add(new PatientRecord(patientId, 75, "HeartRate", now - 40000));
        // * This one is BloodPressure at 190
        records.add(new PatientRecord(patientId,  190 , "BloodPressure", now - 30000));

        patient = new Patient(patientId) {
            @Override
            public List<PatientRecord> getRecords(long startTime, long endTime) {
                return records;
            }
        };
    }

    /**
     * Verifies that the OxygenSaturationStrategy does NOT trigger an alert
     * if the patient's oxygen saturation level is outside the 10-second window
     * (e.g., the data is older than 10 seconds).
     * and his measurementValue is normal.
     */
    @Test
    void testOxygenSaturationStrategyAlertFailsIfTooOld() {
        AlertContext context = new AlertContext();
        long now = System.currentTimeMillis();

        context.setAlertStrategy(new OxygenSaturationStrategy());

        // * Create a patient record with a timestamp older than 10 seconds.
        int patientId = 666;

        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(patientId, 100, "OxygenSaturation", 15000 - now));  // 15 seconds old

        // * Creating a new patient object with this specific record (no other records).
        patient = new Patient(patientId) {
            @Override
            public List<PatientRecord> getRecords(long startTime, long endTime) {
                return records;
            }
        };

        // Ensure the alert does NOT trigger because the record is too old (15 seconds).
        assertFalse(context.triggerAlert(patient), "Expected no alert to be triggered because the record is too old.");
    }


        /**
         * Verifies that the HeartRateStrategy correctly triggers an alert
         * when the patient's heart rate exceeds the critical threshold (e.g., 130 bpm).
         */
    @Test
    void testHeartRateStrategyAlert() {
        AlertContext context = new AlertContext();
        context.setAlertStrategy(new HeartRateStrategy());

        assertTrue(context.triggerAlert(patient), "Expected heart rate alert to be triggered.");
    }

    /**
     * Verifies that the BloodPressureStrategy correctly triggers an alert
     * when the patient's blood pressure is above the normal range (e.g., 190 mmHg).
     */
    @Test
    void testBloodPressureStrategyAlert() {
        AlertContext context = new AlertContext();
        context.setAlertStrategy(new BloodPressureStrategy());

        assertTrue(context.triggerAlert(patient), "Expected blood pressure alert to be triggered.");
    }

    /**
     * Verifies that the OxygenSaturationStrategy correctly triggers an alert
     * when the patient's oxygen saturation level is critically low (e.g., 85%).
     * and within the time range.
     */
    @Test
    void testOxygenSaturationStrategyAlert() {
        AlertContext context = new AlertContext();
        long now = System.currentTimeMillis();

        context.setAlertStrategy(new OxygenSaturationStrategy());

        // * Create a patient record with a timestamp older than 10 seconds.
        int patientId = 666;

        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(patientId, 85, "OxygenSaturation", now - 1000));  // 1 seconds old

        // * Creating a new patient object with this specific record (no other records).
       final Patient patient = new Patient(patientId) {
            @Override
            public List<PatientRecord> getRecords(long startTime, long endTime) {
                return records;
            }
        };
        assertTrue(context.triggerAlert(patient), "Expected oxygen saturation alert to be triggered.");
    }
    }

