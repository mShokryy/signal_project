package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {

    // * This could be a CSV file or a txt.
    private final String outputDirectoryPath;

    public FileDataReader (String outputDirectoryPath) {
        this.outputDirectoryPath= outputDirectoryPath;
    }

    /**
     * Reads data from a specified source and stores it in the data storage.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File directory = new File(outputDirectoryPath);
        if(!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Output directory INVALID"+ outputDirectoryPath);
        }

        //* Again this could be a CSV or a txt file, and we check if the files don't match.
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt") || name.endsWith(".csv"));
        if(files == null || files.length == 0) {
            throw new IOException("No data found in files: " + outputDirectoryPath );
        }

        for (File file : files) {
            System.out.println("Reading file: " + file.getName());  // Log which file is being processed
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFileEmpty = true;

                // Read each line of the file
                while ((line = reader.readLine()) != null) {
                    line = line.trim(); // Trim to remove leading/trailing whitespaces

                    // Skip empty lines or lines with non-valid data
                    if (line.isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split(",");

                    // Ensure the line has exactly 4 parts
                    if (parts.length == 4) {
                        try {
                            int id = Integer.parseInt(parts[0].trim());  // Parse patient ID
                            double value = Double.parseDouble(parts[1].trim());  // Parse measurement value
                            String type = parts[2].trim();  // Parse record type
                            long time = Long.parseLong(parts[3].trim());  // Parse timestamp

                            // Log the record that is being added
                            System.out.println("Adding record: ID=" + id + ", Value=" + value + ", Type=" + type + ", Time=" + time);

                            // Add the data to the DataStorage
                            dataStorage.addPatientData(id, value, type, time);
                            isFileEmpty = false;  // Mark that the file is not empty
                        } catch (NumberFormatException e) {
                            System.out.println("Skipping line due to parsing error: " + line);
                        }
                    } else {
                        System.out.println("Skipping invalid line (does not have 4 parts): " + line);
                    }
                }

                // If the file was empty, throw an error
                if (isFileEmpty) {
                    System.out.println("File is empty: " + file.getName());
                    throw new IOException("File is empty: " + file.getName());
                }
            }
        }
    }
}
