package app.zoftwhere.bolt;

/**
 * <p>Bolt Assertion Runner Exception.
 * </p>
 * <p>{@code RunnerException} is a runtime-exception class for API exceptions.
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public class RunnerException extends RuntimeException {

    static final long serialVersionUID = 0x407c5c6e5485db90L;

    /**
     * <p>Constructor for RunnerException.
     * </p>
     * <p>Creates a {@code RunnerException} with specified message.
     * </p>
     * <p>The cause is automatically set to null.
     * </p>
     *
     * @param message error message
     * @since 6.0.0
     */
    public RunnerException(String message) {
        super(message, null);
    }

    /**
     * <p>Constructor for RunnerException.
     * </p>
     * <p>Creates a {@code RunnerException} with specified message and cause.
     * </p>
     *
     * @param message error message
     * @param cause   error cause
     * @since 6.0.0
     */
    public RunnerException(String message, Throwable cause) {
        super(message, cause);
    }

}
