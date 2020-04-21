package app.zoftwhere.bolt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>Bolt Reader for parsing input in an editor-like fashion.
 * </p>
 * <p>This is a package-private class for providing this functionality.
 * </p>
 *
 * @since 4.0.0
 */
class BoltReader extends Reader implements Iterator<String> {

    /**
     * Static helper method for retrieving text lines as a {@link List} of type {@link String}.
     *
     * @param supplier {@link BoltReader} supplier
     * @return text lines as a {@link List} of type {@link String}
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
     * Static helper method for retrieving text lines as an array of type {@link String}.
     *
     * @param supplier {@link BoltReader} supplier
     * @return text lines as an array of type {@link String}
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

    private final Object lock;

    private final InputStreamReader reader;

    /** First line read. */
    private boolean firstLine = true;

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
        this.reader = new InputStreamReader(new ByteArrayInputStream(data), charset);
        this.lock = super.lock;
    }

    /**
     * Constructor for input stream.
     *
     * @param inputStream input stream for text
     * @param charset     character encoding of {@link InputStream}
     * @since 4.0.0
     */
    BoltReader(InputStream inputStream, Charset charset) {
        if (inputStream == null) {
            throw new RunnerException("bolt.runner.reader.input.stream.null");
        }
        if (charset == null) {
            throw new RunnerException("bolt.runner.reader.charset.null");
        }
        this.reader = new InputStreamReader(inputStream, charset);
        this.lock = super.lock;
    }

    /**
     * <p>Check if there is another line of text.
     * </p>
     *
     * @return Returns true if there is another line, false otherwise.
     */
    @Override
    public boolean hasNext() {
        try {
            synchronized (lock) {
                return lastLineEmpty || ready();
            }
        }
        catch (IOException ignore) {
            return false;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean ready() throws IOException {
        boolean flag;
        synchronized (lock) {
            flag = reader.ready();
        }
        return flag;
    }

    /** {@inheritDoc} */
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

        synchronized (lock) {
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

                if (c == '\n') {
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
        }

        // Remove UTF-16 Byte Order Mark from start of first line.
        if (firstLine) {
            firstLine = false;
            if (builder.length() > 0 && builder.substring(0, 1).equals("\ufeff")) {
                return builder.substring(1);
            }
        }

        return builder.toString();
    }

    private Stream<String> lines() {
        final int characteristics = Spliterator.ORDERED | Spliterator.NONNULL;
        final Spliterator<String> spliterator = Spliterators.spliteratorUnknownSize(this, characteristics);
        final boolean parallelFlag = false;
        return StreamSupport.stream(spliterator, parallelFlag);
    }

    private List<String> list() {
        return lines().collect(Collectors.toList());
    }

    private String[] array() {
        return list().toArray(new String[] { });
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("RedundantThrows")
    public int read() throws IOException {
        return -1;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings({"RedundantThrows", "NullableProblems"})
    public int read(char[] chars, int offset, int length) throws IOException {
        return -1;
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        synchronized (lock) {
            reader.close();
        }
    }

}
