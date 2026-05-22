package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Connects to a WebSocket server and receives patient data in real time. Incoming messages are 
 * parsed and stored in DataStorage.
 *
 * Implements DataReader so the system can use real-time data in the same way as other data sources.
 */
public class WebSocketClientReader implements DataReader {

    private final String serverUri;

    public WebSocketClientReader(String serverUri) {
        this.serverUri = serverUri;
    }

    /**
     * Connects to the WebSocket server and tackles incoming patient data. These messages are then
     * parsed and stored in DataStorage.
     * @param dataStorage the data storage unit where the patient data will then be stored.
     * @throws IOException when the connection fails or the server URI is incorrect.
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        try {
            WebSocketClient client = new WebSocketClient(new URI(serverUri)) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to: " + serverUri);
                }

                @Override
                public void onMessage(String message) {
                    try {
                        parseAndStore(message, dataStorage);
                    } catch (Exception e) {
                        System.err.println("Bad message skipped: " + message + "\nReason: " + e.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Connection closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("Error: " + ex.getMessage());
                }
            };

            client.connectBlocking();

        } catch (URISyntaxException e) {
            throw new IOException("Bad URI: " + serverUri, e);
        } catch (InterruptedException e) {
            throw new IOException("Connection interrupted", e);
        }
    }

    /**
     * Parses the incoming data and stores the parsed values in the dataStorage.
     * @param message this is the incoming data message. 
     * @param dataStorage the data storage unit where the patient data will then be stored. 
     */
    public void parseAndStore(String message, DataStorage dataStorage) {

        String[] parts = message.split(",");

        if (parts.length != 4) {
            throw new IllegalArgumentException("Expected 4 fields, got " + parts.length);
        }

        int patientId = Integer.parseInt(parts[0].trim());
        long timestamp = Long.parseLong(parts[1].trim());
        String label = parts[2].trim();
        double value = Double.parseDouble(parts[3].trim());

        dataStorage.addPatientData(patientId, value, label, timestamp);
    }
}