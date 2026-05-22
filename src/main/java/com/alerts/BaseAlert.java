package com.alerts;

/**
 * Base implementation of the Alert interface, holding the patient ID, condition and timestamp of an alert.
 */
public class BaseAlert implements Alert {
    private String patientId;
    private String condition;
    private long timestamp;

    /**
     * Constructs a BaseAlert with the given patient ID, condition and timestamp.
     * @param patientId a patient's unique identification.
     * @param condition the condition triggering the alert.
     * @param timestamp the time at which the alert occurs.
     */
    public BaseAlert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    @Override
    public String getPatientId() { return patientId; }

    @Override
    public String getCondition() { return condition; }

    @Override
    public long getTimestamp() { return timestamp; }
}
