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
        if (exception != null) {
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
        if (exception != null) {
            return new BoltAsserter(new BoltProgramResult(output, new String[0], exception));
        }

        if (expected == null || expected.length == 0) {
            return new BoltAsserter(buildTestResult(new String[] {""}, output, null, comparator));
        }

        for (String item : expected) {
            if (item == null) {
                RunnerException exception = new RunnerException("bolt.runner.variable.array.expected.has.null");
                return new BoltAsserter(new BoltProgramResult(output, new String[0], exception));
            }
        }

        return new BoltAsserter(buildTestResult(expected, output, null, comparator));
    }

    @Override
    public RunnerAsserter expected(InputStreamSupplier supplier) {
        return expected(supplier, UTF_8);
    }

    @Override
    public RunnerAsserter expected(InputStreamSupplier streamSupplier, Charset charset) {
        if (exception != null) {
            return new BoltAsserter(new BoltProgramResult(output, new String[0], exception));
        }

        return create(charset, streamSupplier);
    }

    @Override
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass) {
        if (exception != null) {
            return new BoltAsserter(new BoltProgramResult(output, new String[0], exception));
        }

        return loadExpectation(resourceName, withClass, UTF_8);
    }

    @Override
    public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset) {
        if (exception != null) {
            return new BoltAsserter(new BoltProgramResult(output, new String[0], exception));
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
     * Helper method to create a {@link RunnerAsserter} for the expected result {@code InputStream}.
     *
     * @param charset  the charset of the {@code InputStream}
     * @param supplier function to return the {@code InputStream} for the expected result
     * @return a {@link RunnerAsserter} instance
     */
    private RunnerAsserter create(Charset charset, InputStreamSupplier supplier) {
        if (exception != null) {
            return new BoltAsserter(buildTestResult(new String[0], output, exception, comparator));
        }
        if (charset == null) {
            RunnerException exception = new RunnerException("bolt.runner.load.expectation.charset.null");
            return new BoltAsserter(buildTestResult(new String[0], output, exception, comparator));
        }
        if (supplier == null) {
            RunnerException exception = new RunnerException("bolt.runner.load.expectation.supplier.null");
            return new BoltAsserter(buildTestResult(new String[0], output, exception, comparator));
        }

        try (InputStream inputStream = supplier.get()) {
            if (inputStream == null) {
                throw new RunnerException("bolt.runner.load.expectation.stream.null");
            }
            final String[] expected = readArray(() -> new BoltReader(inputStream, charset));
            return new BoltAsserter(buildTestResult(expected, output, null, comparator));
        }
        catch (Throwable e) {
            Exception exception = fromThrowable(e);
            return new BoltAsserter(buildTestResult(new String[0], output, exception, comparator));
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
