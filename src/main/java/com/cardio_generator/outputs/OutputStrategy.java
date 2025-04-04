package com.cardio_generator.outputs;

/**
 * The interface is defined to output health data, Implementations
 * of this interface should specify how health data is output.
 */
public interface OutputStrategy {

    /**
     * Outputs the health data for a specific patient.
     *
     * @param patientId The ID of the patient for whom the data is being generated
     * @param timestamp The timestamp of when the data is being generated
     * @param label The label indicating the type of data.
     * @param data The health data associated with the patient and the label.
     */
    void output(int patientId, long timestamp, String label, String data);
}
