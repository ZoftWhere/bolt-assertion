package app.zoftwhere.bolt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * A Bolt {@link InputStream} for cross encoding use.
 *
 * @since 4.0.0
 */
class BoltInputStream extends InputStream {

    private final InputStreamReader reader;

    private final Charset destination;

    private byte[] buffer;

    private int index = 0;

    private int size = 0;

    BoltInputStream(InputStream inputStream, Charset source, Charset destination) {
        this.destination = destination;
        this.reader = new InputStreamReader(inputStream, source);
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

        int v = buffer[index] & 0xff;
        ++index;
        return v;
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
