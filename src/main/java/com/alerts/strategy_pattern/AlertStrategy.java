package com.alerts.strategy_pattern;

import java.util.ArrayList;
import java.util.List;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * An interface of the AlertStrategis, each of which add a potential list of alerts to the patients history.
 */
public interface AlertStrategy {

    /**
     * Evaluates the given patient's records and returns a list of alerts if any criteria are met.
     * @param patient the patient to evaluate.
     * @param records the patient's health records to analyse.
     * @return a list of triggered alerts, or an empty list if none are found.
     */
    List<Alert> checkAlert(Patient patient, List<PatientRecord> records);

    /**
     * A default method that filters what patient measurment is being evaluated.
     * @param records the patient's records.
     * @param type the type of measurment data that isbeing examined.
     * @return the filtered data.
     */
    default List<PatientRecord> filterByType(List<PatientRecord> records, String type) {

        List<PatientRecord> filtered = new ArrayList<>();

        for (PatientRecord record : records) {
            if (record.getRecordType().equalsIgnoreCase(type)) {
                filtered.add(record);
            }
        }

        return filtered;
    }
}
