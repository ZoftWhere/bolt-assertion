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

    @Override
    public void assertSuccess() {
        if (result.isFailure()) {
            throw new RunnerException(result.message().orElse(null));
        }

        if (result.isException()) {
            throw new RunnerException("bolt.runner.asserter.error.found");
        }
    }

    @Override
    public void assertFailure() {
        if (result.isSuccess()) {
            throw new RunnerException("bolt.runner.asserter.success.found");
        }

        if (result.isException()) {
            throw new RunnerException("bolt.runner.asserter.error.found");
        }
    }

    @Override
    public void assertException() {
        if (result.isSuccess()) {
            throw new RunnerException("bolt.runner.asserter.success.found");
        }

        if (result.isFailure()) {
            throw new RunnerException(result.message().orElse(null));
        }
    }

    @Override
    public void assertCheck(RunnerResultConsumer consumer) {
        try {
            consumer.accept(result);
        }
        catch (Throwable throwable) {
            throw new RunnerException(throwable.getMessage(), throwable.getCause());
        }
    }

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

    @Override
    public RunnerProgramResult result() {
        return result;
    }

}
