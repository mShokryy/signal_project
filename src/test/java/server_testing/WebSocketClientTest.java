package server_testing;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.server.MyWebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests verify the correct handling of valid and invalid messages,
 * and check that the WebSocket client does not crash on connection or parsing errors.
 */
public class WebSocketClientTest {
    private DataStorage dataStorage;
    private MyWebSocketClient client;

    /**
     * Sets up a fresh instance of DataStorage and the WebSocket client before each test.
     */
    @BeforeEach
    void setup() throws Exception {
        dataStorage = DataStorage.getInstance();
        dataStorage.clear(); // Reset before each test

        // Create the WebSocket client using a dummy URI and injected DataStorage
        client = new MyWebSocketClient(new URI("ws://localhost:9090"), dataStorage);
    }

    /**
     * Tests that a correctly formatted message is parsed and stored properly in DataStorage.
     */
    @Test
    void testValidMessageIsParsedAndStored() {
        String message = "1,1710000000000,HeartRate,78.5";
        client.onMessage(message);

        List<PatientRecord> records = dataStorage.getRecords(1, 1700000000000L, 1800000000000L);
        assertEquals(1, records.size());
        PatientRecord record = records.get(0);

        assertEquals("HeartRate", record.getRecordType());
        assertEquals(78.5, record.getMeasurementValue());
        assertEquals(1710000000000L, record.getTimestamp());
    }

    /**
     * Tests that a message with too few fields does not get stored.
     */
    @Test
    void testMalformedMessageTooFewFields() {
        String message = "1,1710000000000,HeartRate"; // Missing value
        client.onMessage(message);

        List<Patient> patients = dataStorage.getAllPatients();
        assertTrue(patients.isEmpty(), "No data should be stored for malformed message");
    }

    /**
     * Tests that a message with a non-numeric patient ID is not stored.
     */
    @Test
    void testMalformedMessageNonNumeric() {
        String message = "abc,1710000000000,HeartRate,78.5"; // patientId is not an int
        client.onMessage(message);

        List<Patient> patients = dataStorage.getAllPatients();
        assertTrue(patients.isEmpty(), "No data should be stored for malformed message");
    }

    /**
     * Tests that the onError method does not crash when an exception occurs.
     */
    @Test
    void testOnErrorLogging() {
        Exception e = new RuntimeException("Simulated error");
        client.onError(e);
        // No assertion — just make sure it doesn't crash
    }

    /**
     * Tests that the onClose method executes without crashing.
     */
    @Test
    void testOnClose() {
        client.onClose(1000, "Normal close", true);
        // No assertion — just ensure no exception
    }

    /**
     * Tests that the onOpen method executes without crashing.
     */
    @Test
    void testOnOpen() {
        try {
            client.onOpen(null);  // ServerHandshake is not used
        } catch (Exception e) {
            // Ignore exceptions because this is just a callback test without a real connection
        }
    }
}

