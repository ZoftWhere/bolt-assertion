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

    private final Exception error;

    private final Comparator<String> comparator;

    /**
     * Create an program output instance based on the program output and program exception.
     *
     * @param output program output lines
     * @param error  execution error, if any, null otherwise
     * @since 6.0.0
     */
    BoltProgramOutput(String[] output, Exception error) {
        this.output = requireNonNull(output);
        this.error = error;
        this.comparator = null;
    }

    /**
     * Private constructor for program output.
     *
     * @param output     program output lines
     * @param error      execution error, if any, null otherwise
     * @param comparator program output comparator, if any, null otherwise
     * @since 6.0.0
     */
    private BoltProgramOutput(String[] output, Exception error, Comparator<String> comparator) {
        this.output = requireNonNull(output);
        this.error = error;
        this.comparator = comparator;
    }

    @Override
    public String[] output() {
        return Arrays.copyOf(output, output.length);
    }

    @Override
    public Optional<Exception> error() {
        return Optional.ofNullable(error);
    }

    @Override
    public RunnerPreTest comparator(Comparator<String> comparator) {
        if (error != null) {
            return this;
        }

        if (comparator == null) {
            RunnerException exception = new RunnerException("bolt.runner.expectation.comparator.null");
            return new BoltProgramOutput(output, exception, null);
        }

        return new BoltProgramOutput(output, null, comparator);
    }

    @Override
    public RunnerAsserter expected(String... expected) {
        String[] expectation = expected == null || expected.length == 0 ? new String[] {""} : expected;

        if (error != null) {
            return new BoltAsserter(new BoltResult(output, expectation, error));
        }

        for (String item : expectation) {
            if (item == null) {
                RunnerException exception = new RunnerException("bolt.runner.variable.array.expected.has.null");
                return new BoltAsserter(new BoltResult(output, expectation, exception));
            }
        }

        return new BoltAsserter(buildTestResult(output, expectation, comparator));
    }

    @Override
    public RunnerAsserter expected(InputStreamSupplier supplier) {
        return expected(supplier, UTF_8);
    }

    @Override
    public RunnerAsserter expected(InputStreamSupplier supplier, Charset charset) {
        if (error != null) {
            return new BoltAsserter(new BoltResult(output, new String[0], error));
        }

        return create(charset, supplier);
    }

    @Override
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass) {
        if (error != null) {
            return new BoltAsserter(new BoltResult(output, new String[0], error));
        }

        return loadExpectation(resourceName, withClass, UTF_8);
    }

    @Override
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset) {
        if (error != null) {
            return new BoltAsserter(new BoltResult(output, new String[0], error));
        }
        if (resourceName == null) {
            return create(charset, () -> {
                throw new RunnerException("bolt.runner.load.expectation.resource.name.null");
            });
        }
        if (withClass == null) {
            return create(charset, () -> {
                throw new RunnerException("bolt.runner.load.expectation.resource.class.null");
            });
        }
        if (withClass.getResource(resourceName) == null) {
            return create(charset, () -> {
                throw new RunnerException("bolt.runner.load.expectation.resource.not.found");
            });
        }

        return create(charset, () -> withClass.getResourceAsStream(resourceName));
    }

    /**
     * Helper method to create a {@link RunnerAsserter} for the expected result {@link InputStream}.
     *
     * @param charset  character encoding of {@link InputStream}
     * @param supplier {@link InputStream} supplier for the expected program result
     * @return a {@link RunnerAsserter} instance
     */
    private RunnerAsserter create(Charset charset, InputStreamSupplier supplier) {
        if (charset == null) {
            RunnerException exception = new RunnerException("bolt.runner.load.expectation.charset.null");
            BoltResult result = new BoltResult(output, new String[0], exception);
            return new BoltAsserter(result);
        }
        if (supplier == null) {
            RunnerException exception = new RunnerException("bolt.runner.load.expectation.supplier.null");
            BoltResult result = new BoltResult(output, new String[0], exception);
            return new BoltAsserter(result);
        }

        try (InputStream inputStream = supplier.get()) {
            if (inputStream == null) {
                throw new RunnerException("bolt.runner.load.expectation.stream.null");
            }
            final String[] expected = readArray(() -> new BoltReader(inputStream, charset));
            return new BoltAsserter(buildTestResult(output, expected, comparator));
        }
        catch (Exception e) {
            BoltResult result = new BoltResult(output, new String[0], e);
            return new BoltAsserter(result);
        }
    }

    /**
     * Helper method to build a {@link BoltResult} instance.
     *
     * @param output     the actual program output
     * @param expected   the expected program output
     * @param comparator {@code Nullable} comparator
     * @return {@link BoltResult}
     */
    private BoltResult buildTestResult(String[] output, String[] expected, Comparator<String> comparator) {
        if (expected.length != output.length) {
            int none = -1;
            String message = "bolt.runner.asserter.output.length.mismatch";
            return new BoltResult(output, expected, none, message);
        }

        final int size = output.length;
        if (comparator == null) {
            for (int index = 0; index < size; index++) {
                if (!Objects.equals(expected[index], output[index])) {
                    String message = "bolt.runner.asserter.output.data.mismatch";
                    return new BoltResult(output, expected, index, message);
                }
            }
        }
        else {
            for (int index = 0; index < size; index++) {
                if (comparator.compare(expected[index], output[index]) != 0) {
                    String message = "bolt.runner.asserter.output.data.mismatch";
                    return new BoltResult(output, expected, index, message);
                }
            }
        }

        return new BoltResult(output, expected);
    }

}
