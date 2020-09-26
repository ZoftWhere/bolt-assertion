package app.zoftwhere.bolt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * <p>Bolt line iterator class.
 * </p>
 * <p>This is a package-private class for providing its functionality.
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
 * @since 11.1.0
 */
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
        loader = () -> {
            if (scanner.hasNext()) {
                return scanner.next();
            }
            if (scanner.hasNextLine()) {
                scanner.skip("\f?");
                return scanner.hasNextLine() ? scanner.nextLine() : "";
            }
            return null;
        };

        next = firstLine.get();
        hasNext = true;
    }

    BoltLineIterator(InputStream inputStream, Charset charset) {
        final BoltReader reader = new BoltReader(inputStream, charset);
        loader = () -> {
            if (!reader.hasNext()) {
                return null;
            }
            return reader.next();
        };

        next = loader.get();
        hasNext = next != null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return hasNext;
    }

    /** {@inheritDoc} */
    @Override
    public String next() {
        if (!hasNext) {
            return null;
        }

        String current = next;
        next = loader.get();
        hasNext = next != null;
        return current;
    }

}
