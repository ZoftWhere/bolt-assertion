package app.zoftwhere.bolt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerReaderTest {

    @Test
    void readLine() throws IOException {
        final var string = "";
        final var reader = forString(string, UTF_16);
        assertEquals("", reader.readLine());
        assertFalse(reader.hasNext());
    }

    @Test
    void testNullPointer() {
        try {
            new RunnerReader((byte[]) null, UTF_8);
        }
        catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
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
            try (RunnerReader reader = new RunnerReader(inputStream, UTF_8)) {
                reader.readLine();
            }

            fail("bolt.reader.close.exception.expected");
        }
        catch (IOException ignore) { }
    }

    @Test
    void testByteArraySplitter() {
        final var string = "\n\n";
        final Supplier<RunnerReader> supplier = () -> forString(string, UTF_8);
        final var list = RunnerReader.readList(supplier);
        final var array = RunnerReader.readArray(supplier);
        assertEquals(3, list.size());
        assertEquals(3, array.length);
    }

    @Test
    void testStringSplitter5() {
        final var string = "1\n1\n345";
        final Supplier<RunnerReader> supplier = () -> forString(string, UTF_8);
        final var list = RunnerReader.readList(supplier);
        final var array = RunnerReader.readArray(supplier);
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
        final var list =  RunnerReader.readList(() -> forString(input, UTF_8));

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

    private RunnerReader forString(String string, Charset charset) {
        final var input = new ByteArrayInputStream(string.getBytes(charset));
        return new RunnerReader(input, charset);
    }
}