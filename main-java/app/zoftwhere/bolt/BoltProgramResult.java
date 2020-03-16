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
        this.message = message;
        this.exception = null;
    }

    BoltProgramResult(String[] output, String[] expected, Exception exception) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
        this.offendingIndex = -1;
        this.message = null;
        this.exception = exception;
    }

    /**
     * Check if program outputs match successfully.
     *
     * @return {@code true} for success, {@code false} for failure or error
     * @since 1.0.0
     */
    @Override
    public boolean isSuccess() {
        return message == null && exception == null;
    }

    /**
     * Check if program assertion failure.
     *
     * @return {@code true} for failure, {@code false} for success or error
     * @since 4.0.0
     */
    @Override
    public boolean isFailure() {
        return message != null && exception == null;
    }

    /**
     * Check if program terminated with an error.
     *
     * @return {@code true} for error, {@code false} for success or failure
     * @since 4.0.0
     */
    @Override
    public boolean isException() {
        return exception != null;
    }

    /**
     * Retrieve actual program output.
     *
     * @return array copy of actual program output
     * @since 1.0.0
     */
    @Override
    public String[] output() {
        return Arrays.copyOf(output, output.length);
    }

    /**
     * Retrieve expected program output.
     *
     * @return array copy of expected program output
     * @since 1.0.0
     */
    @Override
    public String[] expected() {
        return Arrays.copyOf(expected, expected.length);
    }

    /**
     * Retrieve the offending index for output comparison.
     *
     * @return array index for mismatch, -1 on length or none
     * @since 5.0.0
     */
    public int offendingIndex() {
        return offendingIndex;
    }

    /**
     * Check if program threw a throwable and/or exception.
     *
     * @return {@code Optional} {@code Exception} for thrown throwable
     * @since 1.0.0
     */
    @Override
    public Optional<Exception> exception() {
        return Optional.ofNullable(exception);
    }

    /**
     * Retrieve program message for failure or error.
     *
     * @return {@code Optional} {@code String} for failure or error
     * @since 1.0.0
     */
    @Override
    public Optional<String> message() {
        return Optional.ofNullable(message);
    }

}
