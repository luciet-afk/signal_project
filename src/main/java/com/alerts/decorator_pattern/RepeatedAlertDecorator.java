package com.alerts.decorator_pattern;

import com.alerts.Alert;

/**
 * A decorator that marks an alert as repeated, indicating the condition
 * has been re-triggered over a set interval.
 */
public class RepeatedAlertDecorator extends AlertDecorator {

    private int repeatCount;

    /**
     * Constructs a RepeatedAlertDecorator with a specified repeat count.
     * @param decoratedAlert the alert to decorate.
     * @param repeatCount the number of times the alert has been repeated.
     */
    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatCount) {
        super(decoratedAlert);
        this.repeatCount = repeatCount;
    }

    @Override
    public String getCondition() {
        return super.getCondition() + " REPEATED: " + repeatCount + " times";
    }
}