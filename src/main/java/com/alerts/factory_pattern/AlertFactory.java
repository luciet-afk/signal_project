package com.alerts.factory_pattern;

import com.alerts.Alert;

/**
 * The Factory interface for the creation of an instance of an Alert.
 * Each impemented class creates a subtype of the Factory based on the specified medical condition.
 */

public interface AlertFactory {

    /**
     * Creates a new Alert for the given patient. 
     * @param patientId the unique patient identifier. 
     * @param condition the condition triggering the alert.
     * @param timestamp the time that the trigger was triggered.
     * @return
     */
    public Alert createAlert(String patientId, String condition, long timestamp);

}
