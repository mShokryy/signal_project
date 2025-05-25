package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Random;

public class WebSocketOutputStrategy implements OutputStrategy {

    private WebSocketServer server;

    public WebSocketOutputStrategy(int port) {
        try {
            server = new SimpleWebSocketServer(new InetSocketAddress(port));
            System.out.println("WebSocket server created on port: " + port + ", listening for connections...");
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start WebSocket server on port " + port);
            e.printStackTrace();
            System.exit(1);  // Stop program if server can't start
        }
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
        for (WebSocket conn : server.getConnections()) {
            if (conn.isOpen()) {
                try {
                    conn.send(message);
                } catch (Exception e) {
                    System.err.println("Failed to send message to a client: " + e.getMessage());
                }
            }
        }
    }


    private static class SimpleWebSocketServer extends WebSocketServer {

        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace();
        }

        @Override
        public void onStart() {
            System.out.println("Server started successfully");
        }


    }

    /**
     * Main method to start the WebSocket server and send data read from files.
     *
     * It reads each line from a set of predefined files, parses the patient ID,
     * timestamp, label, and value, and sends this data through the WebSocket server
     * to all connected clients. There is a short delay between sending each message
     * to avoid flooding the clients.
     *
     * The expected file format for each line is:
     * patientId,timestamp,label,value
     *
     * @throws InterruptedException if the thread sleep is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        int port = 9090;
        WebSocketOutputStrategy wsServer = new WebSocketOutputStrategy(port);

        String[] filePaths = {
                "C:\\Users\\iikxq\\ken1520_2024\\signal_project\\output\\WhiteBloodCells.txt",
                "C:\\Users\\iikxq\\ken1520_2024\\signal_project\\output\\SystolicPressure.txt",
                "C:\\Users\\iikxq\\ken1520_2024\\signal_project\\output\\Saturation.txt",
                "C:\\Users\\iikxq\\ken1520_2024\\signal_project\\output\\RedBloodCells.txt",
                "C:\\Users\\iikxq\\ken1520_2024\\signal_project\\output\\ECG.txt",
                "C:\\Users\\iikxq\\ken1520_2024\\signal_project\\output\\DiastolicPressure.txt",
                "C:\\Users\\iikxq\\ken1520_2024\\signal_project\\output\\Cholesterol.txt"
        };

        try {
            for (String path : filePaths) {
                try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 4) {
                            int patientId = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                            long timestamp = Long.parseLong(parts[1].replaceAll("[^0-9]", ""));
                            String label = parts[2].split(":")[1].trim();
                            String value = parts[3].split(":")[1].trim();

                            wsServer.output(patientId, timestamp, label, value);
                            System.out.println("Sent: " + line);
                            Thread.sleep(800); // Avoid flooding
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
