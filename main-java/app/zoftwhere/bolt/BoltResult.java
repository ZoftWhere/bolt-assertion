package app.zoftwhere.bolt;

import java.util.Arrays;
import java.util.Optional;

import app.zoftwhere.bolt.api.RunnerResult;

import static java.util.Objects.requireNonNull;

/**
 * Bolt execution result class.
 *
 * @since 6.0.0
 */
class BoltResult implements RunnerResult {

    private final String[] output;

    private final String[] expected;

    private final int offendingIndex;

    private final String message;

    private final Exception error;

    /**
     * Create a execution result for a success state.
     *
     * @param output   program output lines
     * @param expected expected program output lines
     * @since 6.0.0
     */
    BoltResult(String[] output, String[] expected) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
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
     * @since 6.0.0
     */
    BoltResult(String[] output, String[] expected, int offendingIndex, String message) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
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
     * @since 6.0.0
     */
    BoltResult(String[] output, String[] expected, Exception error) {
        this.output = requireNonNull(output);
        this.expected = requireNonNull(expected);
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
    public Optional<Exception> error() {
        return Optional.ofNullable(error);
    }

}