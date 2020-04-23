package app.zoftwhere.bolt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * <p>A Bolt Array {@link InputStream} for {@link String} array use.
 * </p>
 * <p>This is a package-private class for providing this functionality.
 * </p>
 *
 * @since 11.1.0
 */
class BoltArrayInputStream extends InputStream {

    private final String[] array;

    private final Charset charset;

    private int row;

    private int rowMax;

    private int column;

    private int columnMax;

    private byte[] newLine;

    private byte[] buffer;

    /**
     * Constructor for creating an {@link InputStream} from an array of {@link String}.
     *
     * @param array   {@code String} array
     * @param charset character encoding for {@code InputStream}
     */
    BoltArrayInputStream(String[] array, Charset charset) {
        this.array = array;
        this.charset = charset;
        this.row = 0;
        this.rowMax = array.length - 1;
        this.column = 0;
        this.newLine = "\r\n".getBytes(charset);
        this.buffer = array.length > 0 ? array[0].getBytes(charset) : new byte[0];
        this.columnMax = buffer.length - 1;
        if (columnMax == -1) {
            if (row < rowMax) {
                row++;
                buffer = array[row].getBytes(charset);
                column = -2;
                columnMax = buffer.length - 1;
            }
            else {
                column = 0;
                columnMax = -1;
            }
        }
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    public int read() throws IOException {
        if (!hasNext()) {
            return -1;
        }
        return next() & 0xff;
    }

    private boolean hasNext() {
        return column <= columnMax || row < rowMax;
    }

    private byte next() {
        if (column < 0) {
            return newLine[newLine.length + column++];
        }

        if (column > columnMax) {
            row++;
            buffer = array[row].getBytes(charset);
            column = 1 - newLine.length;
            columnMax = buffer.length - 1;
            return newLine[0];
        }

        byte next = buffer[column++];
        if (column > columnMax) {
            row++;
            if (row > rowMax) {
                column = 0;
                columnMax = 1 - newLine.length;
                return next;
            }
            buffer = array[row].getBytes(charset);
            column = 0 - newLine.length;
            columnMax = buffer.length - 1;
        }
        return next;
    }

}
