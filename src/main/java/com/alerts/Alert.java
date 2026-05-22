package com.alerts;

/**
 * Interface representing an Alert for a specified patient, condition and time.
 * Used in the product creation of BloodPressureAlert, BloodOxygenAlert and ECGAlert.
 */
public interface Alert {
    String getPatientId();
    String getCondition();
    long getTimestamp();
}