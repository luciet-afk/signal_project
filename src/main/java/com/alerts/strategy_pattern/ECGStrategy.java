package com.alerts.strategy_pattern;

import java.util.ArrayList;
import java.util.List;

import com.alerts.Alert;
import com.alerts.factory_pattern.AlertFactory;
import com.alerts.factory_pattern.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

public class ECGStrategy implements AlertStrategy{

    private AlertFactory ecgFactory = new ECGAlertFactory();

    /**
     * Checks for abnormal ECG peaks using a sliding window average.
     * An alert is triggered if a reading exceeds twice the average of the previous windowSize readings.
     *
     * @param patient the given patient.
     * @param records the patient's records.
     * @return a List of triggered alerts.
     */
    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {

        List<Alert> alerts = new ArrayList<>();

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

                alerts.add(ecgFactory.createAlert(
                    String.valueOf(patient.getPatientId()),
                    "Abnormal ECG Peak",
                    ecgRecords.get(i).getTimestamp()
                ));

            }
        }

        return alerts;
    }

}
