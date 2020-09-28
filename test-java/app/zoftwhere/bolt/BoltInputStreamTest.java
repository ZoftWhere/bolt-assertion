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
        final var string = "Unicode(\ud801\udc10)";
        final var array = forString(string, UTF_16).readAllBytes();
        final var result = new String(array, UTF_16);

        assertEquals(string, result);
    }

    @Test
    void testCrossCodec() throws IOException {
        final var string = "ASCII(Hello World)";
        final var array = forString(string, US_ASCII, UTF_16LE).readAllBytes();
        final var result = new String(array, UTF_16LE);

        assertEquals(string, result);
    }

    @Test
    void testUnicode() throws IOException {
        final var string = "Unicode(\ud801\udc10)";
        final var array = forString(string, UTF_16LE, UTF_8).readAllBytes();
        final var result = new String(array, UTF_8);

        assertEquals(string, result);
    }

    @Test
    void testByteOrderMark1() throws IOException {
        final var string = "";
        final var array = forString(string, UTF_8, UTF_16).readAllBytes();
        final var expected = string.getBytes(UTF_16);

        assertArrayEquals(expected, array);
    }

    @Test
    void testByteOrderMark2() throws IOException {
        final var string = "Hello";
        final var array = forString(string, UTF_8, UTF_16).readAllBytes();
        final var expected = string.getBytes(UTF_16);

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
                final var input = forString(string, from, to);
                try (final var reader = new InputStreamReader(input, to)) {
                    final var n = reader.read(buffer, 0, size);
                    final var s = new String(buffer, 0, size);
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
        final var closedFlag = new BoltPlaceHolder<>(Boolean.FALSE);

        assertNotNull(closedFlag.get());

        for (var from : codec) {
            for (var to : codec) {
                closedFlag.set(false);
                assertFalse(closedFlag.get());

                try (final var input = forString(from.name(), from, to, closedFlag)) {
                    final var array = input.readAllBytes();
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
            public int read() throws IOException {
                throw new IOException();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                throw new IOException(message, null);
            }
        };
        var pass = true;

        try (final var reader = new BoltInputStream(failure, UTF_8, UTF_16)) {
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
