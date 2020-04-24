package app.zoftwhere.bolt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static app.zoftwhere.bolt.BoltProvide.NEW_LINE;
import static java.nio.charset.StandardCharsets.UTF_16BE;

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

    private final byte[] newLine;

    private final int rowMax;

    private int row;

    private int columnMax;

    private int column;

    private byte[] buffer;

    /**
     * Constructor for creating an {@link InputStream} from an array of {@link String}.
     *
     * @param array   {@code String} array
     * @param charset character encoding for {@code InputStream}
     */
    BoltArrayInputStream(String[] array, Charset charset) {
        if (array.length <= 1) {
            this.array = array;
            this.charset = charset;
            this.newLine = new byte[0];
            this.buffer = array.length == 1 ? array[0].getBytes(charset) : new byte[0];
            this.rowMax = 0;
            this.row = 0;
            this.columnMax = buffer.length - 1;
            this.column = 0;
            return;
        }

        Charset baseCharset = charset.name().equals("UTF-16") ? UTF_16BE : charset;

        this.charset = baseCharset;
        this.array = array;
        this.newLine = NEW_LINE.getBytes(baseCharset);
        this.rowMax = array.length - 1;

        if (array[0].length() != 0) {
            this.buffer = array[0].getBytes(charset);
            this.row = 0;
            this.columnMax = buffer.length - 1;
            this.column = 0;
        }
        else if (charset.name().equals("UTF-16")) {
            this.buffer = (NEW_LINE + array[1]).getBytes(charset);
            this.row = 1;
            this.columnMax = buffer.length - 1;
            this.column = 0;
        }
        else {
            this.buffer = array[1].getBytes(charset);
            this.row = 1;
            this.columnMax = buffer.length - 1;
            this.column = -newLine.length;
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
                columnMax = -1;
                return next;
            }
            buffer = array[row].getBytes(charset);
            column = 0 - newLine.length;
            columnMax = buffer.length - 1;
        }
        return next;
    }

}
