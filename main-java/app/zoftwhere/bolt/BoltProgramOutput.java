package app.zoftwhere.bolt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerPreTest;
import app.zoftwhere.bolt.api.RunnerProgramOutput;

import static app.zoftwhere.bolt.BoltReader.readArray;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

/**
 * Bolt program output class.
 *
 * @since 6.0.0
 */
class BoltProgramOutput implements RunnerProgramOutput {

    private final String[] output;

    private final Exception exception;

    private final Comparator<String> comparator;

    BoltProgramOutput(String[] output, Throwable throwable) {
        requireNonNull(output);
        this.output = output;
        this.exception = fromThrowable(throwable);
        this.comparator = null;
    }

    private BoltProgramOutput(String[] output, Exception exception, Comparator<String> comparator) {
        this.output = requireNonNull(output);
        this.exception = exception;
        this.comparator = comparator;
    }

    @Override
    public String[] output() {
        return Arrays.copyOf(output, output.length);
    }

    @Override
    public Optional<Exception> exception() {
        return Optional.ofNullable(exception);
    }

    @Override
    public RunnerPreTest comparator(Comparator<String> comparator) {
        return new BoltProgramOutput(output, exception, comparator);
    }

    @Override
    public RunnerAsserter expected(String... expected) {
        final String[] expectation = expected != null && expected.length > 0 ? expected : new String[] {""};
        return new BoltAsserter(buildTestResult(expectation, output, exception, comparator));
    }

    @Override
    public RunnerAsserter expected(InputStreamSupplier streamSupplier) {
        return create(streamSupplier, UTF_8);
    }

    @Override
    public RunnerAsserter expected(InputStreamSupplier streamSupplier, Charset charset) {
        return create(streamSupplier, charset);
    }

    @Override
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass) {
        return create(() -> withClass.getResourceAsStream(resourceName), UTF_8);
    }

    @Override
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset) {
        return create(() -> withClass.getResourceAsStream(resourceName), charset);
    }

    /**
     * Helper method to create a {@link RunnerAsserter} for the expected result {@code InputStream}.
     *
     * @param getInputStream function to return the {@code InputStream} for the expected result
     * @param charset        the charset of the {@code InputStream}
     * @return a {@link RunnerAsserter} instance
     */
    private RunnerAsserter create(InputStreamSupplier getInputStream, Charset charset) {
        try (InputStream inputStream = getInputStream.get()) {
            if (inputStream == null) {
                throw new NullPointerException("bolt.runner.load.expectation.input.stream.null");
            }
            final String[] expected = readArray(() -> new BoltReader(inputStream, charset));
            return new BoltAsserter(buildTestResult(expected, output, exception, comparator));
        }
        catch (Throwable e) {
            throw new RunnerException("bolt.runner.load.expectation.error", e);
        }
    }

    /**
     * Helper method to build a test result instance.
     *
     * @param expected   the expected program output
     * @param output     the actual program output
     * @param exception  {@code Nullable} exception
     * @param comparator {@code Nullable} comparator
     * @return {@link BoltProgramResult}
     */
    private BoltProgramResult buildTestResult( //
        String[] expected, //
        String[] output, //
        Exception exception, //
        Comparator<String> comparator) //
    {
        if (exception != null) {
            return new BoltProgramResult(output, expected, exception);
        }

        if (expected.length != output.length) {
            int none = -1;
            String message = "bolt.runner.asserter.output.length.mismatch";
            return new BoltProgramResult(output, expected, none, message);
        }
        final int size = output.length;
        if (comparator == null) {
            for (int index = 0; index < size; index++) {
                if (!Objects.equals(expected[index], output[index])) {
                    String message = "bolt.runner.asserter.output.data.mismatch";
                    return new BoltProgramResult(output, expected, index, message);
                }
            }
        }
        else {
            for (int index = 0; index < size; index++) {
                if (comparator.compare(expected[index], output[index]) != 0) {
                    String message = "bolt.runner.asserter.output.data.mismatch";
                    return new BoltProgramResult(output, expected, index, message);
                }
            }
        }

        return new BoltProgramResult(output, expected);
    }

    /**
     * Helper method to convert {@code Throwable} to {@code Exception}.
     *
     * @param throwable the throwable
     * @return {@code Exception}
     */
    private Exception fromThrowable(Throwable throwable) {
        if (throwable == null) { return null; }
        if (throwable instanceof Exception) { return (Exception) throwable; }
        return new RunnerException("bolt.runner.throwable.as.cause", throwable);
    }

}
