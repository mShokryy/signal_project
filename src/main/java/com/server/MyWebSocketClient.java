package com.server;

import com.data_management.DataStorage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Set;

/**
 * WebSocket client that connects to a server, receives patient data as CSV,
 * parses it manually (without external libraries), and stores it using DataStorage.
 */
public class MyWebSocketClient extends WebSocketClient {
    private final DataStorage dataStorage;

    /**
     * Constructs a new WebSocket client.
     *
     * @param serverUri     the URI of the WebSocket server to connect to
     * @param dataStorage   the DataStorage instance used to store parsed patient data
     */
    public MyWebSocketClient(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }

    /**
     * Called when the WebSocket connection is established.
     *
     * @param handshake the server handshake data
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server");
        send("Client connected and ready");
    }

    /**
     * Called when a message is received from the server.
     * Expects the message to be a comma-separated string with format:
     * patientId, timestamp, recordType, measurementValue
     *
     * @param message the message received from the server
     */
    @Override
    public void onMessage(String message) {
        // Ignore server welcome or chat-like messages
        if (message.equals("Welcome client!") || message.startsWith("You said:")) {
            return;
        }

        System.out.println("Received message: " + message);

        String[] parts = message.split(",");

        if (parts.length != 4) {
            System.err.println("Invalid message format: " + message);
            return;
        }

        try {
            int patientId = Integer.parseInt(parts[0].trim());
            long timestamp = Long.parseLong(parts[1].trim());
            String recordType = parts[2].trim();
            double measurementValue = Double.parseDouble(parts[3].trim());

            if (patientId <= 0) throw new IllegalArgumentException("Invalid patient ID");
            if (measurementValue < 0) throw new IllegalArgumentException("Measurement cannot be negative");
            if (!isValidRecordType(recordType)) throw new IllegalArgumentException("Unknown record type");

            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            System.out.println("Data stored for patient ID " + patientId);

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            // e.printStackTrace(); // uncomment if full trace needed
        }
    }

    private static final Set<String> VALID_RECORD_TYPES = Set.of("HeartRate", "BloodPressure", "Temperature");

    /**
     * Validates the record type string.
     *
     * @param recordType the record type to validate
     * @return true if the record type is known, false otherwise
     */
    private boolean isValidRecordType(String recordType) {
        return VALID_RECORD_TYPES.contains(recordType);
    }

    /**
     * Called when the WebSocket connection is closed.
     *
     * @param code    the closure code
     * @param reason  the reason for closure
     * @param remote  true if the connection was closed by the remote host
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    /**
     * Called when an error occurs during communication.
     *
     * @param ex the exception that occurred
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error:");
        ex.printStackTrace();
    }

    /**
     * Entry point to run the WebSocket client.
     * Connects to a local WebSocket server and listens for patient data.
     *
     * @param args command-line arguments (not used)
     * @throws Exception if the URI is invalid or the connection fails
     */
    public static void main(String[] args) throws Exception {
        URI serverUri = new URI("ws://localhost:9090");
        DataStorage dataStorage = DataStorage.getInstance();
        MyWebSocketClient client = new MyWebSocketClient(serverUri, dataStorage);
        client.connectBlocking();
    }
}