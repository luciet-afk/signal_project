package com.alerts;

/**
 * An abstract class representing an Alert for a specified patient, condition and time.
 * Used in the product creation of BloodPressureAlert, BloodOxygenAlert and 
 * ECGAlert.
 */
public abstract class Alert {
    private String patientId;
    private String condition;
    private long timestamp;

    /**
     * Alert constrcutor class with the patientID, condition and time stamp.
     * @param patientId a patients unique identification. 
     * @param condition the condition triggering the alert. 
     * @param timestamp the time at which the alert occurs.
     */
    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
