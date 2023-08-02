package app.zoftwhere.bolt.deluge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Deluge Data Type Test.
 *
 * <p>This is a package-private class for testing the {@link
 * app.zoftwhere.bolt.deluge.DelugeDataType} enum.
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
