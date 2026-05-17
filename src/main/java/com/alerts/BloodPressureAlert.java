package com.alerts;
/**
 * Represents an alert triggered by abnormal blood pressure levels, such as critically high
 * or low readings, or a consistent increasing/decreasing trend. This is then produced by the 
 * BloodPressureAlertFactory.
 */
public class BloodPressureAlert extends Alert {

    public BloodPressureAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

}
