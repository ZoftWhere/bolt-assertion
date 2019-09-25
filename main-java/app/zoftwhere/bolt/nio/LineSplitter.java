package app.zoftwhere.bolt.nio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * The solution for editor like pulling of lines from an input stream.
 */
public class LineSplitter {

    private final List<String> list;

    public LineSplitter(String string) {
        this.list = split(string);
    }

    public LineSplitter(byte[] data, Charset charset) {
        this.list = splitSafely(new ByteArrayInputStream(data), charset);
    }

    public LineSplitter(InputStream inputStream, Charset charset) throws IOException {
        this.list = split(inputStream, charset);
    }

    public LineSplitter(Scanner scanner) {
        this.list = split(scanner);
    }

    public List<String> list() {
        return this.list;
    }

    public String[] array() {
        //noinspection ToArrayCallWithZeroLengthArrayArgument
        return list.toArray(new String[list.size()]);
    }

    List<String> splitSafely(InputStream inputStream, Charset charset) {
        try {
            return split(inputStream, charset);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<String> split(InputStream inputStream, Charset charset) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
        final List<String> list = new ArrayList<>();
        try (LineReader reader = new LineReader(inputStreamReader)) {
            list.add(reader.readLine());
            while (reader.hasNext()) {
                list.add(reader.readLine());
            }
            return list;
        }
    }

    private List<String> split(String string) {
        final List<String> list = new ArrayList<>();

        if (string.length() == 0) {
            list.add("");
            return list;
        }

        final String[] array = string.split("\r?\n");
        list.addAll(Arrays.asList(array));

        // Empty first line.
        if (list.size() == 0) {
            list.add("");
        }

        // Empty trailing lines.
        int index = string.length() - 1;
        if (string.charAt(index) == '\r' || string.charAt(index) == '\n') {
            list.add("");
            --index;

            while (index >= 0 && (string.charAt(index) == '\r' || string.charAt(index) == '\n')) {
                if (string.charAt(index) == '\n') {
                    list.add("");
                }
                --index;
            }
        }

        return list;
    }

    private List<String> split(Scanner scanner) {
        final List<String> list = new ArrayList<>();

        // Check for empty first line.
        scanner.useDelimiter("");
        if (scanner.hasNext("\n")) {
            list.add("");
        }

        // Check for empty input.
        scanner.useDelimiter("\r?\n");
        if (!scanner.hasNext()) {
            list.add("");
            return list;
        }

        // General lines.
        while (scanner.hasNext()) {
            list.add(scanner.next());
        }

        // Empty last line.
        if (scanner.hasNextLine()) {
            list.add(scanner.nextLine());
        }

        return list;
    }

}
