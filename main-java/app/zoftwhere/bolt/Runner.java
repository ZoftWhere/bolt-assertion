package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import app.zoftwhere.bolt.RunnerInterfaces.AbstractTestResult;
import app.zoftwhere.function.ThrowingConsumer1;
import app.zoftwhere.function.ThrowingConsumer2;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

import static app.zoftwhere.bolt.RunnerReader.readArray;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

/**
 * Bolt Assertion Runner.
 */
public class Runner implements RunnerInterfaces.IRunner {

    /**
     * This is an immutable runner (so get one, and run all the tests you need).
     *
     * @return an immutable runner instance
     * @since 1.0.0
     */
    public static Runner newRunner() {
        return new Runner();
    }

    /**
     * Constructor for a reusable, immutable instance of the runner (more than one test can be run with it).
     * <p>
     * The Runner static method {@link #newRunner()} may also be used.
     *
     * @since 2.0.0
     */
    public Runner() {
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@code RunnerProgram}
     * @since 1.0.0
     */
    @Override
    public RunnerProgram run(ThrowingConsumer2<Scanner, BufferedWriter> program) {
        return new RunnerProgram(forProgram(program, UTF_8), UTF_8);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@code BufferedWriter}
     * @param program the program
     * @return {@code RunnerProgram}
     * @since 4.0.0
     */
    @Override
    public RunnerProgram run(Charset charset, ThrowingConsumer2<Scanner, BufferedWriter> program) {
        return new RunnerProgram(forProgram(program, charset), charset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@code RunnerPreProgram}
     * @since 1.0.0
     */
    @Override
    public RunnerPreProgram run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
        return new RunnerPreProgram(forProgram(program, UTF_8), UTF_8);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@code BufferedWriter}
     * @param program the program
     * @return {@code RunnerPreProgram}
     * @since 4.0.0
     */
    @Override
    public RunnerPreProgram run(Charset charset, ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
        return new RunnerPreProgram(forProgram(program, charset), charset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@code RunnerProgram}
     * @since 1.0.0
     */
    @Override
    public RunnerProgram runConsole(ThrowingConsumer2<InputStream, OutputStream> program) {
        return new RunnerProgram(program, UTF_8);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@code InputStream} and {@code OutputStream}
     * @param program the program
     * @return {@code RunnerProgram}
     * @since 2.0.0
     */
    @Override
    public RunnerProgram runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program) {
        return new RunnerProgram(program, charset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@code RunnerPreProgram}
     * @since 1.0.0
     */
    @Override
    public RunnerPreProgram runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program) {
        return new RunnerPreProgram(program, UTF_8);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@code InputStream} and {@code OutputStream}
     * @param program the program
     * @return {@code RunnerPreProgram}
     * @since 2.0.0
     */
    @Override
    public RunnerPreProgram runConsole(Charset charset, ThrowingConsumer3<String[], InputStream, OutputStream> program)
    {
        return new RunnerPreProgram(program, charset);
    }

    /**
     * Specify the input.
     *
     * @param input {@code String} array for input
     * @return {@code RunnerInput}
     * @since 1.0.0
     */
    @Override
    public RunnerInput input(String... input) {
        return new RunnerInput(() -> forInput(input), UTF_8);
    }

    /**
     * Specify the input.
     *
     * @param getInputStream {@code InputStream} function for input
     * @return {@code RunnerInput}
     * @since 1.0.0
     */
    @Override
    public RunnerInput input(ThrowingFunction0<InputStream> getInputStream) {
        return new RunnerInput(getInputStream, UTF_8);
    }

    /**
     * Specify the input.
     *
     * @param getInputStream {@code InputStream} function for input
     * @param charset        character set encoding for the program
     * @return {@code RunnerInput}
     * @since 1.0.0
     */
    @Override
    public RunnerInput input(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
        return new RunnerInput(getInputStream, charset);
    }

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading input
     * @param withClass    resource class for retrieving resource as {@code InputStream}
     * @return {@code RunnerInput}
     * @since 1.0.0
     */
    @Override
    public RunnerInput loadInput(String resourceName, Class<?> withClass) {
        return new RunnerInput(() -> withClass.getResourceAsStream(resourceName), UTF_8);
    }

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading input
     * @param withClass    resource class for retrieving resource as {@code InputStream}
     * @param charset      resource character set encoding
     * @return {@code RunnerInput}
     * @since 1.0.0
     */
    @Override
    public RunnerInput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        final ThrowingFunction0<InputStream> resource = () -> withClass.getResourceAsStream(resourceName);
        return new RunnerInput(resource, charset);
    }

    /**
     * Helper method for providing an {@code InputStream} for a array of String.
     *
     * @param input String array for input
     * @return {@code InputStream}
     * @throws IOException IO exception
     */
    private InputStream forInput(String[] input) throws IOException {
        if (input == null || input.length == 0) {
            return new ByteArrayInputStream(new byte[0]);
        }

        try (ByteArrayOutputStream outputStream = newOutputStream()) {
            try (BufferedWriter writer = newWriter(outputStream, UTF_8)) {
                writer.write(input[0]);
                for (int i = 1, size = input.length; i < size; i++) {
                    writer.newLine();
                    writer.write(input[i]);
                }
                writer.flush();
                return new ByteArrayInputStream(outputStream.toByteArray());
            }
        }
    }

    /**
     * Helper method for providing {@code Scanner} and {@code BufferedWriter} to program.
     *
     * @param program the program
     * @param charset the program character set encoding
     * @return standard {@code InputStream}, {@code OutputStream} consumer
     */
    private ThrowingConsumer2<InputStream, OutputStream> forProgram( //
        ThrowingConsumer2<Scanner, BufferedWriter> program, //
        Charset charset) //
    {
        return (inputStream, outputStream) -> {
            try (Scanner scanner = newScanner(inputStream, charset)) {
                try (BufferedWriter writer = newWriter(outputStream, charset)) {
                    program.accept(scanner, writer);
                }
            }
        };
    }

    /**
     * Helper method for providing {@code Scanner} and {@code BufferedWriter} to program.
     *
     * @param program the program
     * @param charset the program character set encoding
     * @return standard {@code InputStream}, {@code OutputStream} consumer
     */
    private ThrowingConsumer3<String[], InputStream, OutputStream> forProgram( //
        ThrowingConsumer3<String[], Scanner, BufferedWriter> program, //
        Charset charset) //
    {
        return (array, inputStream, outputStream) -> {
            try (Scanner scanner = newScanner(inputStream, charset)) {
                try (BufferedWriter writer = newWriter(outputStream, charset)) {
                    program.accept(array, scanner, writer);
                }
            }
        };
    }

    /**
     * Helper method for getting a new {@code Scanner}.
     *
     * @param inputStream program input {@code InputStream}
     * @param charset     program input character set encoding
     * @return {@code Scanner}
     */
    private Scanner newScanner(InputStream inputStream, Charset charset) {
        // Scanner(InputStream, String) for backward compatibility.
        requireNonNull(inputStream, "bolt.runner.load.input.input.stream.null");
        return new Scanner(inputStream, charset.name());
    }

    /**
     * Helper method for getting a new {@code ByteArrayOutputStream}.
     *
     * @return {@code ByteArrayOutputStream}
     */
    private ByteArrayOutputStream newOutputStream() {
        return new ByteArrayOutputStream(1024);
    }

    /**
     * Helper method for getting a {@code BufferedWriter} wrapping an {@code OutputStreamWriter}.
     *
     * @param outputStream {@code OutputStream}
     * @param charset      {@code OutputStream} character set encoding
     * @return {@code BufferedWriter}
     */
    private BufferedWriter newWriter(OutputStream outputStream, Charset charset) {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset);
        return new BufferedWriter(writer);
    }

    /**
     * Helper method for executing the program and collecting the output.
     *
     * @param program       the program
     * @param outputCharset the program output character set encoding
     * @param arguments     the program arguments
     * @param input         the program input
     * @param inputCharset  the program input character set encoding
     * @return {@code RunnerOutput}
     */
    private RunnerOutput executeRun( //
        ThrowingConsumer3<String[], Scanner, BufferedWriter> program, //
        Charset outputCharset, //
        String[] arguments, //
        ThrowingFunction0<InputStream> input, //
        Charset inputCharset) //
    {
        ByteArrayOutputStream outputStream = newOutputStream();
        Throwable throwable = null;

        try (BufferedWriter writer = newWriter(outputStream, outputCharset); //
            Scanner scanner = newScanner(input.accept(), inputCharset)) //
        {
            program.accept(arguments, scanner, writer);
        }
        catch (Throwable e) {
            throwable = e;
        }

        final byte[] data = outputStream.toByteArray();
        final String[] output = readArray(() -> new RunnerReader(data, outputCharset));
        return new RunnerOutput(output, fromThrowable(throwable));
    }

    /**
     * Helper method for executing the program and collecting the output.
     *
     * @param program       the program
     * @param outputCharset the program output character set encoding
     * @param arguments     the program arguments
     * @param input         the program input
     * @param inputCharset  the program input character set encoding
     * @return {@code RunnerOutput}
     */
    private RunnerOutput executeRunConsole( //
        ThrowingConsumer3<String[], InputStream, OutputStream> program, //
        Charset outputCharset, //
        String[] arguments, //
        ThrowingFunction0<InputStream> input, //
        Charset inputCharset) //
    {
        ByteArrayOutputStream outputStream = newOutputStream();
        Throwable throwable = null;

        try (InputStream inputStream = newInputStreamSupplier(input, inputCharset, outputCharset).accept()) {
            if (inputStream == null) {
                throw new NullPointerException("bolt.runner.load.input.input.stream.null");
            }
            program.accept(arguments, inputStream, outputStream);
        }
        catch (Throwable e) {
            throwable = e;
        }

        final byte[] data = outputStream.toByteArray();
        final String[] output = readArray(() -> new RunnerReader(data, outputCharset));
        return new RunnerOutput(output, fromThrowable(throwable));
    }

    /**
     * Helper method for setting up decoded {@code InputStream}.
     *
     * @param input   the input stream
     * @param charset the input stream character set encoding
     * @param decode  the desired character set encoding
     * @return {@code InputStream} with desired character set as encoding
     */
    private ThrowingFunction0<InputStream> newInputStreamSupplier( //
        ThrowingFunction0<InputStream> input, //
        Charset charset, //
        Charset decode) //
    {
        if (charset.name().equals(decode.name())) {
            return input;
        }

        return () -> new RunnerInputStream(input.accept(), charset, decode);
    }

    /**
     * Helper method to convert {@code Throwable} to {@code Exception}.
     *
     * @param throwable the throwable
     * @return {@code Exception}
     */
    private Exception fromThrowable(Throwable throwable) {
        if (throwable == null) { return null;}
        if (throwable instanceof Exception) { return (Exception) throwable;}
        return new Exception(throwable);
    }

    /**
     * Helper method to build a test result instance.
     *
     * @param expected   the expected program output
     * @param output     the actual program output
     * @param exception  {@code Nullable} exception
     * @param comparator {@code Nullable} comparator
     * @return {@code RunnerTestResult}
     */
    private RunnerTestResult buildTestResult( //
        String[] expected, //
        String[] output, //
        Exception exception, //
        Comparator<String> comparator) //
    {
        if (exception != null) {
            return new RunnerTestResult(output, expected, exception);
        }

        if (expected.length != output.length) {
            int none = -1;
            String message = "bolt.runner.asserter.output.length.mismatch";
            return new RunnerTestResult(output, expected, none, message);
        }
        final int size = output.length;
        if (comparator == null) {
            for (int index = 0; index < size; index++) {
                if (!Objects.equals(expected[index], output[index])) {
                    String message = "bolt.runner.asserter.output.data.mismatch";
                    return new RunnerTestResult(output, expected, index, message);
                }
            }
        }
        else {
            for (int index = 0; index < size; index++) {
                if (comparator.compare(expected[index], output[index]) != 0) {
                    String message = "bolt.runner.asserter.output.data.mismatch";
                    return new RunnerTestResult(output, expected, index, message);
                }
            }
        }

        return new RunnerTestResult(output, expected);
    }

    public class RunnerPreProgram implements RunnerInterfaces.RunnerPreProgram {

        private final ThrowingConsumer3<String[], InputStream, OutputStream> program;

        private final Charset outputCharset;

        RunnerPreProgram(ThrowingConsumer3<String[], InputStream, OutputStream> program, Charset outputCharset) {
            this.program = program;
            this.outputCharset = outputCharset;
        }

        /**
         * Specify program arguments.
         *
         * @param arguments program arguments
         * @return {@code RunnerProgram}
         * @since 1.0.0
         */
        @Override
        public RunnerProgram argument(String... arguments) {
            return new RunnerProgram(program, arguments, outputCharset);
        }
    }

    public class RunnerProgram implements RunnerInterfaces.RunnerProgram {

        private final ThrowingConsumer3<String[], InputStream, OutputStream> program;

        private final String[] arguments;

        private final Charset programCharset;

        RunnerProgram(
            ThrowingConsumer3<String[], InputStream, OutputStream> program, //
            String[] arguments, //
            Charset programCharset) //
        {
            this.program = program;
            this.arguments = arguments;
            this.programCharset = programCharset;
        }

        RunnerProgram(ThrowingConsumer2<InputStream, OutputStream> program, Charset programCharset) {
            this.program = (strings, inputStream, outputStream) -> { /**/
                program.accept(inputStream, outputStream);
            };
            this.arguments = null;
            this.programCharset = programCharset;
        }

        /**
         * Specify the input.
         *
         * @param input program input
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        @Override
        public RunnerOutput input(String... input) {
            final ThrowingFunction0<InputStream> getInput = () -> forInput(input);
            return executeRunConsole(program, programCharset, arguments, getInput, UTF_8);
        }

        /**
         * Specify the input.
         *
         * @param getInputStream {@code InputStream} function for input
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        @Override
        public RunnerOutput input(ThrowingFunction0<InputStream> getInputStream) {
            return create(getInputStream, UTF_8);
        }

        /**
         * Specify the input.
         *
         * @param getInputStream {@code InputStream} function for input
         * @param charset        the {@code InputStream} character set encoding
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        @Override
        public RunnerOutput input(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
            return create(getInputStream, charset);
        }

        /**
         * Specify the input.
         *
         * @param resourceName resource name for loading input
         * @param withClass    resource class for retrieving resource as {@code InputStream}
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        @Override
        public RunnerOutput loadInput(String resourceName, Class<?> withClass) {
            final ThrowingFunction0<InputStream> inputSupplier = () -> withClass.getResourceAsStream(resourceName);
            return create(inputSupplier, UTF_8);
        }

        /**
         * Specify the input.
         *
         * @param resourceName resource name for loading input
         * @param withClass    resource class for retrieving resource as {@code InputStream}
         * @param charset      resource character set encoding
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        @Override
        public RunnerOutput loadInput(String resourceName, Class<?> withClass, Charset charset) {
            final ThrowingFunction0<InputStream> inputSupplier = () -> withClass.getResourceAsStream(resourceName);
            return create(inputSupplier, charset);
        }

        /**
         * Creates a {@code RunnerOutput} for the program input {@code InputStream}.
         *
         * @param getInputStream function to return the {@code InputStream} for the program input
         * @param charset        the charset of the {@code InputStream}
         * @return a {@code RunnerOutput instance}
         */
        private RunnerOutput create(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
            return executeRunConsole(program, programCharset, arguments, getInputStream, charset);
        }
    }

    public class RunnerInput implements RunnerInterfaces.RunnerInput {

        private final ThrowingFunction0<InputStream> getInput;
        private final Charset inputCharset;

        RunnerInput(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
            this.getInput = getInputStream;
            this.inputCharset = charset;
        }

        /**
         * Specify the program arguments.
         *
         * @param arguments program arguments
         * @return {@code RunnerLoader}
         * @since 1.0.0
         */
        @Override
        public RunnerLoader argument(String... arguments) {
            return new RunnerLoader(inputCharset, getInput, arguments);
        }

        /**
         * Specify the program.
         *
         * @param program the program
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        public RunnerOutput run(ThrowingConsumer2<Scanner, BufferedWriter> program) {
            ThrowingConsumer3<String[], Scanner, BufferedWriter> internal = /**/
                (strings, scanner, writer) -> program.accept(scanner, writer);
            return executeRun(internal, UTF_8, null, getInput, inputCharset);
        }

        /**
         * Specify the program.
         *
         * @param program the program
         * @param charset the charset for {@code BufferedWriter}
         * @return {@code RunnerOutput}
         * @since 4.0.0
         */
        public RunnerOutput run(Charset charset, ThrowingConsumer2<Scanner, BufferedWriter> program) {
            ThrowingConsumer3<String[], InputStream, OutputStream> internal = /**/
                (strings, inputStream, outputStream) -> forProgram(program, charset).accept(inputStream, outputStream);
            return executeRunConsole(internal, charset, null, getInput, inputCharset);
        }

        /**
         * Specify the program.
         *
         * @param program the program
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        public RunnerOutput runConsole(ThrowingConsumer2<InputStream, OutputStream> program) {
            ThrowingConsumer3<String[], InputStream, OutputStream> internal = /**/
                (strings, inputStream, outputStream) -> program.accept(inputStream, outputStream);
            return executeRunConsole(internal, UTF_8, null, getInput, inputCharset);
        }

        /**
         * Specify the program.
         *
         * @param charset the charset for {@code InputStream} and {@code OutputStream}
         * @param program the program
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        @Override
        public RunnerOutput runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program) {
            ThrowingConsumer3<String[], InputStream, OutputStream> internal = /**/
                (strings, inputStream, outputStream) -> program.accept(inputStream, outputStream);
            return executeRunConsole(internal, charset, null, getInput, inputCharset);
        }
    }

    public class RunnerLoader implements RunnerInterfaces.RunnerLoader {

        /** Program input {@code InputStream} function */
        private final ThrowingFunction0<InputStream> getInput;

        private final Charset inputCharset;

        private final String[] arguments;

        RunnerLoader(Charset charset, ThrowingFunction0<InputStream> getInput, String[] arguments) {
            this.inputCharset = charset;
            this.getInput = getInput;
            this.arguments = arguments;
        }

        /**
         * Specify the program.
         *
         * @param program the program
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        @Override
        public RunnerOutput run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
            return executeRun(program, UTF_8, arguments, getInput, inputCharset);
        }

        /**
         * Specify the program.
         *
         * @param charset the charset for {@code BufferedWriter}
         * @param program the program
         * @return {@code RunnerOutput}
         * @since 4.0.0
         */
        @Override
        public RunnerOutput run(Charset charset, ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
            return executeRun(program, charset, arguments, getInput, inputCharset);
        }

        /**
         * Specify the program.
         *
         * @param program the program
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        @Override
        public RunnerOutput runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program) {
            return executeRunConsole(program, UTF_8, arguments, getInput, inputCharset);
        }

        /**
         * Specify the program.
         *
         * @param charset the charset for {@code InputStream} and {@code OutputStream}
         * @param program the program
         * @return {@code RunnerOutput}
         * @since 1.0.0
         */
        @Override
        public RunnerOutput runConsole(Charset charset, ThrowingConsumer3<String[], InputStream, OutputStream> program)
        {
            return executeRunConsole(program, charset, arguments, getInput, inputCharset);
        }
    }

    public class RunnerOutput extends RunnerPreTest implements RunnerInterfaces.RunnerOutput {

        RunnerOutput(String[] output, Exception exception) {
            super(output, exception, null);
        }

        /**
         * Specify the comparator.
         *
         * @param comparator {@code String} comparator
         * @return {@code RunnerPreTest}
         * @since 1.0.0
         */
        @Override
        public RunnerPreTest comparator(Comparator<String> comparator) {
            String[] output = ((RunnerOutputCommon) this).output;
            Exception exception = ((RunnerOutputCommon) this).exception;
            return new RunnerPreTest(output, exception, comparator);
        }
    }

    public class RunnerPreTest extends RunnerOutputCommon implements RunnerInterfaces.RunnerPreTest {

        RunnerPreTest(String[] found, Exception exception, Comparator<String> comparator) {
            super(found, exception, comparator);
        }
    }

    class RunnerOutputCommon implements RunnerInterfaces.RunnerOutputCommon {

        private final String[] output;

        private final Exception exception;

        private final Comparator<String> comparator;

        RunnerOutputCommon(String[] output, Exception exception, Comparator<String> comparator) {
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
         * @return {@code RunnerAsserter}
         * @since 1.0.0
         */
        public RunnerAsserter expected(String... expected) {
            final String[] expectation = expected != null && expected.length > 0 ? expected : new String[] {""};
            final RunnerTestResult testResult = buildTestResult(expectation, output, exception, comparator);
            return new RunnerAsserter(testResult);
        }

        /**
         * Specify the expected program output.
         *
         * @param getInputStream {@code InputStream} function for expected program output
         * @return {@code RunnerAsserter}
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
         * @return {@code RunnerAsserter}
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
         * @return {@code RunnerAsserter}
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
         * @return {@code RunnerAsserter}
         * @since 1.0.0
         */
        public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset) {
            return create(() -> withClass.getResourceAsStream(resourceName), charset);
        }

        /**
         * Creates a {@code RunnerAsserter} for the expected result {@code InputStream}.
         *
         * @param getInputStream function to return the {@code InputStream} for the expected result
         * @param charset        the charset of the {@code InputStream}
         * @return a {@code RunnerAsserter} instance
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
                throw new BoltAssertionException("bolt.runner.load.expectation.error", e);
            }
        }
    }

    public class RunnerAsserter implements RunnerInterfaces.RunnerAsserter {

        private final RunnerTestResult result;

        RunnerAsserter(RunnerTestResult result) {
            this.result = result;
        }

        /**
         * Asserts that the program run with expected output.
         * <p>
         * Throws {@code BoltAssertionException} for failure or error.
         *
         * @since 1.0.0
         */
        @Override
        public void assertSuccess() {
            if (result.isFailure()) {
                throw new BoltAssertionException(result.message);
            }

            if (result.isException()) {
                throw new BoltAssertionException("bolt.runner.asserter.error.found");
            }
        }

        /**
         * Asserts that the program run unsuccessfully.
         * <p>
         * Throws {@code BoltAssertionException} for success or error.
         *
         * @since 4.0.0
         */
        @Override
        public void assertFailure() {
            if (result.isSuccess()) {
                throw new BoltAssertionException("bolt.runner.asserter.success.found");
            }

            if (result.isException()) {
                throw new BoltAssertionException("bolt.runner.asserter.error.found");
            }
        }

        /**
         * Asserts that the program terminated with an error.
         * <p>
         * Throws {@code BoltAssertionException} for success or failure.
         *
         * @since 1.0.0
         */
        @Override
        public void assertException() {
            if (result.isSuccess()) {
                throw new BoltAssertionException("bolt.runner.asserter.success.found");
            }

            if (result.isFailure()) {
                throw new BoltAssertionException(result.message);
            }
        }

        /**
         * Asserts program behaviour with custom consumer.
         * <p>
         * The consumer should throw a throwable for undesired behaviour.
         *
         * @param custom custom consumer
         * @since 1.0.0
         */
        @Override
        public void assertCheck(ThrowingConsumer1<RunnerTestResult> custom) {
            try {
                custom.accept(result);
            }
            catch (Throwable throwable) {
                throw new BoltAssertionException(throwable.getMessage(), throwable.getCause());
            }
        }

        /**
         * Asserts program behaviour with offence triggered consumer.
         * <p>
         * The consumer should throw a throwable for undesired behaviour.
         *
         * @param custom custom consumer
         * @since 5.0.0
         */
        @Override
        public void onOffence(ThrowingConsumer1<RunnerTestResult> custom) {
            if (!result.isSuccess()) {
                try {
                    custom.accept(result);
                }
                catch (Throwable throwable) {
                    throw new BoltAssertionException(throwable.getMessage(), throwable.getCause());
                }
            }
        }

        /**
         * @return the program test result
         * @since 1.0.0
         */
        @Override
        public RunnerTestResult result() {
            return result;
        }
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class RunnerTestResult extends AbstractTestResult implements RunnerInterfaces.RunnerTestResult {

        private final String[] output;

        private final String[] expected;

        private final int offendingIndex;

        private final String message;

        private final Exception exception;

        RunnerTestResult(String[] output, String[] expected) {
            this.output = requireNonNull(output);
            this.expected = requireNonNull(expected);
            this.offendingIndex = -1;
            this.message = null;
            this.exception = null;
        }

        RunnerTestResult(String[] output, String[] expected, int offendingIndex, String message) {
            this.output = requireNonNull(output);
            this.expected = requireNonNull(expected);
            //noinspection ManualMinMaxCalculation
            this.offendingIndex = offendingIndex >= -1 ? offendingIndex : -1;
            this.message = message;
            this.exception = null;
        }

        RunnerTestResult(String[] output, String[] expected, Exception exception) {
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

    /**
     * A Bolt Runner Assertion Exception class for internal exceptions.
     */
    static class BoltAssertionException extends RuntimeException {

        BoltAssertionException(String message) {
            super(message, null);
        }

        BoltAssertionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
