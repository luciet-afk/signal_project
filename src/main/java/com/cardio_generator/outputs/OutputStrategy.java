package com.cardio_generator.outputs;

/**
 * Defines the interface for outputting generated patient health data.
 */
public interface OutputStrategy {
    /**
     * Outputs health data for a specific patient.
     * @param patientId the ID of the patient the data belongs to
     * @param timestamp the time at which the data was generated, in milliseconds
     * @param label the type of health data being output
     * @param data the value or content of the health data
     */
    void output(int patientId, long timestamp, String label, String data);
}
