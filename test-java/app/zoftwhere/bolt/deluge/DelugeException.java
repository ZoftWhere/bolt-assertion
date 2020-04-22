package app.zoftwhere.bolt.deluge;

final class DelugeException extends RuntimeException {

    DelugeException(String message) {
        super(message);
    }

    DelugeException(String message, Throwable cause) {
        super(message, cause);
    }

}
