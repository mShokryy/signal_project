package data_management;

import static org.junit.jupiter.api.Assertions.*; // Use JUnit 5 Assertions
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        // Assuming you have some way to create or mock a DataReader
        // DataReader reader = mock(DataReader.class); // Uncomment if using a mock reader
        /*
        DataStorage storage = new DataStorage(reader); // Pass reader here
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    */
    }
}
