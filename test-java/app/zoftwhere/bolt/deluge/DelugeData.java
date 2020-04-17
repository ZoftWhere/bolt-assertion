package app.zoftwhere.bolt.deluge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Objects;

import app.zoftwhere.bolt.BoltPlaceHolder;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;

import static app.zoftwhere.bolt.BoltTestHelper.isOrHasNull;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.ARRAY;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.RESOURCE;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.RESOURCE_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.STREAM;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.STREAM_ENCODED;
import static java.nio.charset.StandardCharsets.UTF_8;

class DelugeData {

    /** New line definition for parsing that allows testing to be system agnostic. */
    private static final String NEW_LINE = "\r\n";

    private final DataType type;

    private final String[] array;

    private final InputStreamSupplier supplier;

    private final String resource;

    private final Class<?> withClass;

    private final Charset charset;

    private final Exception exception;

    private final BoltPlaceHolder<Boolean> openFlag = new BoltPlaceHolder<>(false);

    private final BoltPlaceHolder<Boolean> closedFlag = new BoltPlaceHolder<>(false);

    static DelugeData forStringArray(String[] data) {
        return new DelugeData(ARRAY, data, null, null, false, UTF_8);
    }

    static DelugeData forInputStream(String[] data) {
        return new DelugeData(STREAM, data, null, null, false, UTF_8);
    }

    static DelugeData forInputStream(String[] data, Charset charset) {
        return new DelugeData(STREAM_ENCODED, data, null, null, true, charset);
    }

    static DelugeData forInputStream(Exception exception) {
        return new DelugeData(exception, false, UTF_8);
    }

    static DelugeData forInputStream(Exception exception, Charset charset) {
        return new DelugeData(exception, true, charset);
    }

    static DelugeData forResource(String resource, Class<?> withClass, String[] data) {
        return new DelugeData(RESOURCE, Objects.requireNonNull(data), resource, withClass, false, UTF_8);
    }

    static DelugeData forResource(String resource, Class<?> withClass, String[] data, Charset charset) {
        return new DelugeData(RESOURCE_ENCODED, Objects.requireNonNull(data), resource, withClass, true, charset);
    }

    static InputStreamSupplier newInputStreamSupplier(String[] input) {
        if (input == null) {
            return () -> null;
        }
        return forCloseable(input, UTF_8, new BoltPlaceHolder<>(false), new BoltPlaceHolder<>(false));
    }

    private DelugeData(DataType type, String[] array, String resource,
        Class<?> withClass, boolean hasCharset, Charset charset)
    {
        this.type = type;
        this.array = array;
        this.supplier = STREAM == type || STREAM_ENCODED == type
            ? forCloseable(array, charset, openFlag, closedFlag)
            : null;
        this.resource = resource;
        this.withClass = withClass;
        this.charset = charset;
        this.exception = null;

        if (!hasCharset && charset != UTF_8) {
            throw new IllegalArgumentException("deluge.data.charset.utf-8.expected");
        }
    }

    private DelugeData(Exception exception, boolean hasCharset, Charset charset)
    {
        this.type = hasCharset ? STREAM_ENCODED : STREAM;
        this.array = null;
        this.supplier = () -> {
            throw exception;
        };
        this.resource = null;
        this.withClass = null;
        this.charset = charset;
        this.exception = exception;

        if (!hasCharset && charset != UTF_8) {
            throw new IllegalArgumentException("deluge.data.charset.utf-8.expected");
        }
    }

    DataType type() {
        return type;
    }

    String[] array() {
        return array;
    }

    InputStreamSupplier streamSupplier() {
        return supplier;
    }

    String resource() {
        return resource;
    }

    Class<?> withClass() {
        return withClass;
    }

    Charset charset() {
        return charset;
    }

    Exception exception() {
        return exception;
    }

    void resetFlags() {
        openFlag.set(false);
        closedFlag.set(false);
    }

    boolean isOpened() {
        return openFlag.get();
    }

    boolean isClosed() {
        return closedFlag.get();
    }

    private static InputStreamSupplier forCloseable(String[] input, Charset charset, BoltPlaceHolder<Boolean> openFlag,
        BoltPlaceHolder<Boolean> closedFlag)
    {
        if (input == null) {
            return null;
        }

        if (isOrHasNull(input)) {
            return () -> null;
        }

        return new InputStreamSupplier() {
            @Override
            public InputStream get() throws Exception {
                openFlag.set(true);

                try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    if (input.length > 0) {
                        try (OutputStreamWriter writer = new OutputStreamWriter(output, charset)) {
                            writer.append(input[0]);
                            for (int i = 1, s = input.length; i < s; i++) {
                                writer.append(NEW_LINE);
                                writer.append(input[i]);
                            }
                            writer.flush();
                        }
                    }

                    return new ByteArrayInputStream(output.toByteArray()) {
                        @Override
                        public void close() throws IOException {
                            closedFlag.set(true);
                            super.close();
                        }
                    };
                }
            }
        };
    }

    enum DataType {
        ARRAY,
        STREAM,
        STREAM_ENCODED,
        RESOURCE,
        RESOURCE_ENCODED
    }

}
