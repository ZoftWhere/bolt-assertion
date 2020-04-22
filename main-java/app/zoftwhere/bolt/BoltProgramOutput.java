package app.zoftwhere.bolt;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerPreTest;
import app.zoftwhere.bolt.api.RunnerProgramOutput;

import static app.zoftwhere.bolt.BoltResult.newBoltResult;
import static java.util.Objects.requireNonNull;

/**
 * Bolt program output class.
 *
 * @since 6.0.0
 */
class BoltProgramOutput implements RunnerProgramOutput {

    private final Charset encoding;

    private final String[] output;

    private final Exception error;

    private final Duration duration;

    private final Comparator<String> comparator;

    /**
     * Create an program output instance based on the program output and program exception.
     *
     * @param output   program output lines
     * @param duration duration of program execution
     * @param error    execution error, if any, null otherwise
     * @since 11.0.0
     */
    BoltProgramOutput(Charset encoding, String[] output, Duration duration, Exception error) {
        this.encoding = encoding;
        this.output = requireNonNull(output);
        this.error = error;
        this.duration = duration;
        this.comparator = null;
    }

    /**
     * Private constructor for program output.
     *
     * @param output     program output lines
     * @param duration   duration of program execution
     * @param comparator program output comparator, if any, null otherwise
     * @since 11.0.0
     */
    private BoltProgramOutput(Charset encoding, String[] output, Duration duration, Comparator<String> comparator) {
        this.encoding = encoding;
        this.output = requireNonNull(output);
        this.duration = duration;
        this.error = null;
        this.comparator = comparator;
    }

    @Override
    public String[] output() {
        return Arrays.copyOf(output, output.length);
    }

    @Override
    public Optional<Exception> error() {
        return Optional.ofNullable(error);
    }

    @Override
    public Duration executionDuration() {
        return duration;
    }

    @Override
    public RunnerPreTest comparator(Comparator<String> comparator) {
        if (error != null) {
            return this;
        }

        if (comparator == null) {
            RunnerException exception = new RunnerException("bolt.runner.expectation.comparator.null");
            return new BoltProgramOutput(encoding, output, Duration.ZERO, exception);
        }

        return new BoltProgramOutput(encoding, output, duration, comparator);
    }

    @Override
    public RunnerAsserter expected(String... expected) {
        String[] expectation = expected == null || expected.length == 0 ? new String[] {""} : expected;
        return BoltResult.newBoltResult(output, expectation, duration, comparator, error);
    }

    @Override
    public RunnerAsserter expected(InputStreamSupplier supplier) {
        return BoltResult.newBoltResult(output, supplier, encoding, duration, comparator, error);
    }

    @Override
    public RunnerAsserter expected(InputStreamSupplier supplier, Charset charset) {
        return BoltResult.newBoltResult(output, supplier, charset, duration, comparator, error);
    }

    @Override
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass) {
        return newBoltResult(output, resourceName, withClass, encoding, duration, comparator, error);
    }

    @Override
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset) {
        return newBoltResult(output, resourceName, withClass, charset, duration, comparator, error);
    }

}
