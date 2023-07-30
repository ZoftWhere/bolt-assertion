package app.zoftwhere.bolt;

/**
 * Bolt Assertion Runner Exception.
 *
 * <p>{@code RunnerException} is a runtime-exception class for API exceptions.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public class RunnerException extends RuntimeException {

  static final long serialVersionUID = 0x407c5c6e5485db90L;

  /**
   * Constructor for RunnerException.
   *
   * <p>Creates a {@code RunnerException} with specified message.
   *
   * <p>The cause is automatically set to null.
   *
   * @param message error message
   * @since 6.0.0
   */
  public RunnerException(String message) {
    super(message, null);
  }

  /**
   * Constructor for RunnerException.
   *
   * <p>Creates a {@code RunnerException} with specified message and cause.
   *
   * @param message error message
   * @param cause error cause
   * @since 6.0.0
   */
  public RunnerException(String message, Throwable cause) {
    super(message, cause);
  }
}
