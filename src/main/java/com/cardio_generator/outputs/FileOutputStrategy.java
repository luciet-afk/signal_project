package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

public class FileOutputStrategy implements OutputStrategy {

    //Changed BaseDirectory to baseDirectory per camelCase.
    private String baseDirectory;

    //Changed from file_map to FILE_MAP, based on final constant. Thus must abide UPPER_SNAKE_CASE.
    //Changed public to private to stop information leakage - reinforces encapsulation.
    private final ConcurrentHashMap<String, String> FILE_MAP = new ConcurrentHashMap<>(); 

    public FileOutputStrategy(String baseDirectory) {
        //Changed to fit camelCase.
        this.baseDirectory = baseDirectory;
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory)); //Change to baseDirectory based on camelCase
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        //Changed to FILE_MAP to abide UPPER_SNAKE_CASE for finals. 
        //Change to baseDirectory to abide by camelCase.
        String FilePath = FILE_MAP.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString()); 

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(FilePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + FilePath + ": " + e.getMessage());
        }
    }
}