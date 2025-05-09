package data_management;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit strategy for the Patient class.
 * Verifies record addition and filtering based on timestamp range.
 */
public class PatientTest {

    /**
     * Testing whether it can retrieve and add records in valid ranges.
     */
    @Test
    void testAddAndRetrieveRecords() {
       Patient patient = new Patient(1);

       patient.addRecord(120.5, "HeartRate", 1714376789050L);
        patient.addRecord(98.5, "Temperature", 1714376788950L);

        List<PatientRecord> allRecords = patient.getRecords(1714370000000L, 1714380000000L);
        assertEquals(2, allRecords.size(), "Should retrieve 2 records in the given time range");

    }

    // * Testing if a record OUTOFRANGE is correctly actually not in range.

    @Test
    void testNoRecordsInTimeRange() {
        Patient patient = new Patient(2);
        patient.addRecord(99.1, "BloodPressure", 1714371000000L);
        patient.addRecord(98.5, "Temperature", 1714376788950L);

        List<PatientRecord> emptyList = patient.getRecords(1714380000000L, 1714390000000L);
        assertTrue(emptyList.isEmpty(), "No records should be returned for non-overlapping time range");

    }

    // * Testing if it correctly gets the patientID.
    @Test
    void testGetPatientId() {
        Patient patient = new Patient(42);
        assertEquals(42, patient.getPatientId(), "Should return correct patient ID");
    }

    // * Testing if AddRecord() actually adds the correct record or not.
    @Test
    void testAddRecord() {
        Patient patient = new Patient(99);

        // * Adding a single record
        double measurement = 75.5;
        String type = "HeartRate";
        long timestamp = 1714376789050L;
        patient.addRecord(measurement, type, timestamp);

        // * Retrieve it back
        List<PatientRecord> records = patient.getRecords(timestamp, timestamp);

        assertEquals(1, records.size(), "One record should be added");
        PatientRecord record = records.get(0);
        assertEquals(99, record.getPatientId(), "Patient ID should match");
        assertEquals(measurement, record.getMeasurementValue(), "Measurement should match");
        assertEquals(type, record.getRecordType(), "Record type should match");
        assertEquals(timestamp, record.getTimestamp(), "Timestamp should match");
    }

}
