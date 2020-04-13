package app.zoftwhere.bolt.deluge;

import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

class DelugeSettings {

    private final boolean hasArgumentArray;

    private final String[] argumentArray;

    private final boolean hasThrowable;

    private final Throwable throwable;

    private final boolean hasCharSet;

    private final Charset charset;

    static DelugeSettings from(Throwable throwable) {
        return new DelugeSettings(false, null, true, throwable, false, UTF_8);
    }

    static DelugeSettings from(Throwable throwable, Charset charset) {
        return new DelugeSettings(false, null, true, throwable, true, charset);
    }

    static DelugeSettings from(String[] argumentArray) {
        return new DelugeSettings(true, argumentArray, false, null, false, UTF_8);
    }

    static DelugeSettings from(String[] argumentArray, Throwable throwable) {
        return new DelugeSettings(true, argumentArray, true, throwable, false, UTF_8);
    }

    static DelugeSettings from(String[] argumentArray, Charset charset) {
        return new DelugeSettings(true, argumentArray, false, null, true, charset);
    }

    static DelugeSettings from(String[] argumentArray, Throwable throwable, Charset charset) {
        return new DelugeSettings(true, argumentArray, true, throwable, true, charset);
    }

    private DelugeSettings(
        boolean hasArgumentArray,
        String[] argumentArray,
        boolean hasThrowable,
        Throwable throwable,
        boolean hasCharSet,
        Charset charset)
    {
        this.hasArgumentArray = hasArgumentArray;
        this.argumentArray = argumentArray;
        this.hasThrowable = hasThrowable;
        this.throwable = throwable;
        this.hasCharSet = hasCharSet;
        this.charset = charset;

        if (!hasArgumentArray && argumentArray != null) {
            throw new IllegalArgumentException("deluge.program.settings.argument.null.expected");
        }

        if (!hasCharSet && charset != UTF_8) {
            throw new IllegalArgumentException("deluge.program.settings.charset.utf-8.expected");
        }

        if (hasThrowable && throwable == null) {
            throw new IllegalArgumentException("deluge.program.non-null.throwable.expected");
        }
    }

    boolean hasArgumentArray() {
        return hasArgumentArray;
    }

    String[] argumentArray() {
        return argumentArray;
    }

    boolean hasThrowable() {
        return hasThrowable;
    }

    Throwable throwable() {
        return throwable;
    }

    boolean hasCharSet() {
        return hasCharSet;
    }

    Charset charset() {
        return charset;
    }

}
