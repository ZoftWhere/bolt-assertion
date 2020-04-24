package app.zoftwhere.bolt.deluge;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.Arrays;

import app.zoftwhere.bolt.RunnerException;
import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerResult;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.NEW_LINE;
import static app.zoftwhere.bolt.BoltTestHelper.escapeString;
import static app.zoftwhere.bolt.Runner.newRunner;
import static java.nio.charset.StandardCharsets.UTF_16;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DelugeLineScannerTest {

    private final RunnerInterface.RunnerResultConsumer consumer = (RunnerResult result) -> {
        if (result.isError()) {
            throw result.error().orElse(new Exception());
        }

        var errorMessage = result.message().orElse("") +
            NEW_LINE + "Expected : " + Arrays.toString(result.expected()) +
            NEW_LINE + "Found    : " + Arrays.toString(result.output());
        throw new RunnerException(errorMessage);
    };

    @Test
    void testEmpty() {
        var input = new String[] {""};
        var expected = new String[] {"[]"};
        testWithRunner(input, expected);
    }

    @Test
    void testNewLineFirst() {
        var input = new String[] {"", "2", ""};
        var expected = new String[] {"[][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testZeroWidthSpace() {
        var input = new String[] {"\ufeff", "2\ufeff", ""};
        var expected = new String[] {escapeString("[\ufeff][2\ufeff][]")};
        testWithRunner(input, expected);
    }

    @Test
    void testCarriageReturn() {
        var input = new String[] {"\r" + "\r" + "2\r" + ""};
        var expected = new String[] {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testLineFeed() {
        var input = new String[] {"\n" + "\n" + "2\n" + ""};
        var expected = new String[] {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testFormFeed() {
        var input = new String[] {"\f" + "\f" + "2\f" + ""};
        var expected = new String[] {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testWindowLine() {
        var input = new String[] {"\r\n" + "\r\n" + "2\r\n" + ""};
        var expected = new String[] {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testLineSeparator() {
        var input = new String[] {"\u2028" + "\u2028" + "2\u2028" + ""};
        var expected = new String[] {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testParagraphSeparator() {
        var input = new String[] {"\u2029" + "\u2029" + "2\u2029" + ""};
        var expected = new String[] {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testNextLine() {
        var input = new String[] {"\u0085" + "\u0085" + "2\u0085" + ""};
        var expected = new String[] {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testWithInputStream() {
        var input = "\ufeff";
        var data = input.getBytes(UTF_16);
        var inputStream = new ByteArrayInputStream(data);
        try (var scanner = new DelugeLineScanner(inputStream, UTF_16)) {
            assertEquals("\ufeff", scanner.firstLine());
            assertFalse(scanner.hasMore());
        }
    }

    private void testWithRunner(String[] input, String[] expected) {
        newRunner()
            .input(input)
            .run((scanner, out) -> {
                try (var lineScanner = new DelugeLineScanner(scanner)) {
                    program(lineScanner, out);
                }
            })
            .expected(expected)
            .onOffence(consumer);

        newRunner()
            .run((scanner, out) -> {
                try (var lineScanner = new DelugeLineScanner(scanner)) {
                    program(lineScanner, out);
                }
            })
            .input(input)
            .expected(expected)
            .onOffence(consumer);
    }

    private static void program(DelugeLineScanner lineScanner, PrintStream out) {
        var line = lineScanner.firstLine();
        out.printf("[%s]", escapeString(line));
        while (lineScanner.hasMore()) {
            line = lineScanner.readLine();
            out.printf("[%s]", escapeString(line));
        }
    }

}