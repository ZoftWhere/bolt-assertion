package app.zoftwhere.bolt;

import java.util.Objects;

class BoltUtility {

    static boolean arrayHasNull(String[] array) {
        for (String item : Objects.requireNonNull(array, "array")) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }

}
