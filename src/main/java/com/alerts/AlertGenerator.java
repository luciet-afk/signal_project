package com.alerts;

import com.alerts.strategy_pattern.AlertStrategy;
import com.alerts.strategy_pattern.BloodPressureStrategy;
import com.alerts.strategy_pattern.BloodOxyenStrategy;
import com.alerts.strategy_pattern.ECGStrategy;
import com.alerts.strategy_pattern.HypotensiveHypoxemiaStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for monitoring patient data and generating alerts when certain predefined conditions are met. 
 * Alert logic is then passed on to AlertStrategy implementations.
 */

public class AlertGenerator {

    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts = new ArrayList<>();

    private List<AlertStrategy> strategies = List.of(
        new BloodPressureStrategy(),
        new BloodOxyenStrategy(),
        new ECGStrategy(),
        new HypotensiveHypoxemiaStrategy()
    );

    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }

    /**
     * Constructs an AlertGenerator with a specified DataStorage.
     * The DataStorage is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient data.
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the triggerAlert
     * method. This method should define the specific conditions under which an alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), Long.MIN_VALUE, Long.MAX_VALUE);

        for (AlertStrategy strategy : strategies) {
            for (Alert alert : strategy.checkAlert(patient, records)) {
                triggerAlert(alert);
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
        
        String alertMessage = "ALERT: \nPatient " + alert.getPatientId() +
        ": " + alert.getCondition() +
        " at " + alert.getTimestamp();

        System.out.println(alertMessage);

        try(FileWriter fw = new FileWriter("alerts.log", true)){
            fw.write(alertMessage + "\n");
        } catch (IOException e) {
            System.err.println("Failed to log alert: " + e.getMessage());
        }
    }
}