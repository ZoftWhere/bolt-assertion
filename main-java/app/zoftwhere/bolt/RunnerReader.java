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

import app.zoftwhere.bolt.Runner.BoltAssertionException;

/**
 * @since 4.0.0
 */
class RunnerReader extends Reader implements Iterator<String> {

    @SuppressWarnings("FieldCanBeLocal")
    private final int defaultExpectedLineLength = 80;

    private final Object lock;

    private final InputStreamReader reader;

    /** Last line empty. */
    private boolean lastLineEmpty = true;

    /** If the next character is a line feed (\n), skip it. */
    private boolean skipLF = false;

    static List<String> readList(Supplier<RunnerReader> supplier) {
        try (RunnerReader reader = supplier.get()) {
            return reader.list();
        }
        catch (Throwable e) {
            throw new BoltAssertionException("bolt.runner.reader.read.list", e);
        }
    }

    static String[] readArray(Supplier<RunnerReader> supplier) {
        try (RunnerReader reader = supplier.get()) {
            return reader.array();
        }
        catch (Throwable e) {
            throw new BoltAssertionException("bolt.runner.reader.read.array", e);
        }
    }

    RunnerReader(byte[] data, Charset charset) {
        this(new ByteArrayInputStream(data), charset);
    }

    RunnerReader(InputStream inputStream, Charset charset) {
        this.reader = new InputStreamReader(inputStream, charset);
        this.lock = super.lock;
    }

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

    @Override
    public boolean ready() throws IOException {
        boolean flag;
        synchronized (lock) {
            flag = reader.ready();
        }
        return flag;
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
                    return builder.toString();
                }

                if (c == '\r') {
                    skipLF = true;
                    lastLineEmpty = true;
                    return builder.toString();
                }

                // Skip UTF-16 BOMs
                if (c == '\ufeff') {
                    continue;
                }

                builder.append(c);
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

    @Override
    @SuppressWarnings("RedundantThrows")
    public int read() throws IOException {
        return -1;
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    public int read(char[] chars, int offset, int length) throws IOException {
        return -1;
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            reader.close();
        }
    }

}
