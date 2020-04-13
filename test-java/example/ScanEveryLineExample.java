package example;

import java.io.PrintStream;
import java.util.Scanner;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

/**
 * Scan Every Line example.
 *
 * @since 7.1.0
 */
class ScanEveryLineExample {

    private final Runner runner = new Runner();

    @Test
    void testBlank() {
        runner //
            .run(ScanEveryLineExample::getEveryLine)
            .input(" ")
            .expected("[ ]")
            .assertSuccess();
    }

    @Test
    void testEmpty() {
        runner //
            .run(ScanEveryLineExample::getEveryLine)
            .input("")
            .expected("[]")
            .assertSuccess();
    }

    @Test
    void testCase() {
        runner //
            .run(ScanEveryLineExample::getEveryLine)
            .input("", "Get them all.", "")
            .expected("[]", "[Get them all.]", "[]")
            .assertSuccess();
    }

    @Test
    void testSeparator() {
        runner //
            .run(ScanEveryLineExample::getEveryLine)
            .input("system\r" + "\r\n" + "\n" + "and\u2028" + "unicode\u2029" + "agnostic\u0085" + "")
            .expected("[system]", "[]", "[]", "[and]", "[unicode]", "[agnostic]", "[]")
            .assertSuccess();
    }

    /**
     * Example for running a program with scanner against fixed/file/resource input.
     *
     * @param scanner     scanner
     * @param printStream print stream
     */
    private static void getEveryLine(Scanner scanner, PrintStream printStream) {
        String line = firstLine(scanner);
        printStream.print("[" + line + "]");

        while (hasNextLine(scanner)) {
            line = nextLine(scanner);
            printStream.println();
            printStream.print("[" + line + "]");
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
        return scanner.nextLine();
    }

}
