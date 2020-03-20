package app.zoftwhere.bolt.api;

import java.util.Optional;

/**
 * Runner program execution result interface.
 *
 * @since 6.0.0
 */
public interface RunnerProgramResult extends AbstractUnit.TestResult {

    /**
     * Check if program outputs match successfully.
     *
     * @return {@code true} for success, {@code false} for failure or error
     * @since 1.0.0
     */
    @Override
    boolean isSuccess();

    /**
     * Check if program assertion failure.
     *
     * @return {@code true} for failure, {@code false} for success or error
     * @since 4.0.0
     */
    @Override
    boolean isFailure();

    /**
     * Check if program terminated with an error.
     *
     * @return {@code true} for error, {@code false} for success or failure
     * @since 4.0.0
     */
    @Override
    boolean isException();

    /**
     * Retrieve actual program output.
     *
     * @return array copy of actual program output
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
     * Retrieve the offending index for output comparison.
     *
     * @return array index for mismatch, -1 on length or none
     * @since 5.0.0
     */
    @Override
    int offendingIndex();

    /**
     * Retrieve program message for failure or error.
     *
     * @return {@code Optional} {@code String} for failure or error
     * @since 1.0.0
     */
    @Override
    Optional<String> message();

    /**
     * Check if program threw a throwable and/or exception.
     *
     * @return {@code Optional} {@code Exception} for thrown throwable
     * @since 1.0.0
     */
    @Override
    Optional<Exception> exception();

}
