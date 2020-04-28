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

    static DelugeSetting from(Exception error) {
        return new DelugeSetting(false, ENCODING, false, null, error, false, ENCODING);
    }

    static DelugeSetting from(String[] argumentArray, Exception error) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, error, false, ENCODING);
    }

    static DelugeSetting from(Charset defaultEncoding, Charset charset) {
        return new DelugeSetting(true, defaultEncoding, false, null, null, true, charset);
    }

    static DelugeSetting from(String[] argumentArray, Charset charset) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, null, true, charset);
    }

    static DelugeSetting from(Exception error, Charset charset) {
        return new DelugeSetting(false, ENCODING, false, null, error, true, charset);
    }

    static DelugeSetting from(String[] argumentArray, Exception error, Charset charset) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, error, true, charset);
    }

    private final boolean hasArgumentArray;

    private final String[] argumentArray;

    private final boolean hasError;

    private final Exception error;

    private final boolean hasCharSet;

    private final Charset charset;

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

    DelugeSetting updateEncoding(Charset encoding) {
        return new DelugeSetting(true, encoding, hasArgumentArray, argumentArray, error, hasCharSet, charset);
    }

    Charset defaultEncoding() {
        return encoding;
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
