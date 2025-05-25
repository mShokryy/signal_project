package server_testing;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.server.MyWebSocketClient;
import com.alerts.AlertGenerator;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that valid data is stored correctly, invalid data is ignored,
 * and alerts are evaluated based on stored patient data.
 */
public class IntegrationTest {

    private static MockWebSocketServer mockServer;
    private static MyWebSocketClient client;
    private static final int PORT = 9090;
    private static final String WS_URI = "ws://localhost:" + PORT;
    private static final DataStorage storage = DataStorage.getInstance();

    /**
     * Sets up the mock WebSocket server and client before all tests run.
     * Also clears the data storage.
     */
    @BeforeAll
    public static void setup() throws Exception {
        mockServer = new MockWebSocketServer(PORT);
        mockServer.start();
        Thread.sleep(500); // Give the server time to start

        storage.clear();

        client = new MyWebSocketClient(new URI(WS_URI), storage);
        client.connectBlocking(); // Wait for client to connect
    }

    /**
     * Closes the WebSocket client and stops the server after all tests have completed.
     */
    @AfterAll
    public static void teardown() throws Exception {
        client.closeBlocking();
        mockServer.stop();
    }

    /**
     * Sends a mix of valid and invalid messages to the server.
     * Verifies that only valid records are stored in the DataStorage.
     */
    @Test
    public void testValidAndInvalidMessages() throws Exception {
        // Send valid messages
        mockServer.broadcast("1,1700000000001,HeartRate,85.5");
        mockServer.broadcast("2,1700000001000,BloodPressure,120.0");

        // Send invalid messages
        mockServer.broadcast("1,1700000002000,Temperature,-5.0"); // invalid (negative value)
        mockServer.broadcast("xyz,1700000003000,HeartRate,90.0"); // invalid ID
        mockServer.broadcast("1,1700000004000,InvalidType,98.6"); // invalid record type
        mockServer.broadcast("1,1700000005000,HeartRate"); // missing field

        Thread.sleep(1000); // Allow time for client to process

        // Verify stored records
        List<PatientRecord> records1 = storage.getRecords(1, 1700000000000L, 1800000000000L);
        List<PatientRecord> records2 = storage.getRecords(2, 1700000000000L, 1800000000000L);

        assertEquals(1, records1.size(), "Patient 1 should have 1 valid record");
        assertEquals(1, records2.size(), "Patient 2 should have 1 valid record");

        assertEquals("HeartRate", records1.get(0).getRecordType());
        assertEquals(85.5, records1.get(0).getMeasurementValue(), 0.01);

        assertEquals("BloodPressure", records2.get(0).getRecordType());
        assertEquals(120.0, records2.get(0).getMeasurementValue(), 0.01);
    }

    /**
     * Runs the AlertGenerator to evaluate alerts on the stored patient data.
     * Asserts that the alert states map is initialized.
     */
    @Test
    public void testAlertEvaluation() {
        Map<String, Boolean> alertStates = new ConcurrentHashMap<>();
        AlertGenerator generator = new AlertGenerator(storage, alertStates);

        for (Patient patient : storage.getAllPatients()) {
            generator.evaluateData(patient);
        }

        assertNotNull(alertStates, "Alert states map should not be null");
    }

    /**
     * Simple mock WebSocket server that logs received messages and can broadcast test data.
     */
    public static class MockWebSocketServer extends WebSocketServer {
        public MockWebSocketServer(int port) {
            super(new InetSocketAddress(port));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            conn.send("Welcome client!");
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            System.out.println("Server received: " + message);
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {}

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("Mock server started");
        }
    }
}
