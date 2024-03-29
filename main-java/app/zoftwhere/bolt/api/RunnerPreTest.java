package app.zoftwhere.bolt.api;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Optional;

/**
 * Runner pre-test interface.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public interface RunnerPreTest extends AbstractUnit.Expected<RunnerAsserter>, AbstractUnit.Output {

  /**
   * Retrieve the actual program output.
   *
   * @return array copy of the program output
   * @since 1.0.0
   */
  @Override
  String[] output();

  /**
   * Retrieve the execution error.
   *
   * @return {@link java.util.Optional} of the execution error (empty on success or failure)
   * @since 8.0.0
   */
  @Override
  Optional<Exception> error();

  /**
   * Retrieve the execution duration.
   *
   * @return {@link java.time.Duration} duration from execution call till program completion or
   *     execution error.
   * @since 9.0.0
   */
  @Override
  Duration executionDuration();

  /**
   * Specify the expected program output.
   *
   * <p>The expectation will only be loaded if the expectation is not null, and has one or more
   * items.
   *
   * <p>If the expectation is loaded, the program does not have an exception, and the array contains
   * a null, the execution result will be loaded with a corresponding exception.
   *
   * @param expected variable argument for expected program line output
   * @return {@link app.zoftwhere.bolt.api.RunnerAsserter}
   * @since 1.0.0
   */
  @Override
  RunnerAsserter expected(String... expected);

  /**
   * Specify the expected program output.
   *
   * <p>The expectation will only be loaded if the program output does not contain an exception.
   *
   * <p>If the expectation is loading, and an exception occurs, the execution result will be loaded
   * with a corresponding exception.
   *
   * @param supplier {@link java.io.InputStream} supplier for expected program output
   * @return {@link app.zoftwhere.bolt.api.RunnerAsserter}
   * @since 6.0.0
   */
  @Override
  RunnerAsserter expected(InputStreamSupplier supplier);

  /**
   * Specify the expected program output.
   *
   * <p>The expectation will only be loaded if the program output does not contain an exception.
   *
   * <p>If the expectation is loading, and an exception occurs, the execution result will be loaded
   * with a corresponding exception.
   *
   * @param supplier {@link java.io.InputStream} supplier for expected program output
   * @param charset character encoding of supplied {@link java.io.InputStream}
   * @return {@link app.zoftwhere.bolt.api.RunnerAsserter}
   * @since 6.0.0
   */
  @Override
  RunnerAsserter expected(InputStreamSupplier supplier, Charset charset);

  /**
   * Specify the resource to load as expected program output.
   *
   * <p>The expectation will only be loaded if the program output does not contain an exception.
   *
   * <p>If the expectation is loading, and an exception occurs, the execution result will be loaded
   * with a corresponding exception.
   *
   * @param resourceName resource name for loading expected program output
   * @param withClass {@link java.lang.Class} with which to retrieve the expected program output
   * @return {@link app.zoftwhere.bolt.api.RunnerAsserter}
   * @since 1.0.0
   */
  @Override
  RunnerAsserter loadExpectation(String resourceName, Class<?> withClass);

  /**
   * Specify the resource to load as expected program output.
   *
   * <p>The expectation will only be loaded if the program output does not contain an exception.
   *
   * <p>If the expectation is loading, and an exception occurs, the execution result will be loaded
   * with a corresponding exception.
   *
   * @param resourceName resource name for loading expected program output
   * @param withClass {@link java.lang.Class} with which to retrieve the expected program output
   * @param charset character encoding of resource
   * @return {@link app.zoftwhere.bolt.api.RunnerAsserter}
   * @since 1.0.0
   */
  @Override
  RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset);
}
