package app.zoftwhere.bolt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static app.zoftwhere.bolt.BoltProvide.NEW_LINE;
import static java.nio.charset.StandardCharsets.UTF_16BE;

/**
 * <p>Bolt Array Input Stream class.
 * </p>
 * <p>This is a package-private class for providing an input stream for a {@link java.lang.String} array.
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
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
     * <p>Constructor for BoltArrayInputStream (package-private).
     * </p>
     * <p>Creates an instance for an array of {@link java.lang.String}.
     * </p>
     *
     * @param array   input {@link java.lang.String} array
     * @param charset character encoding for {@link java.io.InputStream}
     * @since 11.1.0
     */
    BoltArrayInputStream(String[] array, Charset charset) {
        if (array.length <= 1) {
            this.array = array;
            this.charset = charset;
            newLine = new byte[0];
            buffer = array.length == 1 ? array[0].getBytes(charset) : new byte[0];
            rowMax = 0;
            row = 0;
            columnMax = buffer.length - 1;
            column = 0;
            return;
        }

        Charset baseCharset = charset.name().equals("UTF-16") ? UTF_16BE : charset;

        this.array = array;
        this.charset = baseCharset;
        newLine = NEW_LINE.getBytes(baseCharset);
        rowMax = array.length - 1;

        if (array[0].length() != 0) {
            buffer = array[0].getBytes(charset);
            row = 0;
            columnMax = buffer.length - 1;
            column = 0;
        }
        else if (charset.name().equals("UTF-16")) {
            buffer = (NEW_LINE + array[1]).getBytes(charset);
            row = 1;
            columnMax = buffer.length - 1;
            column = 0;
        }
        else {
            buffer = array[1].getBytes(charset);
            row = 1;
            columnMax = buffer.length - 1;
            column = -newLine.length;
        }
    }

    /** {@inheritDoc} */
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
            column = -newLine.length;
            columnMax = buffer.length - 1;
        }
        return next;
    }

}
