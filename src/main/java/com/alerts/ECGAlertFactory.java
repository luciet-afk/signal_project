package com.alerts;
/**
 * The factory that creates an instance of ECGAlert. Implements the AlertFactory
 * triggered when the predefined conditions of abnormal ECG readings are triggered.
 */
public class ECGAlertFactory implements AlertFactory{

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new ECGAlert(patientId, condition, timestamp);
    }
    
}
