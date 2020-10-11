package app.zoftwhere.bolt.scope;

import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.deluge.DelugeBuilder;
import app.zoftwhere.bolt.deluge.DelugeData;
import app.zoftwhere.bolt.deluge.DelugeDataType;
import app.zoftwhere.bolt.deluge.DelugeException;
import app.zoftwhere.bolt.deluge.DelugeProgramType;
import app.zoftwhere.bolt.deluge.DelugeSetting;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_CONSOLE_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_STANDARD_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_CONSOLE_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_STANDARD_ARGUED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void testDelugeDataFromBuilder() {
        final var stringArray = new String[0];
        final var encoding = UTF_8;
        final var error = new Exception();
        assertClass(DelugeData.class, DelugeBuilder.forStringArray(stringArray));
        assertClass(DelugeData.class, DelugeBuilder.forStringArray(stringArray, encoding));
        assertClass(DelugeData.class, DelugeBuilder.forInputStream(stringArray, encoding, true));
        assertClass(DelugeData.class, DelugeBuilder.forInputStream(stringArray, encoding, false));
        assertClass(DelugeData.class, DelugeBuilder.forInputStream(error));
        assertClass(DelugeData.class, DelugeBuilder.forInputStream(error));
        assertClass(DelugeData.class, DelugeBuilder.forInputStream(error, encoding));
    }

    @Test
    void testSettingFromBuilder() {
        final var encoding = UTF_8;
        final var array = new String[0];
        final var error = new Exception();
        assertClass(DelugeSetting.class, DelugeBuilder.forSetting());
        assertClass(DelugeSetting.class, DelugeBuilder.forSetting(encoding));
        assertClass(DelugeSetting.class, DelugeBuilder.forSetting(array));
        assertClass(DelugeSetting.class, DelugeBuilder.forSetting(array, encoding));
        assertClass(DelugeSetting.class, DelugeBuilder.forSetting(array, error));
        assertClass(DelugeSetting.class, DelugeBuilder.forSetting(array, error, encoding));
        assertClass(DelugeSetting.class, DelugeBuilder.forSetting(error));
        assertClass(DelugeSetting.class, DelugeBuilder.forSetting(error, encoding));
    }

    @Test
    void testSettingWithEncoding() {
        final var encoding = UTF_8;
        final var base = DelugeBuilder.forSetting();
        final var setting = DelugeSetting.withEncoding(base, encoding);
        assertEquals(encoding, setting.defaultEncoding());
        assertFalse(setting.hasArgumentArray());
        assertNull(setting.argumentArray());
        assertFalse(setting.hasCharSet());
        assertEquals(encoding, setting.charset());
        assertFalse(setting.hasError());
        assertNull(setting.error());
    }

    @Test
    void testBuilderFrom() {
        final var setting = DelugeBuilder.forSetting();
        final var input = DelugeBuilder.forStringArray(new String[0]);
        for (final var type : DelugeProgramType.values()) {
            var builder = DelugeBuilder.from(type, setting, input);
            assertNotNull(builder);
            assertEquals(Runner.DEFAULT_ENCODING, builder.defaultEncoding());
            assertNotNull(builder.inputCharset());
            assertNotNull(builder.outputCharset());
        }
    }

    @Test
    void testBuilderWithEncoding() {
        final var setting = DelugeBuilder.forSetting();
        final var input = DelugeBuilder.forStringArray(new String[0]);
        for (final var type : DelugeProgramType.values()) {
            var builder = DelugeBuilder.from(UTF_8, type, setting, input);
            assertNotNull(builder);
            assertEquals(UTF_8, builder.defaultEncoding());
            assertNotNull(builder.inputCharset());
            assertNotNull(builder.outputCharset());
        }
    }

}
