package app.zoftwhere.bolt.deluge;

/**
 * <p>Bolt Assertion Deluge Exception.
 * </p>
 * <p>{@code DelugeException} is a runtime-exception class for API exceptions.
 * </p>
 *
 * @author Osmund
 * @since 6.0.0
 */
public class DelugeException extends RuntimeException {

    static final long serialVersionUID = 0x22ae901317c5dcabL;

    /**
     * <p>Constructor for DelugeException.
     * </p>
     * <p>Creates a {@code DelugeException} with specified message.
     * </p>
     * <p>The cause is automatically set to null.
     * </p>
     *
     * @param message error message
     * @since 6.0.0
     */
    public DelugeException(String message) {
        super(message, null);
    }

    /**
     * <p>Constructor for DelugeException.
     * </p>
     * <p>Creates a {@code DelugeException} with specified message and cause.
     * </p>
     *
     * @param message error message
     * @param cause   error cause
     * @since 6.0.0
     */
    public DelugeException(String message, Throwable cause) {
        super(message, cause);
    }

}
