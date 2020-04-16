package app.zoftwhere.bolt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

class BoltReaderTest {

    @Test
    void readLine() throws IOException {
        final var string = "";
        final var reader = forString(string, UTF_16);
        assertEquals("", reader.readLine());
        assertFalse(reader.hasNext());
    }

    @Test
    void testByteOrderMark() throws IOException {
        final var string = "\ufeffUTF-16\ufeff";
        try (var reader = forString(string, UTF_16)) {
            assertEquals("UTF-16\ufeff", reader.readLine());
        }
    }

    @Test
    void testByteOrderMarkArray() {
        final var string = "\ufeffUTF-16";
        final var array = BoltReader.readArray(() -> forString(string, UTF_16));
        final var expected = new String[] {"UTF-16"};
        assertArrayEquals(expected, array);
    }

    @Test
    void testReadBuffer() throws IOException {
        var reader = new BoltReader("".getBytes(), UTF_8);
        assertEquals(-1, reader.read());
    }

    @Test
    void testRead() throws IOException {
        var reader = new BoltReader("".getBytes(), UTF_8);
        var chars = new char[0];
        assertEquals(-1, reader.read(chars, 0, 0));
    }

    @Test
    void testNullPointer() {
        try {
            new BoltReader((byte[]) null, UTF_8);
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals(e.getMessage(), "bolt.runner.reader.data.null");
        }
    }

    @Test
    void testNullCharset() {
        try {
            new BoltReader(new byte[0], null);
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals(e.getMessage(), "bolt.runner.reader.charset.null");
        }

        try {
            new BoltReader(new ByteArrayInputStream(new byte[0]), null);
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals(e.getMessage(), "bolt.runner.reader.charset.null");
        }
    }

    @Test
    void testNullInputStream() {
        try {
            new BoltReader((InputStream) null, UTF_8);
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals(e.getMessage(), "bolt.runner.reader.input.stream.null");
        }
    }

    @Test
    void testIOException() {
        final var inputStream = new ByteArrayInputStream("\r\r\r".getBytes()) {
            @Override
            public void close() throws IOException {
                super.close();
                throw new IOException();
            }
        };
        try {
            try (BoltReader reader = new BoltReader(inputStream, UTF_8)) {
                reader.readLine();
            }

            fail("bolt.reader.close.exception.expected");
        }
        catch (IOException ignore) { }
    }

    @Test
    void testHasNextFail() throws IOException {
        var stream = new ByteArrayInputStream("".getBytes()) {
            @Override
            public synchronized int read(byte[] b, int off, int len) {
                return -1;
            }
        };

        var runner = new BoltReader(stream, UTF_8) {
            @Override
            public boolean ready() throws IOException {
                throw new IOException();
            }
        };

        runner.readLine();
        assertFalse(runner.hasNext());
    }

    @Test
    void testNextFail() {
        ByteArrayInputStream stream = new ByteArrayInputStream("".getBytes());

        BoltReader reader = new BoltReader(stream, UTF_8) {
            @Override
            String readLine() throws IOException {
                throw new IOException();
            }
        };

        try {
            reader.next();
            fail("UncheckedIOException expected.");
        }
        catch (RuntimeException e) {
            assertClass(UncheckedIOException.class, e);
        }
    }

    @Test
    void testToArrayFail() {
        var stream = new ByteArrayInputStream(new byte[0]);

        try {
            BoltReader.readArray(() -> new BoltReader(stream, UTF_8) {
                @Override
                public void close() {
                    throw new UncheckedIOException(new IOException("Fake IO Exception."));
                }
            });
            fail();
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.reader.read.array", e.getMessage());
            assertClass(UncheckedIOException.class, e.getCause());
            assertEquals("java.io.IOException: Fake IO Exception.", e.getCause().getMessage());
        }
    }

    @Test
    void testReadListFail() {
        var stream = new ByteArrayInputStream(new byte[0]);

        try {
            BoltReader.readList(() -> new BoltReader(stream, UTF_8) {
                @Override
                public void close() {
                    throw new UncheckedIOException(new IOException("Fake IO Exception."));
                }
            });
            fail();
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.reader.read.list", e.getMessage());
            assertClass(UncheckedIOException.class, e.getCause());
            assertEquals("java.io.IOException: Fake IO Exception.", e.getCause().getMessage());
        }
    }

    @Test
    void testByteArraySplitter() {
        final var string = "\r\n\r\n";
        final Supplier<BoltReader> supplier = () -> forString(string, UTF_8);
        final var list = BoltReader.readList(supplier);
        final var array = BoltReader.readArray(supplier);
        assertEquals(3, list.size());
        assertEquals(3, array.length);
    }

    @Test
    void testStringSplitter5() {
        final var string = "1\n1\n345";
        final Supplier<BoltReader> supplier = () -> forString(string, UTF_8);
        final var list = BoltReader.readList(supplier);
        final var array = BoltReader.readArray(supplier);
        assertEquals(3, list.size());
        assertEquals(3, array.length);
    }

    @Test
    void testInputSplitting() {
        testThis("empty", "");
        testThis("blank", " ");
        testThis("new1", "", "");
        testThis("new2", "", "", "");
        testThis("new3", "", "", "", "");
    }

    private void testThis(String test, String... array) {
        final int size = array.length;
        StringBuilder builder = new StringBuilder();
        if (size > 0) {
            builder.append(array[0]);
        }
        for (int i = 1; i < size; i++) {
            builder.append("\n").append(array[i]);
        }
        String input = builder.toString();
        final var list = BoltReader.readList(() -> forString(input, UTF_8));

        if (array.length != list.size()) {
            assertEquals(array.length, list.size(), test + " [" + Arrays.toString(list.toArray()) + "]");
        }
        for (int i = 0; i < size; i++) {
            assertEquals(array[i], list.get(i), test);
        }
    }

    @Test
    void testReadLine() throws IOException {
        final var string = "Hello World\ud835\udccc";
        final var reader = forString(string, UTF_16);
        final var line = reader.readLine();
        assertEquals(string, line);
        assertFalse(reader.hasNext());
    }

    private BoltReader forString(String string, Charset charset) {
        final var input = new ByteArrayInputStream(string.getBytes(charset));
        return new BoltReader(input, charset);
    }

}
