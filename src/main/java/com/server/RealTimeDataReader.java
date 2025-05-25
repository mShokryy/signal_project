package com.server;

import com.data_management.DataStorage;

/**
 * RealTimeDataReader defines the contract for any data reader
 * that connects to a real-time data stream
 *
 * Used for DYNAMIC file reading! <--
 *
 * Implementing classes should handle connecting to the data stream,
 * processing incoming messages, and storing parsed data into the
 * DataStorage system.
 */
public interface RealTimeDataReader {

    /**
     * Establishes a connection to a real-time data source (e.g., WebSocket server)
     * and processes incoming data by storing it in the provided DataStorage.
     *
     * @param dataStorage  the central storage object where parsed patient data should be stored
     * @param websocketUrl the WebSocket URL (e.g., ws://localhost:8080) to connect to
     */
    void connectToStream(DataStorage dataStorage, String websocketUrl);
}
