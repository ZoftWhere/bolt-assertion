package app.zoftwhere.bolt.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Abstract Unit Test.
 *
 * <p>This class is package-private as it is for testing purposes only.
 *
 * @author Osmund
 * @since 6.0.0
 */
class AbstractUnitTest {

  @Test
  void forCodeCoverage() {
    final var unit = new AbstractUnit() {};
    assertNotNull(unit);
  }
}
