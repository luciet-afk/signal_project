package data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClientReader;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class WebSocketClientReaderTest {

    private DataStorage storage;
    private WebSocketClientReader reader;

    @BeforeEach
    void setUp() {
        DataStorage.resetInstance();
        storage = DataStorage.getInstance();
        reader = new WebSocketClientReader("ws://localhost:8080");
    }

    @Test
    void testValidMessageIsParsedAndStored() {
        reader.parseAndStore("1,1700000000000,HeartRate,82.5", storage);

        var records = storage.getRecords(1, 1700000000000L, 1700000000001L);
        assertEquals(1, records.size());
        assertEquals("HeartRate", records.get(0).getRecordType());
        assertEquals(82.5, records.get(0).getMeasurementValue());
    }

    @Test
    void testMissingFieldsThrowsException() {

        assertThrows(IllegalArgumentException.class, () ->
            reader.parseAndStore("1,1700000000000,HeartRate", storage)
        );
    }

    @Test
    void testNonNumericPatientIdThrowsException() {
        assertThrows(NumberFormatException.class, () ->
            reader.parseAndStore("abc,1700000000000,HeartRate,82.5", storage)
        );
    }

    @Test
    void testNonNumericMeasurementThrowsException() {
        assertThrows(NumberFormatException.class, () ->
            reader.parseAndStore("1,1700000000000,HeartRate,notanumber", storage)
        );
    }

    @Test
    void testMultipleMessagesForSamePatient() {
        reader.parseAndStore("1,1700000000000,HeartRate,82.5", storage);
        reader.parseAndStore("1,1700000000001,HeartRate,85.0", storage);

        var records = storage.getRecords(1, 1700000000000L, 1700000000002L);
        assertEquals(2, records.size());
    }

    @Test
    void testDifferentPatientsStoredSeparately() {
        reader.parseAndStore("1,1700000000000,HeartRate,82.5", storage);
        reader.parseAndStore("2,1700000000000,HeartRate,90.0", storage);

        assertEquals(1, storage.getRecords(1, 1700000000000L, 1700000000001L).size());
        assertEquals(1, storage.getRecords(2, 1700000000000L, 1700000000001L).size());
    }

    @Test
    void testParsedDataTriggersAlertEvaluation() {

        reader.parseAndStore("1,1700000000000,HeartRate,82.5", storage);

        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1700000000001L);
        assertEquals(1, records.size());

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(storage.getAllPatients().get(0)));
    }
}