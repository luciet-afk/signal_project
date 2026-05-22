package com.alerts.strategy_pattern;

import java.util.ArrayList;
import java.util.List;

import com.alerts.Alert;
import com.alerts.factory_pattern.AlertFactory;
import com.alerts.factory_pattern.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * A strategy that monitors blood pressure readings, triggering alerts for consistent 
 * increasing or decreasing trends and critical threshold levels in both systolic and 
 * diastolic pressure.
 */

public class BloodPressureStrategy implements AlertStrategy {

    private AlertFactory bpFactory = new BloodPressureAlertFactory();

    /**
     * Checks the BP values (systolic, diastoic) and calls onto the two different alert types 
     * (BPTrned, and CriticalThreshold). 
     * @param patient the given patient.
     * @param records this patient's records.
     * @return A list of triggered alerts.
     */
    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records){
        List<Alert> alerts = new ArrayList<>();

        List<PatientRecord> diastolic = filterByType(records, "DiastolicPressure");
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        
        checkBPTrend(patient, diastolic, "DiastolicPressure", alerts);
        checkBPTrend(patient, systolic, "SystolicPressure", alerts);
        checkBPCriticalThreshold(patient, systolic, diastolic, alerts);

        return alerts;
    }

    /**
     * Checks whether the BP values are increasing or decreasing over the last 3 readings.
     * @param patient the given patient.
     * @param records this patient's records.
     * @param type whether we are evaluating systolic or diastolic.
     * @param alerts a List of triggered alerts.
     */
    private void checkBPTrend(Patient patient, List<PatientRecord> records, String type, List<Alert> alerts){

        records.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));

        for (int i = 2; i < records.size(); i++) {
            double firstRecord  = records.get(i - 2).getMeasurementValue();
            double secondRecord = records.get(i - 1).getMeasurementValue();
            double thirdRecord  = records.get(i).getMeasurementValue();

            boolean increasing = (secondRecord - firstRecord > 10) && (thirdRecord - secondRecord > 10);
            boolean decreasing = (firstRecord - secondRecord > 10) && (secondRecord - thirdRecord > 10);

            if (increasing) {
                alerts.add(bpFactory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    type + " Increasing Trend",
                    records.get(i).getTimestamp()
                ));
            }

            if (decreasing) {
                alerts.add(bpFactory.createAlert(
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
     * @param a List of triggered alerts.
     */
    private void checkBPCriticalThreshold(Patient patient, List<PatientRecord> systolic, List<PatientRecord> diastolic, List<Alert> alerts) {

        for (PatientRecord record : systolic) {
            double value = record.getMeasurementValue();
            if (value > 180) {

                alerts.add(bpFactory.createAlert(
                    String.valueOf(patient.getPatientId()), 
                    "Systolic BP Critical High", 
                    record.getTimestamp()
                ));

            } else if (value < 90) {
                alerts.add(bpFactory.createAlert(
                    String.valueOf(patient.getPatientId()), 
                    "Systolic BP Critical Low", 
                    record.getTimestamp()
                ));
            }
        }

        for (PatientRecord record : diastolic) {

            double value = record.getMeasurementValue();

            if (value > 120) {
                alerts.add(bpFactory.createAlert(
                    String.valueOf(patient.getPatientId()), 
                    "Diastolic BP Critical High", 
                    record.getTimestamp()
                ));
            } else if (value < 60) {
                alerts.add(bpFactory.createAlert(
                    String.valueOf(patient.getPatientId()), 
                    "Diastolic BP Critical Low", 
                    record.getTimestamp()
                ));
            }
        }
    }
    
}
