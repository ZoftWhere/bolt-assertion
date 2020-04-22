package app.zoftwhere.bolt.deluge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import app.zoftwhere.bolt.BoltPlaceHolder;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import org.junit.jupiter.api.Assertions;

import static app.zoftwhere.bolt.BoltTestHelper.isOrHasNull;
import static app.zoftwhere.bolt.deluge.DelugeDataType.ARRAY;
import static app.zoftwhere.bolt.deluge.DelugeDataType.ARRAY_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeDataType.RESOURCE;
import static app.zoftwhere.bolt.deluge.DelugeDataType.RESOURCE_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeDataType.STREAM;
import static app.zoftwhere.bolt.deluge.DelugeDataType.STREAM_ENCODED;

public class DelugeData {

    /** New line definition for parsing that allows testing to be system agnostic. */
    private static final String NEW_LINE = "\r\n";

    static DelugeData forStringArray(String[] data) {
        return new DelugeData(data);
    }

    static DelugeData forStringArray(String[] data, Charset charset) {
        return new DelugeData(data, charset);
    }

    static DelugeData forInputStream(String[] data, Charset charset, boolean withCharset) {
        DelugeDataType type = withCharset ? STREAM_ENCODED : STREAM;
        return new DelugeData(type, data, charset);
    }

    static DelugeData forInputStream(Exception exception) {
        return new DelugeData(STREAM, exception, null);
    }

    static DelugeData forInputStream(Exception exception, Charset charset) {
        return new DelugeData(STREAM_ENCODED, exception, charset);
    }

    static DelugeData forResource(String resource, Class<?> withClass) {
        return new DelugeData(RESOURCE, resource, withClass, null);
    }

    static DelugeData forResource(String resource, Class<?> withClass, Charset charset) {
        return new DelugeData(RESOURCE_ENCODED, resource, withClass, charset);
    }

    private final DelugeDataType type;
    private final String[] array;
    private final InputStreamSupplier supplier;
    private final String resource;
    private final Class<?> withClass;
    private final Charset charset;
    private final Exception error;
    private final BoltPlaceHolder<Boolean> openFlag = new BoltPlaceHolder<>(false);
    private final BoltPlaceHolder<Boolean> closedFlag = new BoltPlaceHolder<>(false);

    private DelugeData(String[] array) {
        this.type = ARRAY;
        this.array = array;
        this.supplier = () -> { throw new DelugeException("attach manual supplier"); };
        this.withClass = null;
        this.resource = null;
        this.charset = null;
        this.error = null;
    }

    private DelugeData(String[] array, Charset charset) {
        this.type = ARRAY_ENCODED;
        this.array = array;
        this.supplier = newInputStreamSupplier(charset, array);
        this.withClass = null;
        this.resource = null;
        this.charset = charset;
        this.error = null;
    }

    private DelugeData(DelugeDataType type, String[] array, Charset charset) {
        Assertions.assertTrue(type == STREAM || type == STREAM_ENCODED);
        this.type = type;
        this.array = array;
        this.supplier = newInputStreamSupplier(charset, array);
        this.withClass = null;
        this.resource = null;
        this.charset = type == STREAM ? null : charset;
        this.error = null;
    }

    private DelugeData(DelugeDataType type, String resource, Class<?> withClass, Charset charset) {
        Assertions.assertTrue(type == RESOURCE || type == RESOURCE_ENCODED);
        Assertions.assertTrue(type == RESOURCE_ENCODED || charset == null);
        this.type = type;
        this.array = null;
        this.supplier = () -> withClass.getResourceAsStream(resource);
        this.resource = resource;
        this.withClass = withClass;
        this.charset = charset;
        this.error = null;
    }

    private DelugeData(DelugeDataType type, Exception error, Charset charset) {
        Assertions.assertTrue(type == STREAM || type == STREAM_ENCODED);
        this.type = type;
        this.array = null;
        this.supplier = () -> { throw error; };
        this.resource = null;
        this.withClass = null;
        this.charset = charset;
        this.error = error;
    }

    public DelugeDataType type() {
        return type;
    }

    public String[] array() {
        return array;
    }

    public InputStreamSupplier streamSupplier() {
        return supplier;
    }

    public String resource() {
        return resource;
    }

    public Class<?> withClass() {
        return withClass;
    }

    boolean hasCharset() {
        return type == ARRAY_ENCODED || type == STREAM_ENCODED || type == RESOURCE_ENCODED;
    }

    Charset charset() {
        return charset;
    }

    boolean hasError() {
        return error != null;
    }

    Exception error() {
        return error;
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

    InputStreamSupplier newInputStreamSupplier(Charset charset) {
        return newInputStreamSupplier(charset, array);
    }

    private InputStreamSupplier newInputStreamSupplier(Charset charset, String[] input) {
        if (input == null) {
            return null;
        }

        if (charset == null) {
            return () -> null;
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

}
