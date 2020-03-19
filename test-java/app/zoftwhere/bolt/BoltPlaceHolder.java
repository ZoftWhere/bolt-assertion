package app.zoftwhere.bolt;

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
