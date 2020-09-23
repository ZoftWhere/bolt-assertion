package app.zoftwhere.bolt;

import java.util.Objects;

abstract class BoltUtility {

    static boolean arrayHasNull(String[] array) {
        for (String item : Objects.requireNonNull(array, "array")) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }

    private BoltUtility() {
    }

}
