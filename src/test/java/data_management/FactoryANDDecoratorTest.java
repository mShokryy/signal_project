package data_management;

import com.design_pattern.factoryANDdecorator.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for alert factories and decorators, verifying correct instantiation
 * and behavior of alert types and decorators like priority and repetition.
 */

class FactoryANDDecoratorTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    /**
     * Verifies that BloodOxygenAlertFactory correctly creates a BloodOxygenAlert instance.
     */
    @Test
    void testBloodOxygenAlertFactory() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("patient1", "Low SpO2", 123456789L);
        alert.trigger();
        assertTrue(outContent.toString().contains("Blood Oxygen Alert for patient1: Low SpO2 at 123456789"));
    }

    /**
     * Tests if BloodPressureAlertFactory creates and returns as expected.
     */
    @Test
    void testBloodPressureAlertFactory() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("patient2", "High BP", 987654321L);
        alert.trigger();
        assertTrue(outContent.toString().contains("Blood Pressure Alert for patient2: High BP at 987654321"));
    }

    /**
     * Verifies that ECGAlertFactory creates ECGAlert and triggers the correct message
     */
    @Test
    void testECGAlertFactory() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("patient3", "Irregular Rhythm", 1122334455L);
        alert.trigger();
        assertTrue(outContent.toString().contains("ECG Alert for patient3: Irregular Rhythm at 1122334455"));
    }

    /**
     * Tests if it adds PRIORITY line before triggering the base alert.
     */
    @Test
    void testPriorityAlertDecorator() {
        Alert baseAlert = new BloodOxygenAlert("patient4", "Critical", 111111111L);
        Alert priorityAlert = new PriorityAlertDecorator(baseAlert, "HIGH");

        priorityAlert.trigger();
        String output = outContent.toString();
        assertTrue(output.contains("PRIORITY: HIGH"));
        assertTrue(output.contains("Blood Oxygen Alert for patient4: Critical at 111111111"));
    }

    /**
     * Checks that RepeatedAlertDecorator correctly calls the bast multiple times.
     */
    @Test
    void testRepeatedAlertDecorator() {
        Alert baseAlert = new BloodPressureAlert("patient5", "Low BP", 222222222L);
        Alert repeatedAlert = new RepeatedAlertDecorator(baseAlert, 3);

        repeatedAlert.trigger();
        String output = outContent.toString();
        long count = output.lines().filter(line -> line.contains("Blood Pressure Alert")).count();
        assertTrue(count == 3);
    }

    /**
     * Tests the combination of both decorators.
     */
    @Test
    void testCombinedDecorators() {
        Alert baseAlert = new ECGAlert("patient6", "Tachycardia", 333333333L);
        Alert priorityRepeatedAlert = new PriorityAlertDecorator(
                new RepeatedAlertDecorator(baseAlert, 2),
                "URGENT"
        );

        priorityRepeatedAlert.trigger();
        String output = outContent.toString();
        assertTrue(output.contains("PRIORITY: URGENT"));
        long count = output.lines().filter(line -> line.contains("ECG Alert")).count();
        assertTrue(count == 2);
    }
}