package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Outputs patient health data to a connected TCP client. Starts a TCP server on the specified 
 * port and accepts a single client connection in a background thread, then streams data to that 
 * client on each output call.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a TcpOutputStrategy that starts a TCP server on the specified port. Waits for a client 
     * connection in a separate thread to avoid blocking the main thread.
     * @param port the port number on which the TCP server will listen
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends patient health data to the connected TCP client as a comma-separated string. Does nothing if 
     * no client is currently connected.
     * @param patientId the ID of the patient the data belongs to
     * @param timestamp the time at which the data was generated, in milliseconds
     * @param label the type of health data 
     * @param data the value or content of the health data
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
