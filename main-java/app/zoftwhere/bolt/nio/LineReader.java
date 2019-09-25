package app.zoftwhere.bolt.nio;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("WeakerAccess")
public class LineReader extends Reader {

    @SuppressWarnings("FieldCanBeLocal")
    private final int defaultExpectedLineLength = 80;

    private final Object lock;

    private Reader reader;

    private char[] buffer;

    /** Number of chars in buffer. */
    private int charCount;

    /** Index reading from buffer. */
    private int charIndex;

    /** Last line empty. */
    private boolean lastLineEmpty = true;

    /** If the next character is a line feed (\n), skip it. */
    private boolean skipLF = false;

    public LineReader(Reader reader) {
        this(reader, 8192);
    }

    public LineReader(Reader reader, int size) {
        if (size <= 0) { throw new IllegalArgumentException("Buffer size <= 0"); }
        this.reader = reader;
        this.lock = super.lock;
        buffer = new char[size];
        charIndex = charCount = 0;
    }

    /** Checks to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
        if (reader == null) { throw new IOException("Stream closed"); }
    }

    public boolean hasNext() {
        return lastLineEmpty || charIndex < charCount;
    }

    public String readLine() throws IOException {
        return this.readLine(false);
    }

    public String readLine(boolean ignoreLF) throws IOException {
        StringBuilder builder = null;
        String string;
        int startChar;
        synchronized (lock) {
            ensureOpen();
            boolean omitLF = ignoreLF || skipLF;
            for (; ; ) {
                if (charIndex >= charCount) { fill(); }
                if (charIndex >= charCount) { /* EOF */
                    if (builder != null && builder.length() > 0) {
                        lastLineEmpty = false;
                        return builder.toString();
                    }
                    else if (lastLineEmpty) {
                        lastLineEmpty = false;
                        return "";
                    }
                    else {
                        return null;
                    }
                }
                boolean eol = false;
                char c = 0;
                int i;

                /* Skip a leftover '\n', if necessary */
                if (omitLF && (buffer[charIndex] == '\n')) {
                    charIndex++;
                }

                skipLF = false;
                omitLF = false;

                for (i = charIndex; i < charCount; i++) {
                    c = buffer[i];
                    if ((c == '\n') || (c == '\r')) {
                        eol = true;
                        break;
                    }
                }

                startChar = charIndex;
                charIndex = i;
                if (eol) {
                    if (builder == null) {
                        string = new String(buffer, startChar, i - startChar);
                    }
                    else {
                        builder.append(buffer, startChar, i - startChar);
                        string = builder.toString();
                    }
                    charIndex++;
                    if (c == '\r') { skipLF = true;}
                    lastLineEmpty = true;
                    break;
                }
                if (builder == null) {
                    builder = new StringBuilder(defaultExpectedLineLength);
                }
                builder.append(buffer, startChar, i - startChar);
            }
        }
        return string;
    }

    public Stream<String> lines() {
        @SuppressWarnings("Convert2Diamond") final Iterator<String> iterator = new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return LineReader.this.hasNext();
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
        };
        final int characteristics = Spliterator.ORDERED | Spliterator.NONNULL;
        final Spliterator<String> spliterator = Spliterators.spliteratorUnknownSize(iterator, characteristics);
        final boolean parallelFlag = false;
        return StreamSupport.stream(spliterator, parallelFlag);
    }


    /** Fills the input buffer. */
    private void fill() throws IOException {
        int n;
        do { n = reader.read(buffer, 0, buffer.length);}while (n == 0);
        if (n > 0) {
            charCount = n;
            charIndex = 0;
        }
    }

    @Override
    public int read(char[] chars, int offset, int length) throws IOException {
        int n;
        synchronized (lock) {
            ensureOpen();
            if ((offset < 0) || (offset > chars.length) || (length < 0) || ((offset + length) > chars.length) || ((offset + length) < 0)) {
                throw new IndexOutOfBoundsException();
            }
            else if (length == 0) { return 0;}
            n = read1(chars, offset, length);
            if (n <= 0) { return n; }
            while ((n < length) && reader.ready()) {
                int n1 = read1(chars, offset + n, length - n);
                if (n1 <= 0) { break; }
                n += n1;
            }
        }
        return n;
    }

    /** Reads characters into a portion of an array, reading from the underlying stream if necessary. */
    private int read1(char[] chars, int off, int len) throws IOException {
        if (charIndex >= charCount) {
            /* If the requested length is at least as large as the buffer, */
            /* and if line feeds are not being skipped, */
            /* do not bother to copy the characters into the local buffer. */
            /* In this way buffered streams will cascade harmlessly. */
            if (len >= buffer.length && !skipLF) {
                return reader.read(chars, off, len);
            }
            fill();
        }
        if (charIndex >= charCount) {
            if (lastLineEmpty) {
                lastLineEmpty = false;
                return 0;
            }
            return -1;
        }
        if (skipLF) {
            skipLF = false;
            if (buffer[charIndex] == '\n') {
                charIndex++;
                lastLineEmpty = true;
                // Try to fill the buffer.
                if (charIndex >= charCount) {
                    fill();
                }
                // Failed (EOL).
                if (charIndex >= charCount) {
                    lastLineEmpty = false;
                    return 0;
                }
            }
        }
        int n = Math.min(len, charCount - charIndex);
        System.arraycopy(buffer, charIndex, chars, off, n);
        charIndex += n;
        return n;
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (reader == null) { return; }
            try {
                reader.close();
            }
            finally {
                reader = null;
                buffer = null;
            }
        }
    }

}
