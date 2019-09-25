package app.zoftwhere.bolt.nio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class LineSplitterTest {

    @Test
    void testNullPointer() {
        try {
            new LineSplitter((byte[]) null, UTF_8);
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
            new LineSplitter("").splitSafely(inputStream, UTF_8);
            fail("bolt.line.splitter.exception.expected");
        }
        catch (RuntimeException ignore) { }
    }

    @Test
    void testStringSplitter() {
        final var splitter = new LineSplitter("\n\n");
        final var list = splitter.list();
        final var array = splitter.array();
        assertEquals(3, list.size());
        assertEquals(3, array.length);
    }

    @Test
    void testByteArraySplitter() {
        final var splitter = new LineSplitter("\n\n".getBytes(UTF_8), UTF_8);
        final var list = splitter.list();
        final var array = splitter.array();
        assertEquals(3, list.size());
        assertEquals(3, array.length);
    }

    @Test
    void testStringSplitter5() {
        final var splitter = new LineSplitter("1\n1\n345");
        final var list = splitter.list();
        final var array = splitter.array();
        assertEquals(3, list.size());
        assertEquals(3, array.length);
    }

    @Test
    void testScannerSplitter() {
        final var splitter = new LineSplitter(new Scanner("\n\n"));
        final var list = splitter.list();
        final var array = splitter.array();
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
        final var list = new LineSplitter(input.getBytes(UTF_8), UTF_8).list();

        if (array.length != list.size()) {
            assertEquals(array.length, list.size(), test + " [" + Arrays.toString(list.toArray()) + "]");
        }
        for (int i = 0; i < size; i++) {
            assertEquals(array[i], list.get(i), test);
        }
    }

}
