package example;

import java.io.PrintStream;
import java.util.Scanner;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

/**
 * Scan Every Line example.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 2.0.0
 */
class ScanEveryLineExample {

    private final Runner runner = new Runner();

    @Test
    void testBlank() {
        runner //
            .run(ScanEveryLineExample::program)
            .input(" ")
            .expected("[ ]")
            .assertSuccess();
    }

    @Test
    void testEmpty() {
        runner //
            .run(ScanEveryLineExample::program)
            .input("")
            .expected("[]")
            .assertSuccess();
    }

    @Test
    void testCase() {
        runner //
            .run(ScanEveryLineExample::program)
            .input("", "Get them all.", "")
            .expected("[]", "[Get them all.]", "[]")
            .assertSuccess();
    }

    @Test
    void testSeparator() {
        runner //
            .run(ScanEveryLineExample::program)
            .input(
                "system\r" + "\r\n" + "\n" +
                    "and\u2028" + "unicode\u2029" +
                    "agnostic\u0085" + "\f"
            )
            .expected(
                "[system]", "[]", "[]",
                "[and]", "[unicode]",
                "[agnostic]", "[]", "[]"
            )
            .assertSuccess();
    }

    @Test
    void testByteOrderMark() {
        runner //
            .run(ScanEveryLineExample::program)
            .input(
                "\ufeff",
                "Retain leading Zero-Width-Space.",
                "Zero-Width-Space{\ufeff}"
            )
            .expected(
                "[\ufeff]",
                "[Retain leading Zero-Width-Space.]",
                "[Zero-Width-Space{\ufeff}]"
            )
            .assertSuccess();
    }

    /**
     * Example for running a program with scanner against fixed/file/resource input.
     *
     * @param scanner program {@link java.util.Scanner}
     * @param out     program {@link java.io.PrintStream}
     */
    private static void program(Scanner scanner, PrintStream out) {
        String line = firstLine(scanner);
        out.print("[" + line + "]");

        while (hasNextLine(scanner)) {
            line = nextLine(scanner);
            out.println();
            out.print("[" + line + "]");
        }
    }

    private static String firstLine(Scanner scanner) {
        // Check for empty first line.
        scanner.useDelimiter("");
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

    private static boolean hasNextLine(Scanner scanner) {
        return scanner.hasNext() || scanner.hasNextLine();
    }

    private static String nextLine(Scanner scanner) {
        if (scanner.hasNext()) {
            return scanner.next();
        }

        // Check for trailing form-feed character.
        scanner.skip("\f?");
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "";
    }

}
