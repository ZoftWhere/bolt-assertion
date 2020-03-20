package app.zoftwhere.bolt.deluge;

@SuppressWarnings("unused")
class DelugeResult {

    private final String[] output;

    private final Exception exception;

    private final String exceptionClass;

    private final String exceptionMessage;

    private final Throwable cause;

    private final String causeClass;

    private final String causeMessage;

    DelugeResult(String[] output) {
        this.output = output;
        this.exception = null;
        this.exceptionClass = null;
        this.exceptionMessage = null;
        this.cause = null;
        this.causeClass = null;
        this.causeMessage = null;
    }

    DelugeResult(String[] output, Exception e) {
        this.output = output;
        this.exception = e;
        this.exceptionClass = getClass(e);
        this.exceptionMessage = getMessage(e);
        this.cause = e != null ? e.getCause() : null;
        this.causeClass = getClass(cause);
        this.causeMessage = getMessage(cause);
    }

    DelugeResult(String[] output, String exceptionClass, String exceptionMessage, Throwable cause) {
        this.output = output;
        this.exception = new Exception(exceptionMessage, cause);
        this.exceptionClass = exceptionClass;
        this.exceptionMessage = exceptionMessage;
        this.cause = cause;
        this.causeClass = getClass(cause);
        this.causeMessage = getMessage(cause);
    }

    String[] output() {
        return output;
    }

    Exception exception() {
        return exception;
    }

    String exceptionClass() {
        return exceptionClass;
    }

    String exceptionMessage() {
        return exceptionMessage;
    }

    Throwable cause() {
        return cause;
    }

    String causeClass() {
        return this.causeClass;
    }

    String causeMessage() {
        return this.causeClass;
    }

    private String getClass(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        return throwable.getClass().getName();
    }

    private String getMessage(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        return throwable.getMessage();
    }

}
