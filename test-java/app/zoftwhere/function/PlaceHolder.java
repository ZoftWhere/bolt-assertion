package app.zoftwhere.function;

public class PlaceHolder<T> {

    private T value;

    public PlaceHolder(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

}
