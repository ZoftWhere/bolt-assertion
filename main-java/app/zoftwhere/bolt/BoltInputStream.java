package app.zoftwhere.bolt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * <p>A Bolt {@link InputStream} for cross encoding use.
 * </p>
 * <p>This is a package-private class for providing this functionality.
 * </p>
 *
 * @since 4.0.0
 */
class BoltInputStream extends InputStream {

    private final InputStreamReader reader;

    private final Charset destination;

    private byte[] buffer;

    private int index = 0;

    private int size = 0;

    /**
     * Constructor for creating an {@link java.io.InputStream} from {@link java.io.InputStream} transcoder.
     *
     * @param inputStream {@link java.io.InputStream} to transcode
     * @param source      character encoding of source {@link java.io.InputStream}
     * @param destination character encoding of target {@link app.zoftwhere.bolt.BoltInputStream}
     * @since 4.0.0
     */
    BoltInputStream(InputStream inputStream, Charset source, Charset destination) {
        if (!"UTF-16".equals(destination.name())) {
            this.destination = destination;
            this.reader = new InputStreamReader(inputStream, source);
            return;
        }

        this.destination = StandardCharsets.UTF_16BE;
        this.reader = new InputStreamReader(inputStream, source);

        // Pad non-empty text with UTF-16 BOM (UTF-16BE encoding);
        try {
            fill();
            if (size > 0) {
                byte[] newBuffer = new byte[size + 2];
                newBuffer[0] = -2;
                newBuffer[1] = -1;
                System.arraycopy(buffer, 0, newBuffer, 2, size);
                buffer = newBuffer;
                size += 2;
            }
        }
        catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int read() throws IOException {
        if (index >= size) {
            fill();
            if (index >= size) {
                return -1;
            }
        }

        return buffer[index++] & 0xff;
    }

    /**
     * Fills the buffer with code-point-to-byte conversion.
     *
     * @throws IOException when cannot read from reader.
     */
    private void fill() throws IOException {
        index = 0;
        size = 0;

        int f1 = reader.read();
        if (f1 == -1) {
            return;
        }

        if (!Character.isSurrogate((char) f1)) {
            buffer = Character.toString((char) f1).getBytes(destination);
            size = buffer.length;
            return;
        }

        int f2 = reader.read();
        buffer = (new String(new char[] {(char) f1, (char) f2})).getBytes(destination);
        size = buffer.length;
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        reader.close();
    }

}
