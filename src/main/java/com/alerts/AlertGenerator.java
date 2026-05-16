package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {

    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts = new ArrayList<>();

    public List<Alert> getTriggeredAlerts() {
        return triggeredAlerts;
    }

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), Long.MIN_VALUE, Long.MAX_VALUE);

        checkBPAlerts(patient, records);
        checkBloodSaturationAlerts(patient, records);
        checkHypotensiveHypoxemia(patient, records);
        checkECGAlerts(patient, records);
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

    /**
     * Checks the BP values (systolic, diastoic) and calls onto the two different alert types (BPTrned, and CriticalThreshold). 
     * @param patient the given patient.
     * @param records this patient's records.
     */
    private void checkBPAlerts(Patient patient, List<PatientRecord> records){
        List<PatientRecord> diastolic = filterByType(records, "DiastolicPressure");
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        
        checkBPTrend(patient, diastolic, "DiastolicPressure");
        checkBPTrend(patient, systolic, "SystolicPressure");
        checkBPCriticalThreshold(patient, systolic, diastolic);
    }

    /**
     * Checks whether the BP values are increasing or decreasing over the last 3 readings.
     * @param patient the given patient.
     * @param records this patient's records.
     * @param type whether we are evaluating systolic or diastolic.
     */
    private void checkBPTrend(Patient patient, List<PatientRecord> records, String type){

        records.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));

        for (int i = 2; i < records.size(); i++) {
            double firstRecord  = records.get(i - 2).getMeasurementValue();
            double secondRecord = records.get(i - 1).getMeasurementValue();
            double thirdRecord  = records.get(i).getMeasurementValue();

            boolean increasing = (secondRecord - firstRecord > 10) && (thirdRecord - secondRecord > 10);
            boolean decreasing = (firstRecord - secondRecord > 10) && (secondRecord - thirdRecord > 10);

            if (increasing) {
                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()),
                    type + " Increasing Trend",
                    records.get(i).getTimestamp()
                ));
            }

            if (decreasing) {
                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()),
                    type + " Decreasing Trend",
                    records.get(i).getTimestamp()
                ));
            }
        }
    }

    /**
     * Checks whether the systolic value is above 180 or below 90. Further checks if distolic is above 120, 
     * or below 60.
     * @param patient the given patient.
     * @param systolic the patients systolic data.
     * @param diastolic the patient's diastolic data.
     */
    private void checkBPCriticalThreshold(Patient patient, List<PatientRecord> systolic, List<PatientRecord> diastolic) {

        for (PatientRecord record : systolic) {
            double value = record.getMeasurementValue();
            if (value > 180) {

                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()), 
                    "Systolic BP Critical High", 
                    record.getTimestamp()
                ));

            } else if (value < 90) {
                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()), 
                    "Systolic BP Critical Low", 
                    record.getTimestamp()
                ));
            }
        }

        for (PatientRecord record : diastolic) {

            double value = record.getMeasurementValue();

            if (value > 120) {
                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()), 
                    "Diastolic BP Critical High", 
                    record.getTimestamp()
                ));
            } else if (value < 60) {
                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()), 
                    "Diastolic BP Critical Low", 
                    record.getTimestamp()
                ));
            }
        }
    }

    /**
     * Checks whether the BloodSat values are below 92 or if the drop is above or equal to 5 in the past 10 minutes.
     * @param patient the given patient.
     * @param records this patient's records.
     */
    private void checkBloodSaturationAlerts(Patient patient, List<PatientRecord> records){

        List<PatientRecord> satRecords = filterByType(records, "Saturation");
        satRecords.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));

        for (int i = 0; i < satRecords.size(); i++) {
            double value = satRecords.get(i).getMeasurementValue();

            if (value < 92) {
                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()),
                    "Low Blood Saturation",
                    satRecords.get(i).getTimestamp()
                ));
            }

            for (int j = i + 1; j < satRecords.size(); j++) {
                long timeDiff = satRecords.get(j).getTimestamp() - satRecords.get(i).getTimestamp();
                if (timeDiff > 10 * 60 * 1000) break;

                double drop = value - satRecords.get(j).getMeasurementValue();
                if (drop >= 5) {
                    triggerAlert(new Alert(
                        String.valueOf(patient.getPatientId()),
                        "Rapid Blood Saturation Drop",
                        satRecords.get(j).getTimestamp()
                    ));
                }
            }
        }
    }

    /**
     * Checks whether the patient is hypotensive through evaluating whether systolic and blood saturation
     * (below 90 and below 92% respectivley) is critically low in the same time frame.
     * @param patient the given patient.
     * @param records the patient's records.
     */
    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> records) {

        List<PatientRecord> systolic  = filterByType(records, "SystolicPressure");
        List<PatientRecord> saturation = filterByType(records, "Saturation");

        for (PatientRecord bpRecord : systolic) {
            if (bpRecord.getMeasurementValue() < 90) {
                for (PatientRecord satRecord : saturation) {
                    if (satRecord.getMeasurementValue() < 92) {

                        long timeDiff = Math.abs(bpRecord.getTimestamp() - satRecord.getTimestamp());

                        if (timeDiff <= 60 * 1000) {

                            triggerAlert(new Alert(
                                String.valueOf(patient.getPatientId()),
                                "Hypotensive Hypoxemia",
                                bpRecord.getTimestamp()
                            ));

                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks for abnormal ECG peaks using a sliding window average.
     * An alert is triggered if a reading exceeds twice the average of the previous windowSize readings.
     *
     * @param patient the given patient.
     * @param records the patient's records.
     */
    private void checkECGAlerts(Patient patient, List<PatientRecord> records) {

        List<PatientRecord> ecgRecords = filterByType(records, "ECG");
        ecgRecords.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));
        int windowSize = 5;

        for (int i = windowSize; i < ecgRecords.size(); i++) {
            double windowSum = 0;

            for (int j = i - windowSize; j < i; j++) {
                windowSum += ecgRecords.get(j).getMeasurementValue();
            }

            double average = windowSum / windowSize;
            double current = ecgRecords.get(i).getMeasurementValue();

            if (current > average * 2) {

                triggerAlert(new Alert(
                    String.valueOf(patient.getPatientId()),
                    "Abnormal ECG Peak",
                    ecgRecords.get(i).getTimestamp()
                ));

            }
        }
    }

    /**
     * A helper method that filters what patient measurment is being evaluated.
     * @param records the patient's records.
     * @param type the type of measurment data that isbeing examined.
     * @return the filtered data.
     */
    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        List<PatientRecord> filtered = new ArrayList<>();
        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase(type)) {
                filtered.add(record);
            }
        }
        return filtered;
    }
}
