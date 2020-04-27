package app.zoftwhere.bolt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
    void testCrossCodec() throws IOException {
        @SuppressWarnings("UnnecessaryLocalVariable")
        var charset = US_ASCII; // Use ASCII for base.
        var decode = UTF_16LE; // Use UTF-16LE for input stream.
        var string = "ASCII(Hello World)";
        var array = forString(string, charset, decode).readAllBytes();
        var result = new String(array, decode);

        assertEquals(string, result);
    }

    @Test
    void testUnicode() throws IOException {
        @SuppressWarnings("UnnecessaryLocalVariable")
        var charset = UTF_16LE; // Use ASCII for base.
        var decode = UTF_8; // Use UTF-16LE for input stream.
        var string = "Unicode(\ud801\udc10)";
        var array = forString(string, charset, decode).readAllBytes();
        var result = new String(array, decode);

        assertEquals(string, result);
    }

    @Test
    void testByteOrderMark1() throws IOException {
        @SuppressWarnings("UnnecessaryLocalVariable")
        var charset = UTF_8; // Use ASCII for base.
        var decode = UTF_16; // Use UTF-16 for input stream.
        var string = "";
        var array = forString(string, charset, decode).readAllBytes();
        var expected = string.getBytes(decode);

        assertArrayEquals(expected, array);
    }

    @Test
    void testByteOrderMark2() throws IOException {
        @SuppressWarnings("UnnecessaryLocalVariable")
        var charset = UTF_8; // Use ASCII for base.
        var decode = UTF_16; // Use UTF-16 for input stream.
        var string = "Hello";
        var array = forString(string, charset, decode).readAllBytes();
        var expected = string.getBytes(decode);

        assertArrayEquals(expected, array);
    }

    @Test
    void testRun() {
        final var codec = List.of(US_ASCII, UTF_8, UTF_16LE, UTF_16BE, UTF_16);
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
    void testClose() throws Exception {
        final var codec = List.of(US_ASCII, UTF_8, UTF_16LE, UTF_16BE, UTF_16);
        final var string = "Test Close.\n\n\n";
        final var closedFlag = new BoltPlaceHolder<>(Boolean.FALSE);

        assertNotNull(closedFlag.get());

        for (var from : codec) {
            for (var to : codec) {
                closedFlag.set(false);
                assertFalse(closedFlag.get());

                try (var input = forString(string, from, to, closedFlag)) {
                    var array = input.readAllBytes();
                    assertTrue(array.length > 0);
                }

                assertNotNull(closedFlag);
                assertNotNull(closedFlag.get());
                assertTrue(closedFlag.get());
            }
        }
    }

    @Test
    void testIOFailure() {
        final var message = "bolt.input.stream.failure.for.code.coverage";
        final var failure = new InputStream() {
            @Override
            @SuppressWarnings("RedundantThrows")
            public int read() throws IOException {
                return 0;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                throw new IOException(message, null);
            }
        };
        var pass = true;

        try (var reader = new BoltInputStream(failure, UTF_8, UTF_16)) {
            reader.reset();
            pass = false;
        }
        catch (Exception e) {
            assertClass(UncheckedIOException.class, e);
            assertEquals(message, e.getMessage());
            pass = true;
        }

        assertTrue(pass, "bolt.input.stream.io.exception.expected");
    }

    private InputStream forString(String string, Charset charset) {
        return new ByteArrayInputStream(string.getBytes(charset));
    }

    private InputStream forString(String string, Charset charset, Charset decode) {
        return new BoltInputStream(forString(string, charset), charset, decode);
    }

    @SuppressWarnings("SameParameterValue")
    private InputStream forString(String string, Charset charset, Charset decode, BoltPlaceHolder<Boolean> closeFlag) {
        return new BoltInputStream(forString(string, charset), charset, decode) {
            @Override
            public void close() throws IOException {
                closeFlag.set(true);
                super.close();
            }
        };
    }

}
