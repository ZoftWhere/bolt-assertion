package app.zoftwhere.bolt;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public class BoltSingleReturn<T> {

    private final BoltPlaceHolder<T> boltPlaceHolder = new BoltPlaceHolder<>(null);

    public BoltSingleReturn<T> block(Supplier<T> code) {
        if (boltPlaceHolder.get() == null) {
            boltPlaceHolder.set(code.get());
        }

        return this;
    }

    public T orDefault(T defaultValue) {
        return boltPlaceHolder.get() != null ? boltPlaceHolder.get() : defaultValue;
    }

    public T end() {
        return boltPlaceHolder.get();
    }

}
