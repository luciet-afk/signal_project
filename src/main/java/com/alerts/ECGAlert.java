package com.alerts;
/**
 * Represents an alert triggered by abnormal ECG readings, such as irregular
 * heart rates or rhythms. This is then produced by the ECGAlertFactory.
 */
public class ECGAlert extends Alert{

    public ECGAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
    
}
