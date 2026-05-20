package com.alerts.factory_pattern;

import com.alerts.Alert;
import com.alerts.BloodPressureAlert;

/**
 * The factory that creates an instance of BloodPressureAlert. Implements the AlertFactory
 * triggered when the predefined conditions of abnormal blood pressure are triggered.
 */
public class BloodPressureAlertFactory implements AlertFactory{

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, condition, timestamp);
    }
    
}
