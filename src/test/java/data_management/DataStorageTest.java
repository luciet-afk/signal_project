package data_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

public class DataStorageTest {

    private DataStorage storage;

    @BeforeEach
    void setup() {
        DataStorage.resetInstance(); // clears between tests
        storage = DataStorage.getInstance();
    }

    @Test
    void testAddAndGetRecords() {
        storage.addPatientData(1, 100.0, "HeartRate", 1000L);
        storage.addPatientData(1, 200.0, "HeartRate", 2000L);

        List<PatientRecord> records = storage.getRecords(1, 1000L, 2000L);
        assertEquals(2, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testGetRecordsUnknownPatient() {
        List<PatientRecord> records = storage.getRecords(99, 0L, Long.MAX_VALUE);
        assertEquals(0, records.size());
    }

    @Test
    void testGetRecordsOutOfRange() {
        storage.addPatientData(1, 100.0, "HeartRate", 5000L);
        List<PatientRecord> records = storage.getRecords(1, 1000L, 2000L);
        assertEquals(0, records.size());
    }

    @Test
    void testMultiplePatients() {
        storage.addPatientData(1, 100.0, "HeartRate", 1000L);
        storage.addPatientData(2, 200.0, "HeartRate", 1000L);

        assertEquals(1, storage.getRecords(1, 0L, Long.MAX_VALUE).size());
        assertEquals(1, storage.getRecords(2, 0L, Long.MAX_VALUE).size());
    }
}
