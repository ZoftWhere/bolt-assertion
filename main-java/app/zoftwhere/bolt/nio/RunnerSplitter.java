package app.zoftwhere.bolt.nio;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The solution for editor like pulling of lines from an input stream.
 */
public class RunnerSplitter {

    public RunnerSplitter() {
    }

    public List<String> getList(InputStream inputstream, Charset charset) {
        try (Scanner scanner = new Scanner(inputstream, charset.name())) {
            return getList(scanner);
        }
    }

    public List<String> getList(String data) {
        try (Scanner scanner = new Scanner(data)) {
            return getList(scanner);
        }
    }

    public List<String> getList(Scanner scanner) {
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
