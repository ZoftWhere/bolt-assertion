package app.zoftwhere.bolt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * A RunnerInputStream for cross encoding use.
 */
class RunnerInputStream extends InputStream {

    private final InputStreamReader reader;

    private final Charset destination;

    private byte[] buffer;

    private int index = 0;

    private int size = 0;

    RunnerInputStream(InputStream inputStream, Charset source, Charset destination) {
        this.destination = destination;
        this.reader = new InputStreamReader(inputStream, source);
    }

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

    @Override
    public void close() throws IOException {
        reader.close();
    }

}
