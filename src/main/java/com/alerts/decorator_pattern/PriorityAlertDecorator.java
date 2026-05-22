package com.alerts.decorator_pattern;

import com.alerts.Alert;

/**
 * A decorator that adds a priority tag to an alert's condition, flagging it as needing urgent attention.
 */
public class PriorityAlertDecorator extends AlertDecorator {

    private String priorityLevel;

    /**
     * Constructs a PriorityAlertDecorator with a specified priority level.
     * @param decoratedAlert the alert to decorate.
     * @param priorityLevel the priority level to tag the alert with.
     */
    public PriorityAlertDecorator(Alert decoratedAlert, String priorityLevel) {
        super(decoratedAlert);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public String getCondition() {
        return "PRIORITY: " + priorityLevel + " " + super.getCondition();
    }
}