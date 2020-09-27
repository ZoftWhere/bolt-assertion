package app.zoftwhere.bolt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunnerResultConsumer;
import app.zoftwhere.bolt.api.RunnerResult;

import static app.zoftwhere.bolt.BoltReader.readArray;
import static app.zoftwhere.bolt.BoltUtility.arrayHasNull;
import static java.util.Objects.requireNonNull;

/**
 * <p>Bolt execution result class.
 * </p>
 * <p>This is a package-private class for providing this functionality.
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
class BoltResult implements RunnerResult, RunnerAsserter {

    /**
     * BoltResult factory method (package-private).
     *
     * @param output     program (actual) output
     * @param expected   program expected output
     * @param duration   program execution duration
     * @param comparator program output comparator
     * @param error      program error
     * @return {@link app.zoftwhere.bolt.BoltResult}
     * @since 11.0.0
     */
    static BoltResult newBoltResult(
        String[] output,
        String[] expected,
        Duration duration,
        Comparator<String> comparator,
        Exception error
    )
    {
        if (error != null) {
            return new BoltResult(output, expected, duration, error);
        }

        return performComparison(output, expected, duration, comparator);
    }

    /**
     * BoltResult factory method (package-private).
     *
     * @param output       program (actual) output
     * @param supplier     input stream supplier
     * @param inputCharset input stream character encoding
     * @param duration     program execution duration
     * @param comparator   program output comparator
     * @param error        program error
     * @return {@link app.zoftwhere.bolt.BoltResult}
     * @since 11.0.0
     */
    static BoltResult newBoltResult(
        String[] output,
        InputStreamSupplier supplier,
        Charset inputCharset,
        Duration duration,
        Comparator<String> comparator,
        Exception error
    )
    {
        if (error != null) {
            return new BoltResult(output, new String[0], duration, error);
        }

        if (inputCharset == null) {
            RunnerException exception = new RunnerException("bolt.runner.load.expectation.charset.null");
            return new BoltResult(output, new String[0], duration, exception);
        }

        if (supplier == null) {
            RunnerException exception = new RunnerException("bolt.runner.load.expectation.supplier.null");
            return new BoltResult(output, new String[0], duration, exception);
        }

        try (InputStream inputStream = supplier.get()) {
            if (inputStream == null) {
                // throw new RunnerException("bolt.runner.load.expectation.stream.null");
                RunnerException exception = new RunnerException("bolt.runner.load.expectation.stream.null");
                return new BoltResult(output, new String[0], duration, exception);
            }
            String[] expected = readArray(() -> new BoltReader(inputStream, inputCharset));
            return performComparison(output, expected, duration, comparator);
        }
        catch (Exception e) {
            return new BoltResult(output, new String[0], duration, e);
        }
    }

    /**
     * BoltResult factory method (package-private).
     *
     * @param output       program output
     * @param resourceName input resource name
     * @param withClass    input resource class
     * @param charset      input resource character encoding
     * @param duration     program execution duration
     * @param comparator   program output comparator
     * @param error        program error
     * @return {@link app.zoftwhere.bolt.BoltResult}
     * @since 11.0.0
     */
    static BoltResult newBoltResult(
        String[] output,
        String resourceName,
        Class<?> withClass,
        Charset charset,
        Duration duration,
        Comparator<String> comparator,
        Exception error
    )
    {
        if (resourceName == null) {
            InputStreamSupplier supplier = () -> {
                throw new RunnerException("bolt.runner.load.expectation.resource.name.null");
            };
            return newBoltResult(output, supplier, charset, duration, comparator, error);
        }

        if (withClass == null) {
            InputStreamSupplier supplier = () -> {
                throw new RunnerException("bolt.runner.load.expectation.resource.class.null");
            };
            return newBoltResult(output, supplier, charset, duration, comparator, error);
        }

        if (withClass.getResource(resourceName) == null) {
            InputStreamSupplier supplier = () -> {
                throw new RunnerException("bolt.runner.load.expectation.resource.not.found");
            };
            return newBoltResult(output, supplier, charset, duration, comparator, error);
        }

        InputStreamSupplier supplier = () -> withClass.getResourceAsStream(resourceName);
        return newBoltResult(output, supplier, charset, duration, comparator, error);
    }

    private final String[] output;

    private final String[] expected;

    private final int offendingIndex;

    private final String message;

    private final Exception error;

    private final Duration executionDuration;

    /**
     * Create a execution result for a success state.
     *
     * @param output            program output lines
     * @param expected          expected program output lines
     * @param duration       program execution duration
     * @since 9.0.0
     */
    BoltResult(String[] output, String[] expected, Duration duration) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
        this.executionDuration = requireNonNull(duration);
        this.offendingIndex = -1;
        this.message = null;
        this.error = null;
    }

    /**
     * Create a execution result for a failure state.
     *
     * @param output         program output lines
     * @param expected       program expected output lines
     * @param duration       program execution duration
     * @param offendingIndex zero-based index of erroneous line, if any, -1 otherwise.
     * @param message        program failure state message
     * @since 9.0.0
     */
    BoltResult(String[] output, String[] expected, Duration duration, int offendingIndex, String message) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
        this.executionDuration = requireNonNull(duration);
        //noinspection ManualMinMaxCalculation
        this.offendingIndex = offendingIndex >= -1 ? offendingIndex : -1;
        this.message = requireNonNull(message);
        this.error = null;
    }

    /**
     * Create a execution result for an error state.
     *
     * @param output   program output lines
     * @param expected program expected output lines
     * @param duration program execution duration
     * @param error    execution error
     * @since 9.0.0
     */
    BoltResult(String[] output, String[] expected, Duration duration, Exception error) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
        this.executionDuration = requireNonNull(duration);
        this.offendingIndex = -1;
        this.message = null;
        this.error = requireNonNull(error);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSuccess() {
        return message == null && error == null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFailure() {
        return message != null && error == null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isError() {
        return error != null;
    }

    /** {@inheritDoc} */
    @Override
    public String[] output() {
        return Arrays.copyOf(output, output.length);
    }

    /** {@inheritDoc} */
    @Override
    public String[] expected() {
        return Arrays.copyOf(expected, expected.length);
    }

    /** {@inheritDoc} */
    @Override
    public int offendingIndex() {
        return offendingIndex;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> message() {
        return Optional.ofNullable(message);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Exception> error() {
        return Optional.ofNullable(error);
    }

    /** {@inheritDoc} */
    @Override
    public Duration executionDuration() {
        return executionDuration;
    }

    /** {@inheritDoc} */
    @Override
    public void assertSuccess() {
        if (isFailure()) {
            throw new RunnerException(message);
        }

        if (isError()) {
            throw new RunnerException("bolt.runner.asserter.error.found");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void assertFailure() {
        if (isSuccess()) {
            throw new RunnerException("bolt.runner.asserter.success.found");
        }

        if (isError()) {
            throw new RunnerException("bolt.runner.asserter.error.found");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void assertError() {
        if (isSuccess()) {
            throw new RunnerException("bolt.runner.asserter.success.found");
        }

        if (isFailure()) {
            throw new RunnerException(message);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void assertCheck(RunnerResultConsumer consumer) {
        try {
            consumer.accept(result());
        }
        catch (Exception e) {
            throw new RunnerException("bolt.runner.assert.check", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onOffence(RunnerResultConsumer consumer) {
        if (isSuccess()) {
            return;
        }

        try {
            consumer.accept(result());
        }
        catch (Exception e) {
            throw new RunnerException("bolt.runner.on.offence", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public RunnerResult result() {
        return this;
    }

    private static BoltResult performComparison(
        String[] output,
        String[] expected,
        Duration duration,
        Comparator<String> comparator
    )
    {
        if (arrayHasNull(expected)) {
            RunnerException exception = new RunnerException("bolt.runner.variable.argument.expected.has.null");
            return new BoltResult(output, expected, duration, exception);
        }

        if (expected.length != output.length) {
            int none = -1;
            String message = "bolt.runner.asserter.output.length.mismatch";
            return new BoltResult(output, expected, duration, none, message);
        }

        final int size = output.length;
        if (comparator == null) {
            for (int index = 0; index < size; index++) {
                if (!Objects.equals(expected[index], output[index])) {
                    String message = "bolt.runner.asserter.output.data.mismatch";
                    return new BoltResult(output, expected, duration, index, message);
                }
            }
        }
        else {
            for (int index = 0; index < size; index++) {
                if (comparator.compare(expected[index], output[index]) != 0) {
                    String message = "bolt.runner.asserter.output.data.mismatch";
                    return new BoltResult(output, expected, duration, index, message);
                }
            }
        }

        return new BoltResult(output, expected, duration);
    }

}
