package app.zoftwhere.bolt.deluge;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.arrayOfClass;
import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static app.zoftwhere.bolt.BoltTestHelper.assertPublicConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertPublicConstructor(DelugeException.class, arrayOfClass(String.class));
        assertPublicConstructor(DelugeException.class, arrayOfClass(String.class, Throwable.class));
    }

}
