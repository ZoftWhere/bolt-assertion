package app.zoftwhere.bolt;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface.RunnerResultConsumer;
import app.zoftwhere.bolt.api.RunnerResult;

/**
 * Bolt Asserter.
 *
 * @since 6.0.0
 */
class BoltAsserter implements RunnerAsserter {

    private final BoltResult result;

    /**
     * Create an asserter for an execution result.
     *
     * @param result execution result
     * @since 6.0.0
     */
    BoltAsserter(BoltResult result) {
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

        if (result.isError()) {
            throw new RunnerException("bolt.runner.asserter.error.found");
        }
    }

    @Override
    public void assertFailure() {
        if (result.isSuccess()) {
            throw new RunnerException("bolt.runner.asserter.success.found");
        }

        if (result.isError()) {
            throw new RunnerException("bolt.runner.asserter.error.found");
        }
    }

    @Override
    public void assertError() {
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
        catch (Exception e) {
            throw new RunnerException("bolt.runner.assert.check", e);
        }
    }

    @Override
    public void onOffence(RunnerResultConsumer consumer) {
        if (result.isSuccess()) {
            return;
        }

        try {
            consumer.accept(result);
        }
        catch (Exception e) {
            throw new RunnerException("bolt.runner.on.offence", e);
        }
    }

    @Override
    public RunnerResult result() {
        return result;
    }

}
