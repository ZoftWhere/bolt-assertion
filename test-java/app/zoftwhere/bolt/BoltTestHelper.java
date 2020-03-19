package app.zoftwhere.bolt;

import java.util.Objects;

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

    public static boolean isClass(Class<?> expected, Object test) {
        if (expected == null) {
            throw new IllegalArgumentException("bolt.test.helper.is-class.expected.is.null");
        }
        if (test == null) {
            throw new IllegalArgumentException("bolt.test.helper.is-class.test.is.null");
        }
        return Objects.equals(expected.getName(), test.getClass().getName());
    }

}
