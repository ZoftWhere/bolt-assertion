package app.zoftwhere.bolt.deluge;

import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.Runner.newRunner;

class DelugeLineScannerTest {

    @Test
    void testEmpty() {
        String[] input = {""};
        String[] expected = {"[]"};
        testWithRunner(input, expected);
    }

    @Test
    void testNewLineFirst() {
        String[] input = {"", "2", ""};
        String[] expected = {"[][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testByteOrderMark() {
        String[] input = {"\ufeff", "2\ufeff", ""};
        String[] expected = {DelugeLineScanner.escapeString("[][2\ufeff][]")};
        testWithRunner(input, expected);
    }

    @Test
    void testCarriageReturn() {
        String[] input = {"\r" + "\r" + "2\r" + ""};
        String[] expected = {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testLineFeed() {
        String[] input = {"\n" + "\n" + "2\n" + ""};
        String[] expected = {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testWindowLine() {
        String[] input = {"\r\n" + "\r\n" + "2\r\n" + ""};
        String[] expected = {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testLineSeparator() {
        String[] input = {"\u2028" + "\u2028" + "2\u2028" + ""};
        String[] expected = {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testParagraphSeparator() {
        String[] input = {"\u2029" + "\u2029" + "2\u2029" + ""};
        String[] expected = {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    void testNextLine() {
        String[] input = {"\u0085" + "\u0085" + "2\u0085" + ""};
        String[] expected = {"[][][2][]"};
        testWithRunner(input, expected);
    }

    @Test
    @SuppressWarnings("SpellCheckingInspection")
    void testEscapes() {
        String[] source = {
            "\ufeffBOM",
            "\\",
            "\r",
            "\n",
            "\r\n",
            "\t",
            "\u2028",
            "\u2029",
            "\u0085",
            "",
            "end"
        };
        String[] target = {
            "\\ufeffBOM",
            "\\\\",
            "\\r",
            "\\n",
            "\\r\\n",
            "\\t",
            "\\u2028",
            "\\u2029",
            "\\u0085",
            "",
            "end"
        };
        int size = source.length;
        for (int i = 0; i < size; i++) {
            Assertions.assertEquals(target[i], DelugeLineScanner.escapeString(source[i]));
        }
    }

    private void testWithRunner(String[] input, String[] expected) {
        newRunner()
            .input(input)
            .run((scanner, out) -> {
                try (DelugeLineScanner lineScanner = new DelugeLineScanner(scanner)) {
                    program(lineScanner, out);
                }
            })
            .expected(expected)
            .assertSuccess();
    }

    private static void program(DelugeLineScanner lineScanner, PrintStream out) {
        String line = lineScanner.firstLine();
        out.printf("[%s]", DelugeLineScanner.escapeString(line));
        while (lineScanner.hasNextLine()) {
            line = lineScanner.nextLine();
            out.printf("[%s]", DelugeLineScanner.escapeString(line));
        }
    }

}