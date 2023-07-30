package app.zoftwhere.bolt;

/**
 * Bolt Place Holder class.
 *
 * <p>This is a test-only class for being able to pass mutable objects as parameters.
 *
 * @param <T> The data type.
 * @author Osmund
 * @since 6.0.0
 */
public class BoltPlaceHolder<T> {

  private T value;

  /**
   * Constructor for BoltPlaceHolder.
   *
   * @param value a T object.
   */
  public BoltPlaceHolder(T value) {
    this.value = value;
  }

  /**
   * Setter for the mutable value.
   *
   * @param value a T object.
   */
  public void set(T value) {
    this.value = value;
  }

  /**
   * Getter for the mutable value.
   *
   * @return a T object.
   */
  public T get() {
    return value;
  }
}
