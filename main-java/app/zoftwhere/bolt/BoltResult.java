package app.zoftwhere.bolt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerResult;

import static app.zoftwhere.bolt.BoltReader.readArray;
import static app.zoftwhere.bolt.BoltUtility.arrayHasNull;
import static java.util.Objects.requireNonNull;

/**
 * Bolt execution result class.
 *
 * @since 6.0.0
 */
class BoltResult implements RunnerResult, RunnerAsserter {

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

        return newBoltResult(output, expected, duration, comparator);
    }

    @SuppressWarnings({"WeakerAccess", "RedundantSuppression"})
    static BoltResult newBoltResult(
        String[] output,
        String[] expected,
        Duration duration,
        Comparator<String> comparator
    )
    {
        if (arrayHasNull(expected)) {
            RunnerException exception = new RunnerException("bolt.runner.variable.array.expected.has.null");
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
            final String[] expected = readArray(() -> new BoltReader(inputStream, inputCharset));
            return newBoltResult(output, expected, duration, comparator);
        }
        catch (Exception e) {
            return new BoltResult(output, new String[0], duration, e);
        }
    }

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
     * @param executionDuration {@link Duration} of execution
     * @since 9.0.0
     */
    BoltResult(String[] output, String[] expected, Duration executionDuration) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
        this.executionDuration = requireNonNull(executionDuration);
        this.offendingIndex = -1;
        this.message = null;
        this.error = null;
    }

    /**
     * Create a execution result for a failure state.
     *
     * @param output         program output lines
     * @param expected       program expected output lines
     * @param offendingIndex zero-based index of erroneous line, if any, -1 otherwise.
     * @param message        program failure state message
     * @since 9.0.0
     */
    BoltResult(String[] output, String[] expected, Duration executionDuration, int offendingIndex, String message) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
        this.executionDuration = requireNonNull(executionDuration);
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
     * @param error    execution error
     * @since 9.0.0
     */
    BoltResult(String[] output, String[] expected, Duration executionDuration, Exception error) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
        this.executionDuration = requireNonNull(executionDuration);
        this.offendingIndex = -1;
        this.message = null;
        this.error = requireNonNull(error);
    }

    @Override
    public boolean isSuccess() {
        return message == null && error == null;
    }

    @Override
    public boolean isFailure() {
        return message != null && error == null;
    }

    @Override
    public boolean isError() {
        return error != null;
    }

    @Override
    public String[] output() {
        return Arrays.copyOf(output, output.length);
    }

    @Override
    public String[] expected() {
        return Arrays.copyOf(expected, expected.length);
    }

    @Override
    public int offendingIndex() {
        return offendingIndex;
    }

    @Override
    public Optional<String> message() {
        return Optional.ofNullable(message);
    }

    @Override
    public Optional<Exception> error() {
        return Optional.ofNullable(error);
    }

    @Override
    public Duration executionDuration() {
        return executionDuration;
    }

    @Override
    public void assertSuccess() {
        if (isFailure()) {
            throw new RunnerException(message);
        }

        if (isError()) {
            throw new RunnerException("bolt.runner.asserter.error.found");
        }
    }

    @Override
    public void assertFailure() {
        if (isSuccess()) {
            throw new RunnerException("bolt.runner.asserter.success.found");
        }

        if (isError()) {
            throw new RunnerException("bolt.runner.asserter.error.found");
        }
    }

    @Override
    public void assertError() {
        if (isSuccess()) {
            throw new RunnerException("bolt.runner.asserter.success.found");
        }

        if (isFailure()) {
            throw new RunnerException(message);
        }
    }

    @Override
    public void assertCheck(RunnerInterface.RunnerResultConsumer consumer) {
        try {
            consumer.accept(result());
        }
        catch (Exception e) {
            throw new RunnerException("bolt.runner.assert.check", e);
        }
    }

    @Override
    public void onOffence(RunnerInterface.RunnerResultConsumer consumer) {
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

    @Override
    public RunnerResult result() {
        return this;
    }

}
