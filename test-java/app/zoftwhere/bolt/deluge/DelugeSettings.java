package app.zoftwhere.bolt.deluge;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.Runner;

public class DelugeSettings {

    private static final Charset ENCODING = Runner.DEFAULT_ENCODING;

    static DelugeSettings from() {
        return new DelugeSettings(false, ENCODING, false, null, null, false, ENCODING);
    }

    static DelugeSettings from(Charset charset, boolean isEncoding) {
        if (isEncoding) {
            return new DelugeSettings(true, charset, false, null, null, false, ENCODING);
        }
        else {
            return new DelugeSettings(false, ENCODING, false, null, null, true, charset);
        }
    }

    static DelugeSettings from(String[] argumentArray) {
        return new DelugeSettings(false, ENCODING, true, argumentArray, null, false, ENCODING);
    }

    static DelugeSettings from(Charset defaultEncoding, String[] argumentArray) {
        return new DelugeSettings(true, defaultEncoding, true, argumentArray, null, false, ENCODING);
    }

    static DelugeSettings from(Exception error) {
        return new DelugeSettings(false, ENCODING, false, null, error, false, ENCODING);
    }

    static DelugeSettings from(Charset defaultEncoding, Exception error) {
        return new DelugeSettings(true, defaultEncoding, false, null, error, false, ENCODING);
    }

    static DelugeSettings from(String[] argumentArray, Exception error) {
        return new DelugeSettings(false, ENCODING, true, argumentArray, error, false, ENCODING);
    }

    static DelugeSettings from(Charset defaultEncoding, String[] argumentArray, Exception error) {
        return new DelugeSettings(true, defaultEncoding, true, argumentArray, error, false, ENCODING);
    }

    static DelugeSettings from(Charset defaultEncoding, Charset charset) {
        return new DelugeSettings(true, defaultEncoding, false, null, null, true, charset);
    }

    static DelugeSettings from(String[] argumentArray, Charset charset) {
        return new DelugeSettings(false, ENCODING, true, argumentArray, null, true, charset);
    }

    static DelugeSettings from(Charset defaultEncoding, String[] argumentArray, Charset charset) {
        return new DelugeSettings(true, defaultEncoding, true, argumentArray, null, true, charset);
    }

    static DelugeSettings from(Exception error, Charset charset) {
        return new DelugeSettings(false, ENCODING, false, null, error, true, charset);
    }

    static DelugeSettings from(Charset defaultEncoding, Exception error, Charset charset) {
        return new DelugeSettings(true, defaultEncoding, false, null, error, true, charset);
    }

    static DelugeSettings from(String[] argumentArray, Exception error, Charset charset) {
        return new DelugeSettings(false, ENCODING, true, argumentArray, error, true, charset);
    }

    static DelugeSettings from(Charset defaultEncoding, String[] argumentArray, Exception error, Charset charset) {
        return new DelugeSettings(true, defaultEncoding, true, argumentArray, error, true, charset);
    }

    private final boolean hasArgumentArray;

    private final String[] argumentArray;

    private final boolean hasError;

    private final Exception error;

    private final boolean hasCharSet;

    private final Charset charset;

    private final boolean hasDefaultEncoding;

    private final Charset encoding;

    private DelugeSettings(
        boolean hasDefaultEncoding,
        Charset encoding,
        boolean hasArgumentArray,
        String[] argumentArray,
        Exception error,
        boolean hasCharSet,
        Charset charset)
    {
        this.hasDefaultEncoding = hasDefaultEncoding;
        this.encoding = encoding;
        this.hasArgumentArray = hasArgumentArray;
        this.argumentArray = argumentArray;
        this.hasError = error != null;
        this.error = error;
        this.hasCharSet = hasCharSet;
        this.charset = charset;

        if (!hasArgumentArray && argumentArray != null) {
            throw new IllegalArgumentException("deluge.program.settings.argument.null.expected");
        }

        if (!hasCharSet && charset != ENCODING) {
            throw new IllegalArgumentException("deluge.program.settings.charset.null.expected");
        }

        if (!hasDefaultEncoding && encoding != ENCODING) {
            throw new IllegalArgumentException("deluge.program.settings.encoding.utf-8.expected");
        }
    }

    public boolean hasEncoding() {
        return hasDefaultEncoding;
    }

    public Charset defaultEncoding() {
        return encoding;
    }

    public boolean hasArgumentArray() {
        return hasArgumentArray;
    }

    public String[] argumentArray() {
        return argumentArray;
    }

    public boolean hasError() {
        return hasError;
    }

    public Exception error() {
        return error;
    }

    public boolean hasCharSet() {
        return hasCharSet;
    }

    public Charset charset() {
        return charset;
    }

}
