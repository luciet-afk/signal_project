package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

class PatientTest {

    @Test
    void testGetRecordsInRange() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "HeartRate", 1000L);
        patient.addRecord(200.0, "HeartRate", 2000L);
        patient.addRecord(300.0, "HeartRate", 3000L);

        List<PatientRecord> records = patient.getRecords(1000L, 2000L);
        assertEquals(2, records.size());
    }

    @Test
    void testGetRecordsOutOfRange() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "HeartRate", 5000L);

        List<PatientRecord> records = patient.getRecords(1000L, 2000L);
        assertEquals(0, records.size());
    }

    @Test
    void testGetRecordsEmptyPatient() {
        Patient patient = new Patient(1);

        List<PatientRecord> records = patient.getRecords(0L, Long.MAX_VALUE);
        assertEquals(0, records.size());
    }

    @Test
    void testGetPatientId() {
        Patient patient = new Patient(42);
        assertEquals(42, patient.getPatientId());
    }
}
