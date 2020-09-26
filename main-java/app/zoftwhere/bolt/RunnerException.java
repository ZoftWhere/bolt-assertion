package app.zoftwhere.bolt;

/**
 * Runner runtime-exception class for API exceptions.
 *
 * @since 6.0.0
 */
public class RunnerException extends RuntimeException {

    static final long serialVersionUID = 0x407c5c6e5485db90L;

    /**
     * <p>Creates a {@link app.zoftwhere.bolt.RunnerException} with specified message.
     * </p>
     * <p>The cause is automatically set to null.</p>
     *
     * @param message error message
     * @since 6.0.0
     */
    public RunnerException(String message) {
        super(message, null);
    }

    /**
     * <p>Creates a {@link app.zoftwhere.bolt.RunnerException} with specified message and cause.
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
