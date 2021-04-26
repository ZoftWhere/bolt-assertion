package app.zoftwhere.bolt;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

import org.opentest4j.AssertionFailedError;

/**
 * <p>Bolt Test Helper class.
 * </p>
 * <p>This is a test-only class for providing common functionality.
 * </p>
 *
 * @author Osmund
 * @since 6.0.0
 */
public class BoltTestHelper {

    public static String[] array(String... array) {
        return array;
    }

    public static Class<?>[] arrayOfClass(Class<?>... array) {
        return array;
    }

    public static boolean arrayHasNull(String[] array) {
        return BoltUtility.arrayHasNull(array);
    }

    public static <T> boolean isOrHasNull(T[] array) {
        if (array == null) {
            return true;
        }
        for (final var item : array) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }

    public static void assertClass(Class<?> expected, Object test) {
        if (expected == null) {
            throw new IllegalArgumentException("bolt.test.helper.assertClass.expected.is.null");
        }

        if (test == null) {
            throw new IllegalArgumentException("bolt.test.helper.assertClass.test.is.null");
        }

        final var expectedString = expected.getName();
        final var actualString = test.getClass().getName();
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
        final var builder = new StringBuilder();
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

    /**
     * Assert public constructor.
     *
     * @param clazz      Class to inspect.
     * @param parameters Array of constructor parameters.
     * @param <T>        The class type.
     * @since 11.5.0
     */
    public static <T> void assertPublicConstructor(Class<T> clazz, Class<?>[] parameters) {
        try {
            final var constructor = clazz.getConstructor(parameters);
            final var accessMask = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
            final var flag = constructor.getModifiers() & accessMask;

            if (flag == Modifier.PUBLIC) {
                return;
            }
        }
        catch (NoSuchMethodException ignore) {
        }

        final var expected = publicConstructorString(clazz, parameters);
        final var message = "bolt.test.helper.assert.public.constructor.failed";
        throw new AssertionFailedError(message, expected, "<none>");
    }

    /**
     * Private helper method for building public constructor string.
     *
     * @param clazz      Class to inspect.
     * @param parameters Array of constructor parameters.
     * @param <T>        The class type.
     * @return Public constructor string.
     * @since 11.5.0
     */
    private static <T> String publicConstructorString(Class<T> clazz, Class<?>[] parameters) {
        final var builder = new StringBuilder("public")
            .append(" ")
            .append(clazz.getName())
            .append("(");
        if (parameters.length > 0) {
            builder.append(parameters[0].getName());
            final var s = parameters.length;
            for (var i = 1; i < s; i++) {
                builder.append(",").append(parameters[i].getName());
            }
        }
        builder.append(")");
        return builder.toString();
    }

}
