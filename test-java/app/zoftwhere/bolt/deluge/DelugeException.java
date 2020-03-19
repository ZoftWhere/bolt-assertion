package app.zoftwhere.bolt.deluge;

@SuppressWarnings("WeakerAccess")
public final class DelugeException extends RuntimeException {

    @SuppressWarnings("unused")
    public DelugeException(String message) {
        super(message);
    }

    public DelugeException(String message, Throwable cause) {
        super(message, cause);
    }

}
