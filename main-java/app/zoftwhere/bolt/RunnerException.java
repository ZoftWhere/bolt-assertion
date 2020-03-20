package app.zoftwhere.bolt;

/**
 * A Runner Assertion Exception class for API exceptions.
 */
public class RunnerException extends RuntimeException {

    static final long serialVersionUID = 0x407c5c6e5485db90L;

    /**
     * <p>Creates a {@link RunnerException} with specified message.
     * </p>
     * <p>The cause is automatically set to null.</p>
     *
     * @param message error message
     */
    public RunnerException(String message) {
        super(message, null);
    }

    /**
     * <p>Creates a {@link RunnerException} with specified message and cause.
     * </p>
     *
     * @param message error message
     * @param cause   error cause
     */
    public RunnerException(String message, Throwable cause) {
        super(message, cause);
    }

}
