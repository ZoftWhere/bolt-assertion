package app.zoftwhere.bolt.deluge;

/**
 * Bolt Assertion Deluge Exception.
 *
 * <p>{@code DelugeException} is a runtime-exception class for API exceptions.
 *
 * @author Osmund
 * @since 6.0.0
 */
public class DelugeException extends RuntimeException {

  static final long serialVersionUID = 0x22ae901317c5dcabL;

  /**
   * Constructor for DelugeException.
   *
   * <p>Creates a {@code DelugeException} with specified message.
   *
   * <p>The cause is automatically set to null.
   *
   * @param message error message
   * @since 6.0.0
   */
  public DelugeException(String message) {
    super(message, null);
  }

  /**
   * Constructor for DelugeException.
   *
   * <p>Creates a {@code DelugeException} with specified message and cause.
   *
   * @param message error message
   * @param cause error cause
   * @since 6.0.0
   */
  public DelugeException(String message, Throwable cause) {
    super(message, cause);
  }
}
