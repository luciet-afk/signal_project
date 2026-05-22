package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;

class SingletonTest {

    @BeforeEach
    void setup() {
        DataStorage.resetInstance();
    }

    @Test
    void testGetInstanceReturnsSameInstance() {
        DataStorage first = DataStorage.getInstance();
        DataStorage second = DataStorage.getInstance();
        assertSame(first, second);
    }

    @Test
    void testGetInstanceAfterResetReturnsNewInstance() {
        DataStorage first = DataStorage.getInstance();
        DataStorage.resetInstance();
        DataStorage second = DataStorage.getInstance();
        assertNotSame(first, second);
    }

    @Test
    void testInstanceIsNotNull() {
        assertNotNull(DataStorage.getInstance());
    }
}