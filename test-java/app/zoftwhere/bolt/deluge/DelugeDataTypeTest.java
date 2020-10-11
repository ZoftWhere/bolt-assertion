package app.zoftwhere.bolt.deluge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>Deluge Data Type Test.
 * </p>
 * <p>This is a package-private class for testing the {@link app.zoftwhere.bolt.deluge.DelugeDataType} enum.
 * </p>
 *
 * @author Osmund
 * @since 11.4.0
 */
class DelugeDataTypeTest {

    @Test
    void testSize() {
        final var size = DelugeDataType.values().length;
        assertEquals(6, size);
    }

}
