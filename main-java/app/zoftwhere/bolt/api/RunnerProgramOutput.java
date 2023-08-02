package app.zoftwhere.bolt.api;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

/**
 * Runner program execution output interface.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public interface RunnerProgramOutput
    extends AbstractUnit.Comparison<RunnerPreTest, String>, RunnerPreTest {

  /**
   * Specify the comparator.
   *
   * <p>The comparator will only be loaded if the program output does not contain an exception.
   *
   * <p>The execution result will contain an exception if the loaded comparator is null.
   *
   * @param comparator {@link java.lang.String} {@link java.util.Comparator} for program line output
   * @return {@link app.zoftwhere.bolt.api.RunnerPreTest}
   * @since 1.0.0
   */
  @Override
  RunnerPreTest comparator(Comparator<String> comparator);

  /** {@inheritDoc} */
  @Override
  String[] output();

  /** {@inheritDoc} */
  @Override
  Optional<Exception> error();

  /** {@inheritDoc} */
  @Override
  Duration executionDuration();

  /** {@inheritDoc} */
  @Override
  RunnerAsserter expected(String... expected);

  /** {@inheritDoc} */
  @Override
  RunnerAsserter expected(InputStreamSupplier supplier);

  /** {@inheritDoc} */
  @Override
  RunnerAsserter expected(InputStreamSupplier supplier, Charset charset);

  /** {@inheritDoc} */
  @Override
  RunnerAsserter loadExpectation(String resourceName, Class<?> withClass);

  /** {@inheritDoc} */
  @Override
  RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset);
}
