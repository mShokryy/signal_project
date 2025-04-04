package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;


/**
 * This patientDataGenerator is the godfather that defines the generation of patient data.
 * Implementing classes should provide the logic to generate specific types of data for a patient.
 * So basically it's intended to be used by classes that are responsible for generating patient data and then outputting it.
 */
public interface PatientDataGenerator {
    /**
     * Generates data for a patient and outputs it using the specified OutputStrategy
     *
     * @param patientId the ID of the patient in which the data is being generated and is unique.
     * @param outputStrategy is used for outputting the generated data.
     *
     * @throws IllegalAccessException if the patientId is not found.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
