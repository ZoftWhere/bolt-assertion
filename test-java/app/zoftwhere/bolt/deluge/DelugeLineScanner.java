package app.zoftwhere.bolt.deluge;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Scanner;

/**
 * <p>Deluge line scanner class.
 * </p>
 *
 * @since 7.1.0
 */
public class DelugeLineScanner implements AutoCloseable {

    /** Underlying scanner instance. */
    private final Scanner scanner;

    /** Flag to signal if, when closing, the internal scanner instance should be auto-closed. */
    private final boolean autoClose;

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
        Objects.requireNonNull(inputStream, "inputStream");
        Objects.requireNonNull(charset, "charset");
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
    public String firstLine() {
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

    public boolean hasMore() {
        return scanner.hasNext() || scanner.hasNextLine();
    }

    public String readLine() {
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
