package app.zoftwhere.bolt.api;

import app.zoftwhere.bolt.RunnerException;
import app.zoftwhere.bolt.api.RunnerInterface.RunnerResultConsumer;

/**
 * Runner asserter interface.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public interface RunnerAsserter extends AbstractUnit.Assertions<RunnerResult> {

  /**
   * Asserts that execution result is for a success state.
   *
   * <p>Throws {@link app.zoftwhere.bolt.RunnerException} for failure state or error state.
   *
   * @since 1.0.0
   */
  @Override
  void assertSuccess();

  /**
   * Asserts that execution result is for a failure state.
   *
   * <p>Throws {@link app.zoftwhere.bolt.RunnerException} for success state or error state.
   *
   * @since 4.0.0
   */
  @Override
  void assertFailure();

  /**
   * Asserts that execution result is for an error state.
   *
   * <p>Throws {@link app.zoftwhere.bolt.RunnerException} for success state or failure state.
   *
   * @since 8.0.0
   */
  @Override
  void assertError();

  /**
   * Asserts execution behaviour with custom consumer.
   *
   * <p>The consumer should throw an exception for undesired behaviour.
   *
   * @param consumer custom consumer
   * @throws RunnerException around {@link java.lang.Exception} thrown by consumer.
   * @since 6.0.0
   */
  @Override
  void assertCheck(RunnerResultConsumer consumer);

  /**
   * Asserts execution behaviour with offence triggered consumer.
   *
   * <p>The consumer should throw an exception for undesired behaviour.
   *
   * @param consumer custom consumer
   * @throws RunnerException around {@link java.lang.Exception} thrown by consumer.
   * @since 6.0.0
   */
  @Override
  void onOffence(RunnerResultConsumer consumer);

  /**
   * Retrieve the execution result.
   *
   * @return execution result
   * @since 8.0.0
   */
  @Override
  RunnerResult result();
}
