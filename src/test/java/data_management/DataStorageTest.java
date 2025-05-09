package data_management;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This strategy class verifies the functionality of the DataStorage class.
 * It tests the ability to store patient data and retrieve it based on ID and time range.
 *
 * Assumptions:
 * - getRecords() returns all records for a patient ID within the specified time range (inclusive).
 * - The storage does not filter by record type (e.g., "WhiteBloodCells") during retrieval.
 */
class DataStorageTest {

    /**
     * Tests that records can be added and retrieved correctly.
     * Verifies both the number of records and their measurement values.
     */
    @Test
    void testAddAndGetRecords() {
        DataStorage storage = DataStorage.getInstance();
        // * Add two records for the same patient
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        // * Retrieve records within the timestamp range
        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);

        // * Validate size and contents of the returned list
        assertEquals(2, records.size(), "Two records should be returned");
        assertEquals(100.0, records.get(0).getMeasurementValue(), "First record value mismatch");
        assertEquals(200.0, records.get(1).getMeasurementValue(), "Second record value mismatch");
    }

    /**
     * Tests if it correctly retrieves all the patients mentioned.
     */
    @Test
    public void testGetAllPatients() {
        DataStorage storage = DataStorage.getInstance();
        // Add data for two patients
        storage.addPatientData(1, 98.6, "Temperature", 1700000000000L);
        storage.addPatientData(2, 75, "HeartRate", 1700000000000L);

        List<Patient> allPatients = storage.getAllPatients();

        // There should be 2 patients stored
        assertEquals(2, allPatients.size());

        // IDs should match what was added
        assertTrue(allPatients.stream().anyMatch(p -> p.getPatientId() == 1));
        assertTrue(allPatients.stream().anyMatch(p -> p.getPatientId() == 2));
    }
}

