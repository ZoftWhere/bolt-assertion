package app.zoftwhere.bolt;

import java.util.function.Supplier;

/**
 * <p>Bolt single return class.
 * </p>
 * <p>This is a test-only class for building lambda-based code blocks.
 * </p>
 *
 * @param <T> The code block return type.
 * @since 6.0.0
 */
public class BoltSingleReturn<T> {

    private final BoltPlaceHolder<T> boltPlaceHolder = new BoltPlaceHolder<>(null);

    @SuppressWarnings("UnusedReturnValue")
    public BoltSingleReturn<T> block(Supplier<T> code) {
        if (boltPlaceHolder.get() == null) {
            boltPlaceHolder.set(code.get());
        }

        return this;
    }

    @SuppressWarnings("unused")
    public T orDefault(T defaultValue) {
        return boltPlaceHolder.get() != null ? boltPlaceHolder.get() : defaultValue;
    }

    public T end() {
        return boltPlaceHolder.get();
    }

}
