package app.zoftwhere.bolt;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerProgramResult;
import app.zoftwhere.function.ThrowingConsumer1;

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
     * Asserts that the program run with expected output.
     * <p>
     * Throws {@code BoltAssertionException} for failure or error.
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
     * Asserts that the program run unsuccessfully.
     * <p>
     * Throws {@code BoltAssertionException} for success or error.
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
     * Asserts that the program terminated with an error.
     * <p>
     * Throws {@code BoltAssertionException} for success or failure.
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
     * Asserts program behaviour with custom consumer.
     * <p>
     * The consumer should throw a throwable for undesired behaviour.
     *
     * @param custom custom consumer
     * @since 1.0.0
     */
    @Override
    public void assertCheck(ThrowingConsumer1<RunnerProgramResult> custom) {
        try {
            custom.accept(result);
        }
        catch (Throwable throwable) {
            throw new RunnerException(throwable.getMessage(), throwable.getCause());
        }
    }

    /**
     * Asserts program behaviour with offence triggered consumer.
     * <p>
     * The consumer should throw a throwable for undesired behaviour.
     *
     * @param custom custom consumer
     * @since 5.0.0
     */
    @Override
    public void onOffence(ThrowingConsumer1<RunnerProgramResult> custom) {
        if (!result.isSuccess()) {
            try {
                custom.accept(result);
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
