package app.zoftwhere.bolt.deluge;

import org.junit.jupiter.api.Test;

/**
 * Deluge Program Type Test.
 *
 * <p>This is a package-private class for testing the {@link
 * app.zoftwhere.bolt.deluge.DelugeProgramType} enum.
 *
 * @author Osmund
 * @since 6.0.0
 */
class DelugeProgramTypeTest {

  @Test
  void testProgramTypeCompleteness() {
    for (final var programType : DelugeProgramType.values()) {
      if (programType.isProgramFirst() && programType.isInputFirst()) {
        throw new DelugeException("deluge.program.type.occlusion");
      }

      if (!programType.isProgramFirst() && !programType.isInputFirst()) {
        throw new DelugeException("deluge.program.type.exclusion");
      }

      if (programType.isStandard() && programType.isConsole()) {
        throw new DelugeException("deluge.program.type.occlusion");
      }

      if (!programType.isStandard() && !programType.isConsole()) {
        throw new DelugeException("deluge.program.type.exclusion");
      }
    }
  }
}
