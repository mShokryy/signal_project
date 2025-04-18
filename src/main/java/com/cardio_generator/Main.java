package com.cardio_generator;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // Check if the argument is "DataStorage"
        if (args.length > 0 && args[0].equalsIgnoreCase("DataStorage")) {
            // Call DataStorage main method
            com.data_management.DataStorage.main(new String[]{});
        } else {
            // Call HealthDataSimulator main method
            HealthDataSimulator.main(new String[]{});
        }
    }
}
