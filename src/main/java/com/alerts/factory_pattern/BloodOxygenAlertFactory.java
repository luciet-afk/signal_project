package com.alerts.factory_pattern;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;

/**
 * The factory that creates an instance of BloodOxygenAlert. Implements the AlertFactory
 * triggered when the predefined conditions of abnormal blood oxygen are trigggered. 
 */
public class BloodOxygenAlertFactory implements AlertFactory{

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodOxygenAlert(patientId, condition, timestamp);
    }
    
}
