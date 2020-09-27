package app.zoftwhere.bolt;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.NEW_LINE;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BoltArrayInputStreamTest {

    @Test
    void testArrays() throws Exception {
        final var inputMatrix = new String[][] {
            new String[] {""},
            new String[] {"", ""},
            new String[] {"", "", ""},
            new String[] {"1\n2", "3\r4"},
            new String[] {"\ufeffHelloWorld"},
            new String[] {"\ufeffHelloWorld", "\ufeff"},
        };
        final var charsetArray = new Charset[] {US_ASCII, ISO_8859_1, UTF_8, UTF_16LE, UTF_16BE, UTF_16};

        for (var charset : charsetArray) {
            for (var input : inputMatrix) {
                final var actual = buildActual(input, charset);
                final var expected = buildExpected(input, charset);
                assertArrayEquals(expected, actual);
            }
        }
    }

    private byte[] buildActual(String[] input, Charset charset) throws Exception {
        try (final var inputStream = new BoltArrayInputStream(input, charset)) {
            final var output = new ByteArrayOutputStream();
            var i = inputStream.read();
            while (i != -1) {
                output.write(i);
                i = inputStream.read();
            }
            return output.toByteArray();
        }
    }

    private byte[] buildExpected(String[] input, Charset charset) throws Exception {
        try (final var outputStream = new ByteArrayOutputStream()) {
            try (final var out = new PrintStream(outputStream, false, charset)) {
                out.print(input[0]);
                for (var i = 1; i < input.length; i++) {
                    out.print(NEW_LINE);
                    out.print(input[i]);
                }
            }
            return outputStream.toByteArray();
        }
    }

}
