package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class FileDataReaderTest {

    private static final String TEST_FILE = "test_output";

    @BeforeEach
    void setup() {
        DataStorage.resetInstance();
        new File(TEST_FILE).mkdir();
    }

    @AfterEach
    void cleanup() {
        File file = new File(TEST_FILE);
        for (File f : file.listFiles()) {
            f.delete();
        }
        file.delete();
    }

    @Test
    void testReadValidFile() throws IOException {
        File file = new File(TEST_FILE + "/test.txt");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 78.0\n");
            fw.write("Patient ID: 1, Timestamp: 2000, Label: HeartRate, Data: 82.0\n");
        }

        DataStorage storage = DataStorage.getInstance();
        new FileDataReader(TEST_FILE).readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, Long.MAX_VALUE);
        assertEquals(2, records.size());
        assertEquals(78.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testInvalidDirectory() {
        FileDataReader reader = new FileDataReader("nonexistent_dir");
        DataStorage storage = DataStorage.getInstance();
        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void testMalformedLinesSkipped() throws IOException {
        File file = new File(TEST_FILE + "/malformed.txt");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 78.0\n");
            fw.write("this is not valid data\n");
        }

        DataStorage storage = DataStorage.getInstance();
        new FileDataReader(TEST_FILE).readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, Long.MAX_VALUE);
        assertEquals(1, records.size());
    }
}