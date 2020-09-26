package app.zoftwhere.bolt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * <p>Bolt Reader for parsing input in an editor-like fashion.
 * </p>
 * <p>This is a package-private class for providing this functionality.
 * </p>
 *
 * @since 4.0.0
 */
class BoltReader implements Iterator<String>, AutoCloseable {

    /**
     * Static helper method for retrieving text lines as a {@link java.util.List} of type {@link java.lang.String}.
     *
     * @param supplier {@link app.zoftwhere.bolt.BoltReader} supplier
     * @return text lines as a {@link java.util.List} of type {@link java.lang.String}
     * @since 4.0.0
     */
    static List<String> readList(Supplier<BoltReader> supplier) {
        try (BoltReader reader = supplier.get()) {
            return reader.list();
        }
        catch (Exception e) {
            throw new RunnerException("bolt.runner.reader.read.list", e);
        }
    }

    /**
     * Static helper method for retrieving text lines as an array of type {@link java.lang.String}.
     *
     * @param supplier {@link app.zoftwhere.bolt.BoltReader} supplier
     * @return text lines as an array of type {@link java.lang.String}
     * @since 4.0.0
     */
    static String[] readArray(Supplier<BoltReader> supplier) {
        try (BoltReader reader = supplier.get()) {
            return reader.array();
        }
        catch (Exception e) {
            throw new RunnerException("bolt.runner.reader.read.array", e);
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final int defaultExpectedLineLength = 80;

    private final boolean autoClose;

    private final Reader reader;

    /** Last line empty. */
    private boolean lastLineEmpty = true;

    /** If the next character is a line feed (\n), skip it. */
    private boolean skipLF = false;

    /**
     * Constructor for byte array data.
     *
     * @param data    byte array for text
     * @param charset character encoding of byte array
     * @since 4.0.0
     */
    BoltReader(byte[] data, Charset charset) {
        if (data == null) {
            throw new RunnerException("bolt.runner.reader.data.null");
        }

        if (charset == null) {
            throw new RunnerException("bolt.runner.reader.charset.null");
        }

        reader = new InputStreamReader(new ByteArrayInputStream(data), charset);
        autoClose = true;
    }

    /**
     * Constructor for input stream.
     *
     * @param inputStream input stream for text
     * @param charset     character encoding of {@link java.io.InputStream}
     * @since 4.0.0
     */
    BoltReader(InputStream inputStream, Charset charset) {
        if (inputStream == null) {
            throw new RunnerException("bolt.runner.reader.input.stream.null");
        }

        if (charset == null) {
            throw new RunnerException("bolt.runner.reader.charset.null");
        }

        reader = new InputStreamReader(inputStream, charset);
        autoClose = true;
    }

    /**
     * <p>Constructor for reader.
     * </p>
     * <p>Note that BoltReader does not close the reader provided.
     * </p>
     *
     * @param reader reader for reading text
     * @since 11.1.0
     */
    BoltReader(Reader reader) {
        this.reader = reader;
        autoClose = false;
    }

    /**
     * <p>Check if there is another line of text.
     * </p>
     *
     * @return Returns true if there is another line, false otherwise.
     */
    public boolean hasNext() {
        try {
            return lastLineEmpty || reader.ready();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String next() {
        try {
            return readLine();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    String readLine() throws IOException {
        StringBuilder builder = new StringBuilder(defaultExpectedLineLength);
        lastLineEmpty = false;

        while (true) {
            int v = reader.read();

            if (v == -1) {
                break;
            }

            char c = (char) v;

            if (c == '\n' && skipLF) {
                skipLF = false;
                lastLineEmpty = false;
                continue;
            }
            else {
                skipLF = false;
            }

            if (c == '\n' || c == '\f' || c == '\u0085' || c == '\u2028' || c == '\u2029') {
                lastLineEmpty = true;
                break;
            }

            if (c == '\r') {
                skipLF = true;
                lastLineEmpty = true;
                break;
            }

            builder.append(c);
        }

        return builder.toString();
    }

    private List<String> list() {
        List<String> list = new ArrayList<>(0);
        while (hasNext()) {
            list.add(next());
        }
        return list;
    }

    private String[] array() {
        return list().toArray(new String[] { });
    }

    @Override
    public void close() throws IOException {
        if (autoClose) {
            reader.close();
        }
    }

}
