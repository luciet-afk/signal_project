package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated alert events for patients. Models alert triggering and 
 * resolution using probability-based logic, where alerts are triggered using an exponential distribution 
 * and resolved with a fixed probability.
*/
public class AlertGenerator implements PatientDataGenerator {

    //Changed from public to private to perserve encapsulation.
    //Changed from randomGenerator to RANDOM_GENERATOR to follow UPPER_CASE_SNAKE
    public static final Random RANDOM_GENERATOR = new Random(); 
    //Changed from AlertStates to alertStates to follow camelCase
    private boolean[] alertStates; // false = resolved, true = pressed

    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

     /**
     * Generates an alert event for the specified patient and outputs the result.
     * If an alert is active, there is a 90% chance it resolves each period.
     * If no alert is active, a new one may be triggered based on an exponential probability model.
     * @param patientId the ID of the patient to generate an alert event for
     * @param outputStrategy the strategy used to output the generated alert data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                //Lambda changed to lambda, following camelCase
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
