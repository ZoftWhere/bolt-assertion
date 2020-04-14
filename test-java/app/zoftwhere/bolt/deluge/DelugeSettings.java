package app.zoftwhere.bolt.deluge;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

class DelugeSettings {

    private final boolean hasArgumentArray;

    private final String[] argumentArray;

    private final boolean hasError;

    private final Exception error;

    private final boolean hasCharSet;

    private final Charset charset;

    static DelugeSettings from(Exception error) {
        return new DelugeSettings(false, null, true, error, false, UTF_8);
    }

    static DelugeSettings from(Exception throwable, Charset charset) {
        return new DelugeSettings(false, null, true, throwable, true, charset);
    }

    static DelugeSettings from(String[] argumentArray) {
        return new DelugeSettings(true, argumentArray, false, null, false, UTF_8);
    }

    static DelugeSettings from(String[] argumentArray, Exception throwable) {
        return new DelugeSettings(true, argumentArray, true, throwable, false, UTF_8);
    }

    static DelugeSettings from(String[] argumentArray, Charset charset) {
        return new DelugeSettings(true, argumentArray, false, null, true, charset);
    }

    static DelugeSettings from(String[] argumentArray, Exception error, Charset charset) {
        return new DelugeSettings(true, argumentArray, true, error, true, charset);
    }

    private DelugeSettings(
        boolean hasArgumentArray,
        String[] argumentArray,
        boolean hasError,
        Exception error,
        boolean hasCharSet,
        Charset charset)
    {
        this.hasArgumentArray = hasArgumentArray;
        this.argumentArray = argumentArray;
        this.hasError = hasError;
        this.error = error;
        this.hasCharSet = hasCharSet;
        this.charset = charset;

        if (!hasArgumentArray && argumentArray != null) {
            throw new IllegalArgumentException("deluge.program.settings.argument.null.expected");
        }

        if (!hasCharSet && charset != UTF_8) {
            throw new IllegalArgumentException("deluge.program.settings.charset.utf-8.expected");
        }

        if (hasError && error == null) {
            throw new IllegalArgumentException("deluge.program.non-null.throwable.expected");
        }
    }

    boolean hasArgumentArray() {
        return hasArgumentArray;
    }

    String[] argumentArray() {
        return argumentArray;
    }

    boolean hasError() {
        return hasError;
    }

    Exception error() {
        return error;
    }

    boolean hasCharSet() {
        return hasCharSet;
    }

    Charset charset() {
        return charset;
    }

}
