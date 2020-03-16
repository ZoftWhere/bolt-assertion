package app.zoftwhere.bolt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerPreTest;
import app.zoftwhere.function.ThrowingFunction0;

import static app.zoftwhere.bolt.RunnerReader.readArray;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

/**
 * Bolt common output class.
 *
 * @since 6.0.0
 */
class BoltCommonOutput implements RunnerPreTest {

    private final String[] output;

    private final Exception exception;

    private final Comparator<String> comparator;

    BoltCommonOutput(String[] output, Exception exception, Comparator<String> comparator) {
        this.output = requireNonNull(output);
        this.exception = exception;
        this.comparator = comparator;
    }

    /**
     * Retrieve the actual program output.
     *
     * @return array copy of the program output
     * @since 1.0.0
     */
    @Override
    public String[] output() {
        return Arrays.copyOf(output, output.length);
    }

    /**
     * Retrieve the program error.
     *
     * @return the program throwable and/or exception, if thrown, null otherwise
     * @since 1.0.0
     */
    @Override
    public Exception exception() {
        return exception;
    }

    /**
     * Specify the expected program output.
     *
     * @param expected the expected program output
     * @return {@link RunnerAsserter}
     * @since 1.0.0
     */
    public RunnerAsserter expected(String... expected) {
        final String[] expectation = expected != null && expected.length > 0 ? expected : new String[] {""};
        final BoltProgramResult testResult = buildTestResult(expectation, output, exception, comparator);
        return new BoltAsserter(testResult);
    }

    /**
     * Specify the expected program output.
     *
     * @param getInputStream {@code InputStream} function for expected program output
     * @return {@link RunnerAsserter}
     * @since 1.0.0
     */
    public RunnerAsserter expected(ThrowingFunction0<InputStream> getInputStream) {
        return create(getInputStream, UTF_8);
    }

    /**
     * Specify the expected program output.
     *
     * @param getInputStream {@code InputStream} function for the expected program output.
     * @param charset        the {@code InputStream} character set encoding
     * @return {@link RunnerAsserter}
     * @since 1.0.0
     */
    public RunnerAsserter expected(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
        return create(getInputStream, charset);
    }

    /**
     * Specify the resource to load as expected program output.
     *
     * @param resourceName the resource name to load as expected program output
     * @param withClass    the class to load the resource with
     * @return {@link RunnerAsserter}
     * @since 1.0.0
     */
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass) {
        return create(() -> withClass.getResourceAsStream(resourceName), UTF_8);
    }

    /**
     * Specify the resource to load as expected program output.
     *
     * @param resourceName the resource name to load as expected program output
     * @param withClass    the class to load the resource with
     * @param charset      the character set encoding of the resource
     * @return {@link RunnerAsserter}
     * @since 1.0.0
     */
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset) {
        return create(() -> withClass.getResourceAsStream(resourceName), charset);
    }

    /**
     * Creates a {@link RunnerAsserter} for the expected result {@code InputStream}.
     *
     * @param getInputStream function to return the {@code InputStream} for the expected result
     * @param charset        the charset of the {@code InputStream}
     * @return a {@link RunnerAsserter} instance
     */
    private RunnerAsserter create(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
        try (InputStream inputStream = getInputStream.accept()) {
            if (inputStream == null) {
                throw new NullPointerException("bolt.runner.load.expectation.input.stream.null");
            }
            final String[] expected = readArray(() -> new RunnerReader(inputStream, charset));
            return expected(expected);
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

}
