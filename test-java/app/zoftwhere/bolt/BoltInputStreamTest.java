package app.zoftwhere.bolt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import app.zoftwhere.function.PlaceHolder;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class BoltInputStreamTest {

    @Test
    void testSameCodec() throws IOException {
        // UTF_16 works here, but UTF_16BE and/or UTF_16LE should be used instead.
        var charset = UTF_16;
        var string = "Unicode(\ud801\udc10)";
        var array = forString(string, charset).readAllBytes();
        var result = new String(array, charset);

        assertEquals(string, result);
    }

    @Test
    @SuppressWarnings("UnnecessaryLocalVariable")
    void testCrossCodec() throws IOException {
        var charset = US_ASCII; // Use ASCII for base.
        var decode = UTF_16LE; // Use UTF-16LE for input stream.
        var string = "ASCII(Hello World)";
        var array = forString(string, charset, decode).readAllBytes();
        var result = new String(array, decode);

        assertEquals(string, result);
    }

    @Test
    @SuppressWarnings("UnnecessaryLocalVariable")
    void testUnicode() throws IOException {
        var charset = UTF_16LE; // Use ASCII for base.
        var decode = UTF_8; // Use UTF-16LE for input stream.
        var string = "Unicode(\ud801\udc10)";
        var array = forString(string, charset, decode).readAllBytes();
        var result = new String(array, decode);

        assertEquals(string, result);
    }

    @Test
    void testRun() {
        // UTF_16 does not work here; UTF_16BE and UTF_16LE is listed instead.
        final var codec = List.of(US_ASCII, UTF_8, UTF_16LE, UTF_16BE);
        final var string = "Test Run.\n\n\n";
        final var size = (int) string.chars().count();
        final var buffer = new char[size];

        for (var from : codec) {
            for (var to : codec) {
                var input = forString(string, from, to);
                try (var reader = new InputStreamReader(input, to)) {
                    var n = reader.read(buffer, 0, size);
                    var s = new String(buffer, 0, size);
                    assertEquals(size, n);
                    assertEquals(string, s);
                }
                catch (IOException ignore) {
                    fail("IOException not expected");
                }
            }
        }
    }

    @Test
    void testClose() {
        // UTF_16 does not work here; UTF_16BE and UTF_16LE is listed instead.
        final var codec = List.of(US_ASCII, UTF_8, UTF_16LE, UTF_16BE);
        final var string = "Test Close.\n\n\n";
        final var closedFlag = new PlaceHolder<>(Boolean.FALSE);

        assertNotNull(closedFlag.get());

        for (var from : codec) {
            for (var to : codec) {
                closedFlag.set(false);
                assertFalse(closedFlag.get());

                try (var input = forString(string, from, to, closedFlag)) {
                    var array = input.readAllBytes();
                    assertTrue(array.length > 0);
                }
                catch (IOException ignore) {
                    fail("IOException not expected");
                }

                assertNotNull(closedFlag);
                assertNotNull(closedFlag.get());
                assertTrue(closedFlag.get());
            }
        }
    }

    private InputStream forString(String string, Charset charset) {
        return new ByteArrayInputStream(string.getBytes(charset));
    }

    private InputStream forString(String string, Charset charset, Charset decode) {
        return new BoltInputStream(forString(string, charset), charset, decode);
    }

    @SuppressWarnings("SameParameterValue")
    private InputStream forString(String string, Charset charset, Charset decode, PlaceHolder<Boolean> closeFlag) {
        return new BoltInputStream(forString(string, charset), charset, decode) {
            @Override
            public void close() throws IOException {
                closeFlag.set(true);
                super.close();
            }
        };
    }

}
