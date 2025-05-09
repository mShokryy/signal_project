package com.cardio_generator;

import java.io.IOException;

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
