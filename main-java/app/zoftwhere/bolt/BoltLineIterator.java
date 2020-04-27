package app.zoftwhere.bolt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.function.Supplier;

class BoltLineIterator implements Iterator<String> {

    private final Supplier<String> loader;

    private boolean hasNext;

    private String next;

    BoltLineIterator(Scanner scanner) {
        final Supplier<String> firstLine = () -> {
            // Check for empty first line.
            scanner.useDelimiter("");
            if (scanner.hasNext("\\R")) {
                scanner.useDelimiter("\\R");
                return "";
            }

            // Check for empty input.
            scanner.useDelimiter("\\R");
            if (!scanner.hasNext()) {
                return "";
            }

            return scanner.next();
        };
        this.loader = () -> {
            if (scanner.hasNext()) {
                return scanner.next();
            }
            if (scanner.hasNextLine()) {
                scanner.skip("\f?");
                return scanner.hasNextLine() ? scanner.nextLine() : "";
            }
            return null;
        };

        this.next = firstLine.get();
        this.hasNext = true;
    }

    BoltLineIterator(InputStream inputStream, Charset charset) {
        final BoltReader reader = new BoltReader(inputStream, charset);
        this.loader = () -> {
            if (!reader.hasNext()) {
                return null;
            }
            return reader.next();
        };

        this.next = loader.get();
        this.hasNext = next != null;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public String next() {
        if (!hasNext) {
            return null;
        }

        String current = next;
        this.next = loader.get();
        this.hasNext = next != null;
        return current;
    }

}
