package com.data_management;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.alerts.AlertGenerator;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {
    // Thread-safe patient map
    private Map<Integer, Patient> patientMap = new ConcurrentHashMap<>();

    private static DataStorage instance;

    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */
    private DataStorage() {
        // Already initialized above with ConcurrentHashMap
    }

    public static DataStorage getInstance() {
        if (instance == null) {
            synchronized (DataStorage.class) {
                if (instance == null) {
                    instance = new DataStorage();
                }
            }
        }
        return instance;
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's records.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        // Use atomic operation to get or create the patient
        patientMap.computeIfAbsent(patientId, id -> new Patient(id))
                .addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Clears all stored patient data. Primarily used for resetting state in unit tests.
     * This method will be very helpful for unit testing.
     */
    public void clear() {
        patientMap.clear();
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        return Collections.emptyList(); // immutable empty list
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * The main method for the DataStorage class.
     * Initializes the system, reads data into storage, and continuously monitors
     * and evaluates patient data.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // the directory path where your data files are located
        String dataDirectoryPath = "C:\\Users\\iikxq\\ken1520_2024\\signal_project";

        // Using both FileDataReader and DataStorage
        DataStorage storage = DataStorage.getInstance();
        FileDataReader reader = new FileDataReader(dataDirectoryPath);

        try {
            reader.readData(storage);

            List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1800000000000L);

            Map<String, Boolean> alertStates = new ConcurrentHashMap<>();
            AlertGenerator alertGenerator = new AlertGenerator(storage, alertStates);

            for (Patient patient : storage.getAllPatients()) {
                alertGenerator.evaluateData(patient);
            }
        } catch (IOException e) {
            System.err.println("Error reading data: " + e.getMessage());
        }
    }
}
