package server_testing;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests verify that patient data is stored, retrieved,
 * and cleared correctly, and that invalid queries are handled properly.
 */
public class DataStorageTest {
    private DataStorage dataStorage;

    /**
     * Sets up a fresh instance of DataStorage before each test.
     */
    @BeforeEach
    void setup() {
        dataStorage = DataStorage.getInstance();
        dataStorage.clear(); // Clear data before each test.
    }

    /**
     * Tests that a single patient data record can be added and retrieved correctly.
     */
    @Test
    void testAddAndRetrievePatientData() {
        dataStorage.addPatientData(1, 98.6, "Temperature", 1710000000000L);
        List<PatientRecord> records = dataStorage.getRecords(1, 1700000000000L, 1800000000000L);

        assertEquals(1, records.size());
        assertEquals("Temperature", records.get(0).getRecordType());
        assertEquals(98.6, records.get(0).getMeasurementValue());
    }

    /**
     * Tests that multiple data points for the same patient are stored and retrieved correctly.
     */
    @Test
    void testAddMultipleDataPoints() {
        dataStorage.addPatientData(2, 120, "Systolic", 1710000000000L);
        dataStorage.addPatientData(2, 80, "Diastolic", 1710000005000L);

        List<PatientRecord> records = dataStorage.getRecords(2, 1700000000000L, 1800000000000L);
        assertEquals(2, records.size());
    }

    /**
     * Tests that clearing the storage removes all patient data.
     */
    @Test
    void testClearStorage() {
        dataStorage.addPatientData(3, 70, "HeartRate", 1710000000000L);
        dataStorage.clear();

        assertTrue(dataStorage.getAllPatients().isEmpty());
    }

    /**
     * Tests that requesting records for a non-existent patient returns an empty list.
     */
    @Test
    void testGetRecordsNoPatient() {
        List<PatientRecord> records = dataStorage.getRecords(999, 1700000000000L, 1800000000000L);
        assertNotNull(records);
        assertTrue(records.isEmpty());
    }
}
