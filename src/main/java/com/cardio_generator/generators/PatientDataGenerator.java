package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines the interface for generating health data for a specific patient.
 */
public interface PatientDataGenerator {
    /**
     * Generates health data for the specified patient and sends it to the given output strategy.
     * @param patientId the ID of the patient to generate data for
     * @param outputStrategy the strategy used to output the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
