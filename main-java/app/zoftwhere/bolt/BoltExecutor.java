package app.zoftwhere.bolt;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Bolt Executor functional interface.
 *
 * <p>This is a package-private interface for creating internal implementations with lambdas.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 9.0.0
 */
@FunctionalInterface
interface BoltExecutor {

  /**
   * Program execution method.
   *
   * @param arguments program arguments
   * @param inputCharset character encoding for {@link java.io.InputStream}
   * @param inputStream {@link java.io.InputStream}
   * @param outputCharset character encoding for {@link java.io.OutputStream}
   * @param outputStream {@link java.io.OutputStream}
   * @return {@code null} for execution success, {@link java.lang.Exception} with program error
   *     otherwise
   * @since 9.0.0
   */
  Exception execute(
      String[] arguments,
      Charset inputCharset,
      InputStream inputStream,
      Charset outputCharset,
      OutputStream outputStream);
}
