package com.alerts;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.Patient;

import java.util.List;

class AlertGeneratorTest {

    private DataStorage storage;
    private AlertGenerator alertGenerator;

    @BeforeEach
    void setup() {
        storage = DataStorage.getInstance();
        alertGenerator = new AlertGenerator(storage);
    }

    @Test
    void testSystolicIncreasingTrend() {
        storage.addPatientData(1, 110.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 125.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 140.0, "SystolicPressure", 3000L);

        alertGenerator.evaluateData(new Patient(1));
        assertTrue(containsCondition("Increasing Trend"));
    }

    @Test
    void testSystolicDecreasingTrend() {
        storage.addPatientData(1, 140.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 125.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 110.0, "SystolicPressure", 3000L);

        alertGenerator.evaluateData(new Patient(1));
        assertTrue(containsCondition("Decreasing Trend"));
    }

    @Test
    void testNoTrendAlert() {
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 125.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 122.0, "SystolicPressure", 3000L);

        alertGenerator.evaluateData(new Patient(1));
        assertFalse(containsCondition("Trend"));
    }

    @Test
    void testSystolicCriticalHigh() {
        storage.addPatientData(1, 185.0, "SystolicPressure", 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertTrue(containsCondition("Systolic BP Critical High"));
    }

    @Test
    void testSystolicCriticalLow() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertTrue(containsCondition("Systolic BP Critical Low"));
    }

    @Test
    void testDiastolicCriticalHigh() {
        storage.addPatientData(1, 125.0, "DiastolicPressure", 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertTrue(containsCondition("Diastolic BP Critical High"));
    }

    @Test
    void testDiastolicCriticalLow() {
        storage.addPatientData(1, 55.0, "DiastolicPressure", 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertTrue(containsCondition("Diastolic BP Critical Low"));
    }

    @Test
    void testNoCriticalThresholdAlert() {
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 80.0, "DiastolicPressure", 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertFalse(containsCondition("Critical"));
    }

    @Test
    void testLowSaturationAlert() {
        storage.addPatientData(1, 90.0, "Saturation", 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertTrue(containsCondition("Low Blood Saturation"));
    }

    @Test
    void testNoLowSaturationAlert() {
        storage.addPatientData(1, 92.0, "Saturation", 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertFalse(containsCondition("Low Blood Saturation"));
    }

    @Test
    void testRapidSaturationDrop() {
        storage.addPatientData(1, 97.0, "Saturation", 1000L);
        storage.addPatientData(1, 92.0, "Saturation", 2000L);
        alertGenerator.evaluateData(new Patient(1));
        assertTrue(containsCondition("Rapid Blood Saturation Drop"));
    }

    @Test
    void testNoRapidDropOutsideWindow() {
        storage.addPatientData(1, 97.0, "Saturation", 1000L);
        storage.addPatientData(1, 92.0, "Saturation", 1000L + 11 * 60 * 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertFalse(containsCondition("Rapid Blood Saturation Drop"));
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 90.0, "Saturation", 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertTrue(containsCondition("Hypotensive Hypoxemia"));
    }

    @Test
    void testHypotensiveHypoxemiaLowBPOnly() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 95.0, "Saturation", 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertFalse(containsCondition("Hypotensive Hypoxemia"));
    }

    @Test
    void testHypotensiveHypoxemiaLowSatOnly() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 90.0, "Saturation", 1000L);
        alertGenerator.evaluateData(new Patient(1));
        assertFalse(containsCondition("Hypotensive Hypoxemia"));
    }

    @Test
    void testAbnormalECGPeak() {
        for (int i = 0; i < 5; i++) {
            storage.addPatientData(1, 1.0, "ECG", 1000L + i * 100L);
        }
        storage.addPatientData(1, 10.0, "ECG", 2000L);
        alertGenerator.evaluateData(new Patient(1));
        assertTrue(containsCondition("Abnormal ECG Peak"));
    }

    @Test
    void testNoECGAlert() {
        for (int i = 0; i < 6; i++) {
            storage.addPatientData(1, 1.0, "ECG", 1000L + i * 100L);
        }
        alertGenerator.evaluateData(new Patient(1));
        assertFalse(containsCondition("Abnormal ECG Peak"));
    }

    private boolean containsCondition(String condition) {
        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        for (Alert alert : alerts) {
            if (alert.getCondition().contains(condition)) {
                return true;
            }
        }
        return false;
    }
}