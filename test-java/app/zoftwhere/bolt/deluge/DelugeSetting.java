package app.zoftwhere.bolt.deluge;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.Runner;

/**
 * <p>DelugeSetting class.</p>
 *
 * @author Osmund
 * @since 11.0.0
 */
public class DelugeSetting {

    private static final Charset ENCODING = Runner.DEFAULT_ENCODING;

    /**
     * Return instance with user defined default encoding.
     *
     * @param setting  setting to update
     * @param encoding user defined default encoding
     * @return instance with user defined default encoding
     * @since 11.4.0
     */
    public static DelugeSetting withEncoding(DelugeSetting setting, Charset encoding) {
        final var hasEncoding = true;
        final var hasArgumentArray = setting.hasArgumentArray;
        final var argumentArray = setting.argumentArray;
        final var error = setting.error;
        final var hasCharSet = setting.hasCharSet;
        final var charset = setting.charset;
        return new DelugeSetting(hasEncoding, encoding, hasArgumentArray, argumentArray, error, hasCharSet, charset);
    }

    /**
     * Return instance with default settings.
     *
     * @return instance with default settings
     * @since 11.0.0
     */
    static DelugeSetting from() {
        return new DelugeSetting(false, ENCODING, false, null, null, false, ENCODING);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param charset    character encoding
     * @param isEncoding flag to signal if stream should be passed with encoding
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.0.0
     */
    static DelugeSetting from(Charset charset, boolean isEncoding) {
        if (isEncoding) {
            return new DelugeSetting(true, charset, false, null, null, false, ENCODING);
        }
        else {
            return new DelugeSetting(false, ENCODING, false, null, null, true, charset);
        }
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param argumentArray program argument array
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.0.0
     */
    static DelugeSetting from(String[] argumentArray) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, null, false, ENCODING);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param error loading program data exception
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.0.0
     */
    static DelugeSetting from(Exception error) {
        return new DelugeSetting(false, ENCODING, false, null, error, false, ENCODING);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param argumentArray program argument array
     * @param error         loading program data exception
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.0.0
     */
    static DelugeSetting from(String[] argumentArray, Exception error) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, error, false, ENCODING);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param defaultEncoding default character encoding to use
     * @param charset         program data character encoding
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.0.0
     */
    static DelugeSetting from(Charset defaultEncoding, Charset charset) {
        return new DelugeSetting(true, defaultEncoding, false, null, null, true, charset);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param argumentArray program argument array
     * @param charset       program data character encoding
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.0.0
     */
    static DelugeSetting from(String[] argumentArray, Charset charset) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, null, true, charset);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param error   loading program data exception
     * @param charset program data character encoding
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.0.0
     */
    static DelugeSetting from(Exception error, Charset charset) {
        return new DelugeSetting(false, ENCODING, false, null, error, true, charset);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param argumentArray program argument array
     * @param error         loading program data exception
     * @param charset       program data character encoding
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.0.0
     */
    static DelugeSetting from(String[] argumentArray, Exception error, Charset charset) {
        return new DelugeSetting(false, ENCODING, true, argumentArray, error, true, charset);
    }

    private final boolean hasArgumentArray;

    private final String[] argumentArray;

    private final Exception error;

    private final boolean hasCharSet;

    private final Charset charset;

    private final Charset encoding;

    /**
     * Constructor for DelugeSetting (private).
     *
     * @param hasDefaultEncoding has encoding indicator
     * @param encoding           default character encoding
     * @param hasArgumentArray   has argument array indicator
     * @param argumentArray      argument array
     * @param error              program error
     * @param hasCharSet         has program input character encoding
     * @param charset            program input character encoding
     */
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
        return error != null;
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
