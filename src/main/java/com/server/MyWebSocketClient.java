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

    public MyWebSocketClient(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server");
        send("Client connected and ready");
    }

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

    private boolean isValidRecordType(String recordType) {
        return VALID_RECORD_TYPES.contains(recordType);
    }





    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error:");
        ex.printStackTrace();
    }

    public static void main(String[] args) throws Exception {
        URI serverUri = new URI("ws://localhost:9090");
        DataStorage dataStorage = DataStorage.getInstance();
        MyWebSocketClient client = new MyWebSocketClient(serverUri, dataStorage);
        client.connectBlocking();
    }

}
