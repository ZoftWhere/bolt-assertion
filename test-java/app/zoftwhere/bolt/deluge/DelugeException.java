package app.zoftwhere.bolt.deluge;

public final class DelugeException extends RuntimeException {

    public DelugeException(String message) {
        super(message);
    }

    @SuppressWarnings("WeakerAccess")
    public DelugeException(String message, Throwable cause) {
        super(message, cause);
    }

}
