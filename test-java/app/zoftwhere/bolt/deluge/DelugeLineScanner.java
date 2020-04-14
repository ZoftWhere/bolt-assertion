package app.zoftwhere.bolt.deluge;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * <p>Deluge line scanner class.
 * </p>
 *
 * @since 7.1.0
 */
class DelugeLineScanner implements AutoCloseable {

    /** Underlying scanner instance. */
    private final Scanner scanner;

    /** Flag to signal if, when closing, the internal scanner instance should be auto-closed. */
    private final boolean autoClose;

    /**
     * Helper method for returning human-readable characters for select characters.
     *
     * @param input text to escape
     * @return text with select characters replaced with human-readable versions.
     * @since 7.1.0
     */
    static String escapeString(String input) {
        StringBuilder builder = new StringBuilder();
        input.codePoints().forEach(i -> {
            if (i == '\ufeff') {
                //noinspection SpellCheckingInspection
                builder.append("\\ufeff");
            }
            else if (i == '\\') { builder.append("\\\\"); }
            else if (i == '\r') { builder.append("\\r"); }
            else if (i == '\n') { builder.append("\\n"); }
            else if (i == '\t') { builder.append("\\t"); }
            else if (i == '\u2028') { builder.append("\\u2028"); }
            else if (i == '\u2029') { builder.append("\\u2029"); }
            else if (i == '\u0085') { builder.append("\\u0085"); }
            else { builder.appendCodePoint(i); }
        });
        return builder.toString();
    }

    /**
     * <p>Constructor for use with a scanner instance.
     * </p>
     * <p>The {@link DelugeLineScanner} will not close the underlying scanner.
     * </p>
     *
     * @param scanner input {@link Scanner}
     */
    DelugeLineScanner(Scanner scanner) {
        this.scanner = scanner;
        this.autoClose = false;
    }

    /**
     * <p>Constructor for use with an {@link InputStream}.
     * </p>
     * <p>Closing the {@link DelugeLineScanner} will not close the underlying {@link InputStream}.
     * </p>
     *
     * @param inputStream input {@link InputStream}
     * @param charset     character encoding of supplied {@link InputStream}
     */
    DelugeLineScanner(InputStream inputStream, Charset charset) {
        this.scanner = new Scanner(inputStream, charset.name());
        this.autoClose = true;
    }

    /**
     * <p>Returns the first line of the input.
     * </p>
     * <p>This method will remove a leading UTF-16 BOM character if present.</p>
     *
     * @return first line of the input
     * @since 7.1.0
     */
    String firstLine() {
        // Check Byte-Order-Mark and for empty first line.
        scanner.useDelimiter("");
        scanner.skip("\ufeff?");
        if (scanner.hasNext("\\R")) {
            scanner.useDelimiter("\\R");
            return "";
        }

        // Check for empty input.
        scanner.useDelimiter("\\R");
        if (!scanner.hasNext()) {
            return "";
        }

        return scanner.next();
    }

    boolean hasNextLine() {
        return scanner.hasNext() || scanner.hasNextLine();
    }

    String nextLine() {
        if (scanner.hasNext()) {
            return scanner.next();
        }
        return scanner.nextLine();
    }

    @Override
    public void close() {
        if (autoClose) {
            scanner.close();
        }
    }

}
