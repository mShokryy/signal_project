package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates ECG (electrocardiogram) data for patients.
 * This implementation uses Gaussian functions to simulate realistic ECG waveforms
 * including P waves, QRS complex, and T waves, along with natural variability.
 */
public class ECGDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private double[] lastEcgValues;
    private static final double PI = Math.PI;

    public ECGDataGenerator(int patientCount) {
        lastEcgValues = new double[patientCount + 1];
        // Initialize the last ECG value for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastEcgValues[i] = 0; // Initial ECG value can be set to 0
        }
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            double ecgValue = simulateEcgWaveform(patientId, lastEcgValues[patientId]);
            outputStrategy.output(patientId, System.currentTimeMillis(), "ECG", Double.toString(ecgValue));
            lastEcgValues[patientId] = ecgValue;
        } catch (Exception e) {
            System.err.println("An error occurred while generating ECG data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }

    /**
     * Simulates a realistic ECG waveform using Gaussian functions to model the P wave,
     * QRS complex, and T wave. This approach produces a more physiologically accurate
     * shape than simple sine waves.
     *
     * Key improvements over basic sinusoidal generation:
     *
     * Each waveform component (P, Q, R, S, T) is modeled using a Gaussian curve,
     * mimicking the natural timing and amplitude of real ECG signals.
     * Time is normalized over a 1-second cycle, corresponding to 60 bpm, simulating a full heartbeat.
     * Random noise is added to represent sensor and biological variability.
     *
     *
     * @param patientId The ID of the patient (used for indexing).
     * @param lastEcgValue The previous ECG value (not used in this version, kept for future extension).
     * @return A simulated ECG signal value at the current time.
     */
    private double simulateEcgWaveform(int patientId, double lastEcgValue) {
        double hr = 60.0 + random.nextDouble() * 20.0; // Heart rate between 60-80 bpm
        double t = (System.currentTimeMillis() % 1000) / 1000.0; // Normalize time to 1-second ECG cycle

        double pWave = gaussian(t, 0.2, 0.025, 0.1);    // small bump around t=0.2s
        double qWave = gaussian(t, 0.37, 0.012, -0.15); // small downward dip
        double rWave = gaussian(t, 0.4, 0.01, 1.0);     // tall spike
        double sWave = gaussian(t, 0.43, 0.012, -0.25); // small dip after R
        double tWave = gaussian(t, 0.6, 0.04, 0.35);    // broad bump

        double ecgSignal = pWave + qWave + rWave + sWave + tWave;

        // Adding small random noise
        ecgSignal += (random.nextDouble() - 0.5) * 0.05;

        return ecgSignal;
    }

    /*
    /**     -> Gaussian curve formula

     * Calculates a Gaussian (bell curve) value.
     *
     * @param t Current time (normalized between 0 and 1 for a single heartbeat).
     * @param mean The center of the peak.
     * @param stdDev The standard deviation (controls width of the curve).
     * @param amplitude The height of the curve.
     * @return The value of the Gaussian at time t.
     */
    private double gaussian(double t, double mean, double stdDev, double amplitude) {
        return amplitude * Math.exp(-Math.pow(t - mean, 2) / (2 * Math.pow(stdDev, 2)));
    }

}
