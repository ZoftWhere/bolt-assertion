package app.zoftwhere.bolt.deluge;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.Runner;

public class DelugeSetting {

    private static final Charset ENCODING = Runner.DEFAULT_ENCODING;

    static DelugeSetting from() {
        return new DelugeSetting(false, ENCODING, false, null, null, false, ENCODING);
    }

    static DelugeSetting from(Charset charset, boolean isEncoding) {
        if (isEncoding) {
            return new DelugeSetting(true, charset, false, null, null, false, ENCODING);
        }
        else {
            return new DelugeSetting(false, ENCODING, false, null, null, true, charset);
        }
    }

    static DelugeSetting from(String[] argumentArray) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, null, false, ENCODING);
    }

    static DelugeSetting from(Charset defaultEncoding, String[] argumentArray) {
        return new DelugeSetting(true, defaultEncoding, true, argumentArray, null, false, ENCODING);
    }

    static DelugeSetting from(Exception error) {
        return new DelugeSetting(false, ENCODING, false, null, error, false, ENCODING);
    }

    static DelugeSetting from(Charset defaultEncoding, Exception error) {
        return new DelugeSetting(true, defaultEncoding, false, null, error, false, ENCODING);
    }

    static DelugeSetting from(String[] argumentArray, Exception error) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, error, false, ENCODING);
    }

    static DelugeSetting from(Charset defaultEncoding, String[] argumentArray, Exception error) {
        return new DelugeSetting(true, defaultEncoding, true, argumentArray, error, false, ENCODING);
    }

    static DelugeSetting from(Charset defaultEncoding, Charset charset) {
        return new DelugeSetting(true, defaultEncoding, false, null, null, true, charset);
    }

    static DelugeSetting from(String[] argumentArray, Charset charset) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, null, true, charset);
    }

    static DelugeSetting from(Charset defaultEncoding, String[] argumentArray, Charset charset) {
        return new DelugeSetting(true, defaultEncoding, true, argumentArray, null, true, charset);
    }

    static DelugeSetting from(Exception error, Charset charset) {
        return new DelugeSetting(false, ENCODING, false, null, error, true, charset);
    }

    static DelugeSetting from(Charset defaultEncoding, Exception error, Charset charset) {
        return new DelugeSetting(true, defaultEncoding, false, null, error, true, charset);
    }

    static DelugeSetting from(String[] argumentArray, Exception error, Charset charset) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, error, true, charset);
    }

    static DelugeSetting from(Charset defaultEncoding, String[] argumentArray, Exception error, Charset charset) {
        return new DelugeSetting(true, defaultEncoding, true, argumentArray, error, true, charset);
    }

    private final boolean hasArgumentArray;

    private final String[] argumentArray;

    private final boolean hasError;

    private final Exception error;

    private final boolean hasCharSet;

    private final Charset charset;

    private final boolean hasDefaultEncoding;

    private final Charset encoding;

    private DelugeSetting(
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
            throw new IllegalArgumentException("deluge.program.setting.argument.null.expected");
        }

        if (!hasCharSet && charset != ENCODING) {
            throw new IllegalArgumentException("deluge.program.setting.charset.null.expected");
        }

        if (!hasDefaultEncoding && encoding != ENCODING) {
            throw new IllegalArgumentException("deluge.program.setting.encoding.utf-8.expected");
        }
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasEncoding() {
        return hasDefaultEncoding;
    }

    @SuppressWarnings("WeakerAccess")
    public Charset defaultEncoding() {
        return encoding;
    }

    public boolean hasArgumentArray() {
        return hasArgumentArray;
    }

    @SuppressWarnings("WeakerAccess")
    public String[] argumentArray() {
        return argumentArray;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasError() {
        return hasError;
    }

    public Exception error() {
        return error;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasCharSet() {
        return hasCharSet;
    }

    public Charset charset() {
        return charset;
    }

}
