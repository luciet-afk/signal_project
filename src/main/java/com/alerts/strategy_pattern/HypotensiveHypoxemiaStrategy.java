package com.alerts.strategy_pattern;

import com.alerts.Alert;
import com.alerts.factory_pattern.AlertFactory;
import com.alerts.factory_pattern.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * A strategy that monitors for hypotensive hypoxemia, triggering an alert when systolic blood 
 * pressure drops below 90 mmHg and oxygen saturation falls below 92% within the same 60-second window.
 */

public class HypotensiveHypoxemiaStrategy implements AlertStrategy {

    private AlertFactory boFactory = new BloodOxygenAlertFactory();

        /**
     * Checks whether the patient is hypotensive through evaluating whether systolic and blood saturation
     * (below 90 and below 92% respectivley) is critically low in the same time frame.
     * @param patient the given patient.
     * @param records the patient's records.
     * @return a List of triggered alerts.
     */
    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {

        List<Alert> alerts = new ArrayList<>();

        List<PatientRecord> systolic  = filterByType(records, "SystolicPressure");
        List<PatientRecord> saturation = filterByType(records, "Saturation");

        for (PatientRecord bpRecord : systolic) {
            if (bpRecord.getMeasurementValue() < 90) {
                for (PatientRecord satRecord : saturation) {
                    if (satRecord.getMeasurementValue() < 92) {

                        long timeDiff = Math.abs(bpRecord.getTimestamp() - satRecord.getTimestamp());

                        if (timeDiff <= 60 * 1000) {

                            alerts.add(boFactory.createAlert(
                                String.valueOf(patient.getPatientId()),
                                "Hypotensive Hypoxemia",
                                bpRecord.getTimestamp()
                            ));

                            return alerts;
                        }
                    }
                }
            }
        }

        return alerts;
    }

}