package app.zoftwhere.bolt;

import java.util.Objects;

/**
 * Bolt Utility class.
 *
 * <p>This is a package-private class for providing its functionality.
 *
 * @author Osmund
 * @version 11.4.0
 * @since 11.0.0
 */
abstract class BoltUtility {

  /**
   * Constructor for BoltUtility (private).
   *
   * @since 11.0.0
   */
  private BoltUtility() {}

  /**
   * Check if array contains a null element.
   *
   * @param array array to check
   * @return {@code true} if the array contains one or more null elements, {@code false} otherwise.
   * @since 11.0.0
   */
  static boolean arrayHasNull(String[] array) {
    for (final String item : Objects.requireNonNull(array, "array")) {
      if (item == null) {
        return true;
      }
    }
    return false;
  }
}
