package example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

import app.zoftwhere.bolt.runner.Runner;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"WeakerAccess"})
public class ScanEveryLineExample {

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

    /**
     * Example for running a program with scanner against fixed/file/resource input.
     *
     * @param scanner scanner
     * @param writer  writer
     * @throws IOException in the event an IOException occurs
     */
    public static void getEveryLine(Scanner scanner, BufferedWriter writer)
    throws IOException
    {
        String line = firstLine(scanner);
        writer.write("[" + line + "]");

        while (hasNextLine(scanner)) {
            line = nextLine(scanner);
            writer.newLine();
            writer.write("[" + line + "]");
        }
    }

    private static String firstLine(Scanner scanner) {
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
