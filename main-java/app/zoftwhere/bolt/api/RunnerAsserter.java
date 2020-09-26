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
     * <p>Asserts that execution result is for a success state.
     * </p>
     * <p>Throws {@link app.zoftwhere.bolt.RunnerException} for failure state or error state.
     * </p>
     *
     * @since 1.0.0
     */
    @Override
    void assertSuccess();

    /**
     * <p>Asserts that execution result is for a failure state.
     * </p>
     * <p>Throws {@link app.zoftwhere.bolt.RunnerException} for success state or error state.
     * </p>
     *
     * @since 4.0.0
     */
    @Override
    void assertFailure();

    /**
     * <p>Asserts that execution result is for an error state.
     * </p>
     * <p>Throws {@link app.zoftwhere.bolt.RunnerException} for success state or failure state.
     * </p>
     *
     * @since 8.0.0
     */
    @Override
    void assertError();

    /**
     * <p>Asserts execution behaviour with custom consumer.
     * </p>
     * <p>The consumer should throw an exception for undesired behaviour.
     * </p>
     *
     * @param consumer custom consumer
     * @throws RunnerException around {@link java.lang.Exception} thrown by consumer.
     * @since 6.0.0
     */
    @Override
    void assertCheck(RunnerResultConsumer consumer);

    /**
     * <p>Asserts execution behaviour with offence triggered consumer.
     * </p>
     * <p>The consumer should throw an exception for undesired behaviour.
     * </p>
     *
     * @param consumer custom consumer
     * @throws RunnerException around {@link java.lang.Exception} thrown by consumer.
     * @since 6.0.0
     */
    @Override
    void onOffence(RunnerResultConsumer consumer);

    /**
     * <p>Retrieve the execution result.
     * </p>
     *
     * @return execution result
     * @since 8.0.0
     */
    @Override
    RunnerResult result();

}
