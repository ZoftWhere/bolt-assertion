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
 * <p>Bolt program output class.
 * </p>
 * <p>This is a package-private class for providing its functionality.
 * </p>
 *
 * @author Osmund
 * @version 11.3.0
 * @since 6.0.0
 */
class BoltProgramOutput implements RunnerProgramOutput {

    private final Charset encoding;

    private final String[] output;

    private final Exception error;

    private final Duration duration;

    private final Comparator<String> comparator;

    /**
     * <p>Constructor for BoltProgramOutput (package-private).
     * </p>
     * <p>Creates an instance based on the program output and program exception.
     * </p>
     *
     * @param encoding default character encoding
     * @param output   program (actual) output lines
     * @param duration program execution duration
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
     * <p>Constructor for BoltProgramOutput (private).
     * </p>
     *
     * @param encoding   default character encoding
     * @param output     program (actual) output lines
     * @param duration   program execution duration
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

    /** {@inheritDoc} */
    @Override
    public String[] output() {
        return Arrays.copyOf(output, output.length);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Exception> error() {
        return Optional.ofNullable(error);
    }

    /** {@inheritDoc} */
    @Override
    public Duration executionDuration() {
        return duration;
    }

    /** {@inheritDoc} */
    @Override
    public RunnerPreTest comparator(Comparator<String> comparator) {
        if (error != null) {
            return this;
        }

        if (comparator == null) {
            RunnerException nullError = new RunnerException("bolt.runner.expectation.comparator.null");
            return new BoltProgramOutput(encoding, output, Duration.ZERO, nullError);
        }

        return new BoltProgramOutput(encoding, output, duration, comparator);
    }

    /** {@inheritDoc} */
    @Override
    public RunnerAsserter expected(String... expected) {
        String[] expectation = expected == null || expected.length == 0 ? new String[] {""} : expected;
        return newBoltResult(output, expectation, duration, comparator, error);
    }

    /** {@inheritDoc} */
    @Override
    public RunnerAsserter expected(InputStreamSupplier supplier) {
        return newBoltResult(output, supplier, encoding, duration, comparator, error);
    }

    /** {@inheritDoc} */
    @Override
    public RunnerAsserter expected(InputStreamSupplier supplier, Charset charset) {
        return newBoltResult(output, supplier, charset, duration, comparator, error);
    }

    /** {@inheritDoc} */
    @Override
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass) {
        return newBoltResult(output, resourceName, withClass, encoding, duration, comparator, error);
    }

    /** {@inheritDoc} */
    @Override
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset) {
        return newBoltResult(output, resourceName, withClass, charset, duration, comparator, error);
    }

}
