package com.alerts;

/**
 * Represents an alert triggered by abnormal blood oxygen levels, such as low saturation or a 
 * rapid drop in oxygen levels. This is then produced by the BloodOxygenAlertFactory.
 */

public class BloodOxygenAlert extends Alert {

    public BloodOxygenAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
    
}
