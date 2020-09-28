package app.zoftwhere.bolt.deluge;

import java.lang.reflect.Modifier;

import app.zoftwhere.bolt.RunnerException;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>Deluge Exception Test.
 * </p>
 * <p>This is a package-private class for testing the {@link app.zoftwhere.bolt.deluge.DelugeException} class.
 * </p>
 *
 * @author Osmund
 * @since 11.4.0
 */
class DelugeExceptionTest {

    @Test
    void testNonFinal() {
        DelugeException exception = new DelugeException("open", null) {
            @Override
            public String toString() {
                return "ExtensionException.class";
            }
        };
        try {
            throw exception;
        }
        catch (Exception e) {
            final var className = DelugeExceptionTest.class.getName() + "$1";
            assertEquals(className, e.getClass().getName());
        }
    }

    @Test
    void testMessageConstructor() {
        final var exception = new DelugeException("test");
        assertNotNull(exception);
    }

    @Test
    void testMessageCauseConstructor() {
        final var cause = new NullPointerException("null");
        final var exception = new DelugeException("test.cause", cause);
        assertNotNull(exception);
        assertClass(DelugeException.class, exception);
        assertClass(NullPointerException.class, exception.getCause());
    }

    @Test
    void testPublicConstructors() {
        assertPublicConstructor(array(String.class));
        assertPublicConstructor(array(String.class, Throwable.class));
    }

    private void assertPublicConstructor(Class<?>[] parameters) {
        try {
            final var constructor = DelugeException.class.getConstructor(parameters);
            final var accessMask = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
            final var flag = constructor.getModifiers() & accessMask;

            if (flag == Modifier.PUBLIC) {
                return;
            }
        }
        catch (NoSuchMethodException ignore) {
        }

        final var expected = publicConstructorString(parameters);
        throw new AssertionFailedError("bolt.deluge.exception.constructor.check", expected, "<none>");
    }

    private Class<?>[] array(Class<?>... array) {
        return array;
    }

    private String publicConstructorString(Class<?>[] parameters) {
        final var builder = new StringBuilder("public")
            .append(" ")
            .append(RunnerException.class.getName())
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
