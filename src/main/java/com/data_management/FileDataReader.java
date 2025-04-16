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

        //* Again this could be a CSV or a txt file and we check if the files don't match.
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt") || name.endsWith(".csv"));
        if(files == null || files.length == 0) {
            throw new IOException("No data found in files: " + outputDirectoryPath );
        }

        for (File file : files) {
            //* Opens each file and reads line by line using BufferedReader
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                //* Splitting the lines based on commas, and parses the following fields, (ID) - (VALUE > measurement) - (TYPE > Ex. heart rate) - (TIME > long).
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        int id = Integer.parseInt(parts[0]);
                        double value = Double.parseDouble(parts[1]);
                        String type = parts[2];
                        long time = Long.parseLong(parts[3]);
                        dataStorage.addPatientData(id, value, type, time);
                    }
                }
            }
        }
    }
}
