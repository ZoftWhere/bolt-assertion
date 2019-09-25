package app.zoftwhere.bolt.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.stream.Collectors;

import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.Runner.RunnerProgram;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

class LineReaderTest {

    private final RunnerProgram program = new Runner().runConsole(LineReaderTest::getOutput);

    @Test
    void testMarkRead() {
        assertFalse(new LineReader(new StringReader("")).markSupported());
    }

    @Test
    void testStreamSmall() {
        final var stringReader = new StringReader("\n\n\n");
        final var list = new LineReader(stringReader).lines().collect(Collectors.toList());
        assertEquals(4, list.size());
    }

    @Test
    void testStreamLarge() {
        StringBuilder builder = new StringBuilder();
        //noinspection StringRepeatCanBeUsed
        for (int i = 0; i < 256; i++) {
            builder.append("--------------------------------------------------------------\r\n");
        }
        final StringReader stringReader = new StringReader(builder.toString());
        final var list = new LineReader(stringReader).lines().collect(Collectors.toList());
        assertNotNull(list);
    }

    @Test
    void testReadBuffer() throws IOException {
        StringBuilder builder = new StringBuilder();
        //noinspection StringRepeatCanBeUsed
        for (int i = 0; i < 256; i++) {
            builder.append("--------------------------------------------------------------\r\n");
        }

        final var charArray = new char[2048];
        final var reader = new LineReader(new StringReader(builder.toString()));
        reader.readLine();
        for (int i = 0; i < 6; i++) {
            assertEquals(2048, reader.read(charArray, 0, 2048));
        }
    }

    @SuppressWarnings("StringRepeatCanBeUsed")
    @Test
    void testBufferCrossOver() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1024 - 2; i++) {
            builder.append("d");
        }
        builder.append("\r\n");

        final var charArray = new char[1024];
        final var reader = new LineReader(new StringReader(builder.toString()), 1024);
        reader.readLine();
        assertEquals(0, reader.read(charArray, 0, 64));
        assertEquals(-1, reader.read(charArray, 0, 64));
    }

    @SuppressWarnings("StringRepeatCanBeUsed")
    @Test
    void testBufferCrossOver2() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1023; i++) {
            builder.append("d");
        }
        builder.append("\r\n");
        builder.append(" ");

        final var charArray = new char[1024];
        final var reader = new LineReader(new StringReader(builder.toString()), 1024);
        assertEquals(1024, reader.read(charArray, 0, 1024));
        assertEquals(2, reader.read(charArray, 0, 64));
        assertEquals(-1, reader.read(charArray, 0, 64));
    }

    @Test
    void indexOufOfBounds() throws IOException {
        final var reader = new LineReader(new StringReader(""));
        try {
            assertEquals(-1, reader.read(new char[10], -1, 10));
            fail();
        }
        catch (IndexOutOfBoundsException ignored) { }
    }

    @Test
    void nullOnReader() throws IOException {
        final var reader = new LineReader(new StringReader(""));
        reader.readLine();
        assertNull(reader.readLine());
    }

    @Test
    void testIOES() {
        LineReader g = new LineReader(new StringReader("")) {
            @Override
            public String readLine() throws IOException {
                throw new IOException();
            }
        };
        try {
            assertNull(g.lines().collect(Collectors.toList()));
            fail();
        }
        catch (Exception ignored) { }
    }

    @Test
    void testBasics() {
        program.input("")
            .expected("")
            .assertSuccess();
        program.input(" ")
            .expected(" ")
            .assertSuccess();
        program.input("\n")
            .expected("", "")
            .assertSuccess();
        program.input("\n ")
            .expected("", " ")
            .assertSuccess();
        program.input(" \n")
            .expected(" ", "")
            .assertSuccess();
        program.input(" \n ")
            .expected(" ", " ")
            .assertSuccess();
        program.input("\n\n")
            .expected("", "", "")
            .assertSuccess();
        program.input("\r\n\r")
            .expected("", "", "")
            .assertSuccess();
        program.input("\r\n\r\n")
            .expected("", "", "")
            .assertSuccess();
    }

    private static void getOutput(InputStream inputStream, OutputStream outputStream) throws IOException {
        Reader streamReader = new InputStreamReader(inputStream, UTF_8);
        try (LineReader lineReader = new LineReader(streamReader)) {
            try (Writer writer = new OutputStreamWriter(outputStream, UTF_8)) {
                writer.write(lineReader.readLine());

                while (lineReader.hasNext()) {
                    writer.write('\n');
                    writer.write(lineReader.readLine());
                }

                writer.flush();
            }
        }
    }

}