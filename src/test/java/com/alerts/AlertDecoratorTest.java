package com.alerts;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.alerts.decorator_pattern.PriorityAlertDecorator;
import com.alerts.decorator_pattern.RepeatedAlertDecorator;

class AlertDecoratorTest {

    private Alert baseAlert = new BaseAlert("1", "Systolic BP Critical High", 1000L);

    @Test
    void testPriorityDecoratorAddsTag() {
        Alert decorated = new PriorityAlertDecorator(baseAlert, "HIGH");
        assertTrue(decorated.getCondition().contains("PRIORITY: HIGH"));
    }

    @Test
    void testPriorityDecoratorPreservesOriginalCondition() {
        Alert decorated = new PriorityAlertDecorator(baseAlert, "HIGH");
        assertTrue(decorated.getCondition().contains("Systolic BP Critical High"));
    }

    @Test
    void testRepeatedDecoratorAddsTag() {
        Alert decorated = new RepeatedAlertDecorator(baseAlert, 3);
        assertTrue(decorated.getCondition().contains("REPEATED: 3 times"));
    }

    @Test
    void testRepeatedDecoratorPreservesOriginalCondition() {
        Alert decorated = new RepeatedAlertDecorator(baseAlert, 3);
        assertTrue(decorated.getCondition().contains("Systolic BP Critical High"));
    }

    @Test
    void testStackedDecorators() {
        Alert priority = new PriorityAlertDecorator(baseAlert, "HIGH");
        Alert repeated = new RepeatedAlertDecorator(priority, 2);
        assertTrue(repeated.getCondition().contains("PRIORITY: HIGH"));
        assertTrue(repeated.getCondition().contains("REPEATED: 2 times"));
        assertTrue(repeated.getCondition().contains("Systolic BP Critical High"));
    }

    @Test
    void testDecoratorPreservesPatientId() {
        Alert decorated = new PriorityAlertDecorator(baseAlert, "HIGH");
        assertEquals("1", decorated.getPatientId());
    }

    @Test
    void testDecoratorPreservesTimestamp() {
        Alert decorated = new RepeatedAlertDecorator(baseAlert, 1);
        assertEquals(1000L, decorated.getTimestamp());
    }
}
