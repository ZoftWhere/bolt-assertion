package app.zoftwhere.bolt.api;

import java.time.Duration;
import java.util.Optional;

/**
 * Runner execution result interface.
 *
 * @since 8.0.0
 */
public interface RunnerResult extends AbstractUnit.Result {

    /**
     * <p>Check if success state.
     * </p>
     * <p>The execution result will be in a success state if the runner was able to execute the program, and the
     * program output matched the expected program output.
     * </p>
     *
     * @return {@code true} for success state, {@code false} for failure state or error state
     * @since 8.0.0
     */
    @Override
    boolean isSuccess();

    /**
     * <p>Check if failure state.
     * </p>
     * <p>The execution result will be in a failure state if the runner was able to execute the program, but the
     * program output does not match the expected program output.
     * </p>
     *
     * @return {@code true} for failure state, {@code false} for success state or error state
     * @since 8.0.0
     */
    @Override
    boolean isFailure();

    /**
     * <p>Check if error state.
     * </p>
     * <p>The execution result will be in an error state if the runner was unable to execute the program, or the
     * program threw an exception during execution.
     * </p>
     *
     * @return {@code true} for error state, {@code false} for success state or failure state
     * @since 8.0.0
     */
    @Override
    boolean isError();

    /**
     * Retrieve (actual) program output.
     *
     * @return array copy of (actual) program output
     * @since 1.0.0
     */
    @Override
    String[] output();

    /**
     * Retrieve expected program output.
     *
     * @return array copy of expected program output
     * @since 1.0.0
     */
    @Override
    String[] expected();

    /**
     * Retrieve the execution duration.
     *
     * @return {@link java.time.Duration} duration from execution call till program completion or execution error.
     * @since 9.0.0
     */
    @Override
    Duration executionDuration();

    /**
     * Retrieve the offending index for output comparison.
     *
     * @return array index for mismatch, -1 on length or none
     * @since 5.0.0
     */
    @Override
    int offendingIndex();

    /**
     * Retrieve program message for failure state or error state.
     *
     * @return {@link java.util.Optional} {@link java.lang.String} for failure state or error state
     * @since 8.0.0
     */
    @Override
    Optional<String> message();

    /**
     * Retrieve execution error for error state.
     *
     * @return {@code Optional} {@link java.lang.Exception} for execution error
     * @since 8.0.0
     */
    @Override
    Optional<Exception> error();

}
