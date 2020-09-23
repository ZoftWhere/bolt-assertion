package app.zoftwhere.bolt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

import org.opentest4j.AssertionFailedError;

public class BoltTestHelper {

    /** New line definition for parsing that allows testing to be system agnostic. */
    @SuppressWarnings("WeakerAccess")
    public static final String NEW_LINE = BoltProvide.NEW_LINE;

    public static String[] array(String... array) {
        return array;
    }

    public static boolean arrayHasNull(String[] array) {
        return BoltUtility.arrayHasNull(array);
    }

    public static <T> boolean isOrHasNull(T[] array) {
        if (array == null) {
            return true;
        }
        for (T item : array) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }

    public static void assertClass(Class<?> expected, Object test) {
        if (expected == null) {
            throw new IllegalArgumentException("bolt.test.helper.is-class.expected.is.null");
        }

        if (test == null) {
            throw new IllegalArgumentException("bolt.test.helper.is-class.test.is.null");
        }

        String expectedString = expected.getName();
        String actualString = test.getClass().getName();
        if (!Objects.equals(expectedString, actualString)) {
            throw new AssertionFailedError("bolt.test.class.not.equal", expectedString, actualString);
        }
    }

    public static InputStream transcode(InputStream inputStream, Charset source, Charset destination) {
        if (Objects.equals(source, destination)) {
            return inputStream;
        }
        return new BoltInputStream(inputStream, source, destination);
    }

    public static Iterator<String> newStringIterator(InputStream inputStream, Charset charset) {
        return new BoltLineIterator(inputStream, charset);
    }

    public static Iterator<String> newStringIterator(Scanner scanner) {
        return new BoltLineIterator(scanner);
    }

    public static String[] readArray(byte[] data, Charset encoding) {
        return BoltReader.readArray(() -> new BoltReader(data, encoding));
    }

    public static InputStream newStringArrayInputStream(String[] array, Charset charset) {
        return new BoltArrayInputStream(array, charset);
    }

    /**
     * Helper method for returning human-readable characters for select characters.
     *
     * @param input text to escape
     * @return text with select characters replaced with human-readable versions.
     * @since 11.0.0
     */
    public static String escapeString(String input) {
        StringBuilder builder = new StringBuilder();
        input.codePoints().forEach(i -> {
            if (i == '\ufeff') {
                //noinspection SpellCheckingInspection
                builder.append("\\ufeff");
            }
            else if (i == '\\') {
                builder.append("\\\\");
            }
            else if (i == '\f') {
                builder.append("\\f");
            }
            else if (i == '\n') {
                builder.append("\\n");
            }
            else if (i == '\r') {
                builder.append("\\r");
            }
            else if (i == '\t') {
                builder.append("\\t");
            }
            else if (i == '\u0085') {
                builder.append("\\u0085");
            }
            else if (i == '\u2028') {
                builder.append("\\u2028");
            }
            else if (i == '\u2029') {
                builder.append("\\u2029");
            }
            else {
                builder.appendCodePoint(i);
            }
        });
        return builder.toString();
    }

}
