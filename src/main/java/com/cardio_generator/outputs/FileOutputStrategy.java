package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements the OutputStrategy interface and provides the implementation to output health data to files.
 * It also writes health data to files in a specified directory and the files are also separate.
 */
public class FileOutputStrategy implements OutputStrategy {

    // Changed it to lower camel case, and made it final since it won't change.
    private final String baseDirectory;

    // Changed it to lower camel case, made it private because it's an instance variable.
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a new FileOutputStrategy with the specified base directory.
     *
     * @param baseDirectory The base directory where the output files will be stored.
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    /**
     * Outputs health data for a specific patient to a file in the base directory.
     *
     * @param patientId The ID of the patient for whom the data is being generated.
     * @param timestamp The timestamp of when the data is being generated.
     * @param label The label indicating the type of data.
     * @param data The health data associated with the patient and the label.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Changed it to lower camel case.
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}