package app.zoftwhere.bolt;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface.RunnerResultConsumer;
import app.zoftwhere.bolt.api.RunnerProgramResult;

/**
 * Bolt program result asserter.
 *
 * @since 6.0.0
 */
class BoltAsserter implements RunnerAsserter {

    private final BoltProgramResult result;

    BoltAsserter(BoltProgramResult result) {
        this.result = result;
    }

    /**
     * <p>Asserts that the program run with expected output.
     * </p>
     * <p>Throws {@code BoltAssertionException} for failure or error.
     * </p>
     *
     * @since 1.0.0
     */
    @Override
    public void assertSuccess() {
        if (result.isFailure()) {
            throw new RunnerException(result.message().orElse(null));
        }

        if (result.isException()) {
            throw new RunnerException("bolt.runner.asserter.error.found");
        }
    }

    /**
     * <p>Asserts that the program run unsuccessfully.
     * </p>
     * <p>Throws {@code BoltAssertionException} for success or error.
     * </p>
     *
     * @since 4.0.0
     */
    @Override
    public void assertFailure() {
        if (result.isSuccess()) {
            throw new RunnerException("bolt.runner.asserter.success.found");
        }

        if (result.isException()) {
            throw new RunnerException("bolt.runner.asserter.error.found");
        }
    }

    /**
     * <p>Asserts that the program terminated with an error.
     * </p>
     * <p>Throws {@code BoltAssertionException} for success or failure.
     * </p>
     *
     * @since 1.0.0
     */
    @Override
    public void assertException() {
        if (result.isSuccess()) {
            throw new RunnerException("bolt.runner.asserter.success.found");
        }

        if (result.isFailure()) {
            throw new RunnerException(result.message().orElse(null));
        }
    }

    /**
     * <p>Asserts program behaviour with custom consumer.
     * </p>
     * <p>The consumer should throw a throwable for undesired behaviour.
     * </p>
     *
     * @param consumer custom consumer
     * @since 1.0.0
     */
    @Override
    public void assertCheck(RunnerResultConsumer consumer) {
        try {
            consumer.accept(result);
        }
        catch (Throwable throwable) {
            throw new RunnerException(throwable.getMessage(), throwable.getCause());
        }
    }

    /**
     * <p>Asserts program behaviour with offence triggered consumer.
     * </p>
     * <p>The consumer should throw a throwable for undesired behaviour.
     * </p>
     *
     * @param consumer custom consumer
     * @since 5.0.0
     */
    @Override
    public void onOffence(RunnerResultConsumer consumer) {
        if (!result.isSuccess()) {
            try {
                consumer.accept(result);
            }
            catch (Throwable throwable) {
                throw new RunnerException(throwable.getMessage(), throwable.getCause());
            }
        }
    }

    /**
     * @return the program test result
     * @since 1.0.0
     */
    @Override
    public RunnerProgramResult result() {
        return result;
    }

}
