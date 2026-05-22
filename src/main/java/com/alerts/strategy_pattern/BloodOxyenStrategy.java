package com.alerts.strategy_pattern;

import java.util.ArrayList;
import java.util.List;

import com.alerts.Alert;
import com.alerts.factory_pattern.AlertFactory;
import com.alerts.factory_pattern.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * A strategy that monitors blood oxygen saturation levels, triggering alerts
 * when saturation drops below 92% or falls by 5% or more within a 10-minute window.
 */

public class BloodOxyenStrategy implements AlertStrategy{

    private AlertFactory boFactory = new BloodOxygenAlertFactory();

    /**
     * Checks whether the Blood Oxyen values are below 92 or if the drop is above or equal 
     * to 5 in the past 10 minutes.
     * @param patient the given patient.
     * @param records this patient's records.
     * @return a List of triggered alerts.
     */
    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records){

        List<Alert> alerts = new ArrayList<>();

        List<PatientRecord> satRecords = filterByType(records, "Saturation");
        satRecords.sort((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()));

        for (int i = 0; i < satRecords.size(); i++) {
            double value = satRecords.get(i).getMeasurementValue();

            if (value < 92) {
                alerts.add(boFactory.createAlert(
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
                    alerts.add(boFactory.createAlert(
                        String.valueOf(patient.getPatientId()),
                        "Rapid Blood Saturation Drop",
                        satRecords.get(j).getTimestamp()
                    ));
                }
            }
        }

        return alerts;
    }
}
