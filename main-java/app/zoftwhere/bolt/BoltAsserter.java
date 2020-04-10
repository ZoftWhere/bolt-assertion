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
        if (result == null) {
            throw new RunnerException("bolt.runner.asserter.result.null");
        }

        this.result = result;
    }

    @Override
    public void assertSuccess() {
        if (result.isFailure()) {
            throw new RunnerException(result.message().orElse(""));
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
            throw new RunnerException(result.message().orElse(""));
        }
    }

    @Override
    public void assertCheck(RunnerResultConsumer consumer) {
        try {
            consumer.accept(result);
        }
        catch (Throwable throwable) {
            throw new RunnerException("bolt.runner.assert.check", throwable);
        }
    }

    @Override
    public void onOffence(RunnerResultConsumer consumer) {
        if (!result.isSuccess()) {
            try {
                consumer.accept(result);
            }
            catch (Throwable throwable) {
                throw new RunnerException("bolt.runner.on.offence", throwable);
            }
        }
    }

    @Override
    public RunnerProgramResult result() {
        return result;
    }

}
