package app.zoftwhere.bolt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * <p>Bolt line scanner class.
 * </p>
 * <p>This is a test-only class for being able to read and escape data in an editor-like fashion.
 * </p>
 *
 * @since 6.0.0
 */
public class BoltLineScanner implements AutoCloseable {

    private final Scanner scanner;

    public static String escapeString(String input) {
        return input.replaceAll("\"", "\\\\\"")
            .replaceAll("\r\n", "\\\\n")
            .replaceAll("\r", "\\\\n")
            .replaceAll("\n", "\\\\n")
            .replaceAll("\t", "\\\\t");
    }

    public BoltLineScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public BoltLineScanner(InputStream inputStream, Charset charset) {
        this.scanner = new Scanner(inputStream, charset.name());
    }

    public String firstLine() {
        // Check for empty first line.
        scanner.useDelimiter("");
        if (scanner.hasNext("\n")) {
            scanner.useDelimiter("\r?\n");
            return "";
        }

        // Check for empty input.
        scanner.useDelimiter("\r?\n");
        if (!scanner.hasNext()) {
            return "";
        }

        return scanner.next();
    }

    public boolean hasNextLine() {
        return scanner.hasNext() || scanner.hasNextLine();
    }

    public String nextLine() {
        if (scanner.hasNext()) {
            return scanner.next();
        }
        return scanner.nextLine();
    }

    @Override
    public void close() {
        this.scanner.close();
    }

}
