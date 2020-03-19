package app.zoftwhere.bolt;

import java.util.Objects;

import org.opentest4j.AssertionFailedError;

@SuppressWarnings("WeakerAccess")
public class BoltTestHelper {

    public static String[] array(String... array) {
        return array;
    }

    public static <T> boolean isOrHasNull(T[] array) {
        if (array == null) {
            return true;
        }
        for (int s = array.length, i = 0; i < s; i++) {
            if (array[i] == null) {
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

}
