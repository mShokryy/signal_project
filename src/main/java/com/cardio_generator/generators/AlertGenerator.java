package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * The AlertGenerator simulates giving and resolving alerts for patients based on some probability.
 *
 */
public class AlertGenerator implements PatientDataGenerator {


    // This is a constant.
    /**
     * A shared random number used for alert simulation.
     */
    public static final Random RANDOM_GENERATOR = new Random();

    // Changed variable name to lower camelCase.
    /**
     * Tracks the alert state of each patient.
     */
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructs an AlertGenerator for a given number of patients.
     *
     * @param patientCount the number of patients to give alerts for.
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates alert data for a specific patient.
     * If an alert is currently active, there's a 90% to resolve.
     * otherwise an alert will be triggered.
     *
     * @param patientId the ID of the patient in which the data is being generated and is unique.
     * @param outputStrategy is used for outputting the generated data.
     * @thrwos Exception if an error occurs during generation of alert data.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {

                // Changed this to a lower camel case
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                // Changed this to a lower camel case
                double alertProbability = -Math.expm1(-lambda); // Probability of at least one alert in the period
                // Changed this to a lower camel case
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < alertProbability;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
