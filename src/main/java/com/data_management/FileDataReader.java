package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {

    private final String filePath;

    /**
     * The fileDataReader constructor method, used to initiate the file pathway
     * @param filePath the file pathway to the output files
     */
    public FileDataReader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * This method is responsible forvparsing the given DataStorage file/directory. These results are 
     * then stored in the dataStorage. 
     * @param dataStorage the storage where the data will be stored.
     * @throws IOException if the file is not found.
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {

        File directory = new File(filePath);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("This directory: " + filePath + " does not exist.");
        }

        File[] outputFiles = directory.listFiles();

        if (outputFiles == null) {
            return;
        }

        for (File file : outputFiles) {

            if (file.isFile()) {
                parseFile(file, dataStorage);
            }
        }
    }

    /**
     * This helper method is responsible for parsing/reading the given file, then adds each record into dataStorage.
     * @param file the file to be parsed.
     * @param dataStorage this is the dataStorage where results will be returned to.
     * @throws IOException if a line is formatted wrong.
     */
    private void parseFile(File file, DataStorage dataStorage) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                try {
                    parseLine(line, dataStorage);

                } catch (Exception e) {
                    System.err.println("Line: " + line + " is not formed correctly, therefore it was skipped.");
                }
            }
        }
    }

    /**
     * This parses a singular line, and then adds it to the DataStorage.
     * @param line is the line being parsed.
     * @param dataStorage this is the dataStorage where results will be returned to.
     */
    private void parseLine(String line, DataStorage dataStorage) {

        String[] parts = line.split(", ");

        int patientId = Integer.parseInt(getValue(parts[0]));
        long timestamp = Long.parseLong(getValue(parts[1]));
        String label = getValue(parts[2]);
        double data = Double.parseDouble(getValue(parts[3]));

        dataStorage.addPatientData(patientId, data, label, timestamp);
    }

    /**
     * Helper method that gets the value after ": " from a field.
     * @param field The field to extract from.
     * @return the extracted value.
     */
    private String getValue(String field) {
        return field.split(": ")[1].trim();
    }
}