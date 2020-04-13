package app.zoftwhere.bolt;

/**
 * <p>Bolt place holder class.
 * </p>
 * <p>This is a test-only class for being able to pass mutable objects as parameters.
 * </p>
 *
 * @param <T> The data type.
 * @since 6.0.0
 */
public class BoltPlaceHolder<T> {

    private T value;

    public BoltPlaceHolder(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

}
