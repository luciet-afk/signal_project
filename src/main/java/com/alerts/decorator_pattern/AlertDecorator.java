package com.alerts.decorator_pattern;

import com.alerts.Alert;

/**
 * An abstract Decorator class for Alert. Wraps an Alert instance allowing for multiple
 * 'wrappers' of information without changing the original struture.
 */
public abstract class AlertDecorator implements Alert {

    private Alert decoratedAlert;

    public AlertDecorator(Alert decoratedAlert) {
        this.decoratedAlert = decoratedAlert;
    }

    @Override
    public String getPatientId() { return decoratedAlert.getPatientId(); }

    @Override
    public String getCondition() { return decoratedAlert.getCondition(); }

    @Override
    public long getTimestamp() { return decoratedAlert.getTimestamp(); }
}