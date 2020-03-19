package app.zoftwhere.bolt.deluge;

@SuppressWarnings("unused")
public class DelugeResult {

    private final String[] output;

    private final Exception exception;

    private final String exceptionClass;

    private final String exceptionMessage;

    private final Throwable cause;

    private final String causeClass;

    private final String causeMessage;

    public DelugeResult(String[] output) {
        this.output = output;
        this.exception = null;
        this.exceptionClass = null;
        this.exceptionMessage = null;
        this.cause = null;
        this.causeClass = null;
        this.causeMessage = null;
    }

    public DelugeResult(String[] output, Exception e) {
        this.output = output;
        this.exception = e;
        this.exceptionClass = getClass(e);
        this.exceptionMessage = getMessage(e);
        this.cause = e != null ? e.getCause() : null;
        this.causeClass = getClass(cause);
        this.causeMessage = getMessage(cause);
    }

    public DelugeResult(String[] output, String exceptionClass, String exceptionMessage, Throwable cause) {
        this.output = output;
        this.exception = new Exception(exceptionMessage, cause);
        this.exceptionClass = exceptionClass;
        this.exceptionMessage = exceptionMessage;
        this.cause = cause;
        this.causeClass = getClass(cause);
        this.causeMessage = getMessage(cause);
    }

    public String[] output() {
        return output;
    }

    public Exception exception() {
        return exception;
    }

    public String exceptionClass() {
        return exceptionClass;
    }

    public String exceptionMessage() {
        return exceptionMessage;
    }

    public Throwable cause() {
        return cause;
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

    public String causeClass() {
        return this.causeClass;
    }

    public String causeMessage() {
        return this.causeClass;
    }

}
