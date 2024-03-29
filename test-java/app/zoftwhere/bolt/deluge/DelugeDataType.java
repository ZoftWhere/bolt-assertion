package app.zoftwhere.bolt.deluge;

/**
 * Deluge Data Type enum.
 *
 * @author Osmund
 * @since 11.0.0
 */
public enum DelugeDataType {

  /**
   * Array data type.
   *
   * @since 11.0.0
   */
  ARRAY,

  /**
   * Array data type with character encoding.
   *
   * @since 11.0.0
   */
  ARRAY_ENCODED,

  /**
   * Input stream data type.
   *
   * @since 11.0.0
   */
  STREAM,

  /**
   * Input stream data type with character encoding.
   *
   * @since 11.0.0
   */
  STREAM_ENCODED,

  /**
   * Resource data type.
   *
   * @since 11.0.0
   */
  RESOURCE,

  /**
   * Resource data type with character encoding.
   *
   * @since 11.0.0
   */
  RESOURCE_ENCODED;

  /**
   * Constructor for DelugeDataType (private).
   *
   * @since 11.0.0
   */
  DelugeDataType() {}
}
