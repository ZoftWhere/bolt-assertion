package app.zoftwhere.bolt.deluge;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.array;
import static app.zoftwhere.bolt.deluge.DelugeData.forInputStream;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class DelugeDataTest {

    @Test
    void testDelugeDataFlagging() {
        DelugeData data = forInputStream(array("1", "2", "3"));
        assertNotNull(data.streamSupplier());
        assertFalse(data.isOpened());
        assertFalse(data.isClosed());
        try (InputStream inputStream = data.streamSupplier().get()) {
            assertNotNull(inputStream);
            assertTrue(data.isOpened());
            assertFalse(data.isClosed());
        }
        catch (Throwable e) {
            fail(e);
        }
        assertTrue(data.isOpened());
        assertTrue(data.isOpened());
    }

}