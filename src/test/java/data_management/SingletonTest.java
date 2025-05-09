package data_management;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to verify correct implementation of singleton pattern
 * in {DataStorage} and {HealthDataSimulator} classes.
 */
public class SingletonTest {

    /**
     * Ensures the singleton method returns a non-null instance
     */
    @Test
    public void testDataStorageSingletonInstanceIsNotNull() {
        DataStorage instance = DataStorage.getInstance();
        assertNotNull(instance, "DataStorage instance should not be null");
    }

    /**
     * ensures all calls to getInstance() return the exact same object.
     */
    @Test
    public void testDataStorageSingletonInstanceIsSame() {
        DataStorage firstInstance = DataStorage.getInstance();
        DataStorage secondInstance = DataStorage.getInstance();
        assertSame(firstInstance, secondInstance, "Both references should point to the same DataStorage instance");
    }

    /**
     * Ensures the singleton method returns a non-null instance
     */
    @Test
    public void testHealthSimSingletonInstanceIsNotNull() {
        HealthDataSimulator instance = HealthDataSimulator.getInstance();
        assertNotNull(instance, "HealthDataSimulator instance should not be null");
    }

    /**
     * Ensures all calls to getInstance() return the exact same object.
     */
    @Test
    public void testHealthSimSingletonInstanceIsSame() {
        HealthDataSimulator firstInstance = HealthDataSimulator.getInstance();
        HealthDataSimulator secondInstance = HealthDataSimulator.getInstance();
        assertSame(firstInstance, secondInstance, "Both references should point to the same HealthDataSimulator instance");
    }
}
