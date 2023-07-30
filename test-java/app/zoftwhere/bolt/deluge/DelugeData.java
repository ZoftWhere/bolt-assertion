package app.zoftwhere.bolt.deluge;

import static app.zoftwhere.bolt.BoltTestHelper.isOrHasNull;
import static app.zoftwhere.bolt.BoltTestHelper.newStringArrayInputStream;
import static app.zoftwhere.bolt.deluge.DelugeDataType.ARRAY;
import static app.zoftwhere.bolt.deluge.DelugeDataType.ARRAY_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeDataType.RESOURCE;
import static app.zoftwhere.bolt.deluge.DelugeDataType.RESOURCE_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeDataType.STREAM;
import static app.zoftwhere.bolt.deluge.DelugeDataType.STREAM_ENCODED;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import java.nio.charset.Charset;
import org.junit.jupiter.api.Assertions;

/**
 * Bolt Assertion Deluge Data class.
 *
 * @author Osmund
 * @since 6.0.0
 */
public class DelugeData {

  /**
   * DelugeData factory method.
   *
   * @param data program input data
   * @return {@link app.zoftwhere.bolt.deluge.DelugeData} instance
   * @since 11.0.0
   */
  static DelugeData forStringArray(String[] data) {
    return new DelugeData(data);
  }

  /**
   * DelugeData factory method.
   *
   * @param data program input data
   * @param charset input data character encoding
   * @return {@link app.zoftwhere.bolt.deluge.DelugeData} instance
   * @since 11.0.0
   */
  static DelugeData forStringArray(String[] data, Charset charset) {
    return new DelugeData(data, charset);
  }

  /**
   * DelugeData factory method.
   *
   * @param data program input data
   * @param charset input data character encoding
   * @param withCharset flag to indicate runner default encoding
   * @return {@link app.zoftwhere.bolt.deluge.DelugeData} instance
   * @since 11.0.0
   */
  static DelugeData forInputStream(String[] data, Charset charset, boolean withCharset) {
    final var type = withCharset ? STREAM_ENCODED : STREAM;
    return new DelugeData(type, data, charset);
  }

  /**
   * DelugeData factory method.
   *
   * @param exception program exception
   * @return {@link app.zoftwhere.bolt.deluge.DelugeData} instance
   * @since 11.0.0
   */
  static DelugeData forInputStream(Exception exception) {
    return new DelugeData(STREAM, exception, null);
  }

  /**
   * DelugeData factory method.
   *
   * @param exception program exception
   * @param charset runner default encoding
   * @return {@link app.zoftwhere.bolt.deluge.DelugeData} instance
   * @since 11.0.0
   */
  static DelugeData forInputStream(Exception exception, Charset charset) {
    return new DelugeData(STREAM_ENCODED, exception, charset);
  }

  /**
   * DelugeData factory method.
   *
   * @param resource program input data resource
   * @param withClass class to load resource with
   * @return {@link app.zoftwhere.bolt.deluge.DelugeData} instance
   * @since 11.0.0
   */
  static DelugeData forResource(String resource, Class<?> withClass) {
    return new DelugeData(RESOURCE, resource, withClass, null);
  }

  /**
   * DelugeData factory method.
   *
   * @param resource program input data resource
   * @param withClass class to load resource with
   * @param charset resource character encoding
   * @return {@link app.zoftwhere.bolt.deluge.DelugeData} instance
   * @since 11.0.0
   */
  static DelugeData forResource(String resource, Class<?> withClass, Charset charset) {
    return new DelugeData(RESOURCE_ENCODED, resource, withClass, charset);
  }

  private final DelugeDataType type;

  private final String[] array;

  private final InputStreamSupplier supplier;

  private final String resource;

  private final Class<?> withClass;

  private final Charset charset;

  private final Exception error;

  /**
   * Constructor for {@link app.zoftwhere.bolt.deluge.DelugeData} (private).
   *
   * @param array {@link java.lang.String} array to use for program input
   * @since 11.0.0
   */
  private DelugeData(String[] array) {
    this.type = ARRAY;
    this.array = array;
    this.supplier =
        () -> {
          throw new DelugeException("attach manual supplier");
        };
    this.withClass = null;
    this.resource = null;
    this.charset = null;
    this.error = null;
  }

  /**
   * Constructor for {@link app.zoftwhere.bolt.deluge.DelugeData} (private).
   *
   * @param array {@link java.lang.String} array to use for program input
   * @param charset data input character encoding
   * @since 11.0.0
   */
  private DelugeData(String[] array, Charset charset) {
    this.type = ARRAY_ENCODED;
    this.array = array;
    this.supplier = newInputStreamSupplier(array, charset);
    this.withClass = null;
    this.resource = null;
    this.charset = charset;
    this.error = null;
  }

  /**
   * Constructor for {@link app.zoftwhere.bolt.deluge.DelugeData} (private).
   *
   * @param type data type
   * @param array {@link java.lang.String} array to use for program input
   * @param charset data input character encoding
   * @since 11.0.0
   */
  private DelugeData(DelugeDataType type, String[] array, Charset charset) {
    Assertions.assertTrue(type == STREAM || type == STREAM_ENCODED);
    this.type = type;
    this.array = array;
    this.supplier = newInputStreamSupplier(array, charset);
    this.withClass = null;
    this.resource = null;
    this.charset = type == STREAM ? null : charset;
    this.error = null;
  }

  /**
   * Constructor for {@link app.zoftwhere.bolt.deluge.DelugeData} (private).
   *
   * @param type data type
   * @param resource program input data resource
   * @param withClass class to load input data resource with
   * @param charset resource character encoding
   * @since 11.0.0
   */
  private DelugeData(DelugeDataType type, String resource, Class<?> withClass, Charset charset) {
    Assertions.assertTrue(type == RESOURCE || type == RESOURCE_ENCODED);
    Assertions.assertTrue(type == RESOURCE_ENCODED || charset == null);
    this.type = type;
    this.array = null;
    this.supplier = () -> withClass.getResourceAsStream(resource);
    this.resource = resource;
    this.withClass = withClass;
    this.charset = charset;
    this.error = null;
  }

  /**
   * Constructor for {@link app.zoftwhere.bolt.deluge.DelugeData} (private).
   *
   * @param type data type
   * @param error program exception
   * @param charset data input character encoding
   * @since 11.0.0
   */
  private DelugeData(DelugeDataType type, Exception error, Charset charset) {
    Assertions.assertTrue(type == STREAM || type == STREAM_ENCODED);
    this.type = type;
    this.array = null;
    this.supplier =
        () -> {
          throw error;
        };
    this.resource = null;
    this.withClass = null;
    this.charset = charset;
    this.error = error;
  }

  /**
   * Return data type.
   *
   * @return data type
   * @since 11.1.0
   */
  public DelugeDataType type() {
    return type;
  }

  /**
   * Return input data {@link java.lang.String} array.
   *
   * @return input data {@link java.lang.String} array
   * @since 11.1.0
   */
  String[] array() {
    return array;
  }

  /**
   * Return program input data stream supplier.
   *
   * @return program input data stream supplier
   * @since 11.1.0
   */
  InputStreamSupplier streamSupplier() {
    return supplier;
  }

  /**
   * Return program data resource.
   *
   * @return program data resource
   * @since 11.1.0
   */
  String resource() {
    return resource;
  }

  /**
   * Return class to load program input resource with.
   *
   * @return class to load program input resource with
   * @since 11.1.0
   */
  Class<?> withClass() {
    return withClass;
  }

  /**
   * Return if program data requires character encoding.
   *
   * @return true if program data requires character encoding, false otherwise.
   * @since 11.1.0
   */
  boolean hasCharset() {
    return type == ARRAY_ENCODED || type == STREAM_ENCODED || type == RESOURCE_ENCODED;
  }

  /**
   * Return program input data character encoding.
   *
   * @return program input data character encoding
   * @since 11.1.0
   */
  Charset charset() {
    return charset;
  }

  /**
   * Return if loading data has an error.
   *
   * @return true if loading data has an error, false otherwise
   * @since 11.1.0
   */
  boolean hasError() {
    return error != null;
  }

  /**
   * Return loading data exception.
   *
   * @return load data exception, null otherwise
   * @since 11.1.0
   */
  Exception error() {
    return error;
  }

  /**
   * Return new input stream supplier.
   *
   * @param charset character encoding for input stream
   * @return new input stream supplier
   */
  InputStreamSupplier newInputStreamSupplier(Charset charset) {
    return newInputStreamSupplier(array, charset);
  }

  /**
   * Return new input stream supplier.
   *
   * @param input program input data {@link String} array
   * @param charset program input data character encoding
   * @return new input stream supplier
   */
  private InputStreamSupplier newInputStreamSupplier(String[] input, Charset charset) {
    if (input == null) {
      return null;
    }

    if (charset == null) {
      return () -> null;
    }

    if (isOrHasNull(input)) {
      return () -> null;
    }

    return () -> newStringArrayInputStream(array, charset);
  }
}
