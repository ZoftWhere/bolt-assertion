package app.zoftwhere.bolt;

import java.util.Arrays;
import java.util.Optional;

import app.zoftwhere.bolt.api.RunnerProgramResult;

import static java.util.Objects.requireNonNull;

/**
 * Bolt program result class.
 *
 * @since 6.0.0
 */
class BoltProgramResult implements RunnerProgramResult {

    private final String[] output;

    private final String[] expected;

    private final int offendingIndex;

    private final String message;

    private final Exception exception;

    BoltProgramResult(String[] output, String[] expected) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
        this.offendingIndex = -1;
        this.message = null;
        this.exception = null;
    }

    BoltProgramResult(String[] output, String[] expected, int offendingIndex, String message) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
        //noinspection ManualMinMaxCalculation
        this.offendingIndex = offendingIndex >= -1 ? offendingIndex : -1;
        this.message = requireNonNull(message);
        this.exception = null;
    }

    BoltProgramResult(String[] output, String[] expected, Exception exception) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
        this.offendingIndex = -1;
        this.message = null;
        this.exception = exception;

        if (exception != null && expected.length != 0) {
            throw new RunnerException("bolt.runner.expected.expectation.length.zero");
        }
    }

    @Override
    public boolean isSuccess() {
        return message == null && exception == null;
    }

    @Override
    public boolean isFailure() {
        return message != null && exception == null;
    }

    @Override
    public boolean isException() {
        return exception != null;
    }

    @Override
    public int offendingIndex() {
        return offendingIndex;
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
    public Optional<String> message() {
        return Optional.ofNullable(message);
    }

    @Override
    public Optional<Exception> exception() {
        return Optional.ofNullable(exception);
    }

}
