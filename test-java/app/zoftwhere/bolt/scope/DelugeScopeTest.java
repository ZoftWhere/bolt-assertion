package app.zoftwhere.bolt.scope;

import app.zoftwhere.bolt.deluge.DelugeBuilder;
import app.zoftwhere.bolt.deluge.DelugeData;
import app.zoftwhere.bolt.deluge.DelugeDataType;
import app.zoftwhere.bolt.deluge.DelugeException;
import app.zoftwhere.bolt.deluge.DelugeProgramType;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_CONSOLE_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_STANDARD_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_CONSOLE_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_STANDARD_ARGUED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DelugeScopeTest {

    @Test
    void testDelugeException() {
        assertNotEquals(new DelugeException("test"), null);
        assertNotEquals(new DelugeException("test", new Exception()), null);
    }

    @Test
    void testDelugeProgramType() {
        assertEquals(8, DelugeProgramType.values().length);
        for (final var type : DelugeProgramType.values()) {
            assertNotEquals(type.isInputFirst(), type.isProgramFirst());
            assertNotEquals(type.isConsole(), type.isStandard());
        }
        assertTrue(INPUT_CONSOLE_ARGUED.isArgued());
        assertTrue(INPUT_STANDARD_ARGUED.isArgued());
        assertTrue(PROGRAM_CONSOLE_ARGUED.isArgued());
        assertTrue(PROGRAM_STANDARD_ARGUED.isArgued());
    }

    @Test
    void testDelugeDataType() {
        assertEquals(6, DelugeDataType.values().length);
    }

    @Test
    void testDelugeData() {
        DelugeData data = DelugeBuilder.forInputStream(new String[0], UTF_8, true);
        assertSame(DelugeDataType.STREAM_ENCODED, data.type());
    }

}
