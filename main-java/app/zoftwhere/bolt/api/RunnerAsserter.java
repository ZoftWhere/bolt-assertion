package app.zoftwhere.bolt.api;

import app.zoftwhere.bolt.RunnerException;
import app.zoftwhere.bolt.api.RunnerInterface.RunnerResultConsumer;

/**
 * Runner program result asserter interface.
 *
 * @since 6.0.0
 */
public interface RunnerAsserter extends AbstractUnit.Assertions<RunnerProgramResult> {

    /**
     * <p>Asserts that the program run with expected output.
     * </p>
     * <p>Throws {@link RunnerException} for failure or error.
     * </p>
     *
     * @since 1.0.0
     */
    @Override
    void assertSuccess();

    /**
     * <p>Asserts that the program run unsuccessfully.
     * </p>
     * <p>Throws {@link RunnerException} for success or error.
     * </p>
     *
     * @since 4.0.0
     */
    @Override
    void assertFailure();

    /**
     * <p>Asserts that the program terminated with an error.
     * </p>
     * <p>Throws {@link RunnerException} for success or failure.
     * </p>
     *
     * @since 1.0.0
     */
    @Override
    void assertException();

    /**
     * <p>Asserts program behaviour with custom consumer.
     * </p>
     * <p>The consumer should throw a throwable for undesired behaviour.
     * </p>
     *
     * @param consumer custom consumer
     * @throws RunnerException around {@link Throwable} thrown by consumer.
     * @since 6.0.0
     */
    @Override
    void assertCheck(RunnerResultConsumer consumer);

    /**
     * <p>Asserts program behaviour with offence triggered consumer.
     * </p>
     * <p>The consumer should throw a throwable for undesired behaviour.
     * </p>
     *
     * @param consumer custom consumer
     * @throws RunnerException around {@link Throwable} thrown by consumer.
     * @since 6.0.0
     */
    @Override
    void onOffence(RunnerResultConsumer consumer);

    /**
     * <p>Retrieve the program test result.
     * </p>
     *
     * @return the program test result
     * @since 6.0.0
     */
    @Override
    RunnerProgramResult result();

}
