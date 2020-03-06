package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import app.zoftwhere.function.ThrowingConsumer1;
import app.zoftwhere.function.ThrowingConsumer2;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

import static app.zoftwhere.bolt.RunnerReader.readArray;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Bolt Assertion Runner.
 */
public class Runner implements RunnerInterfaces.IRunner {

    /**
     * Constructor for a reusable, immutable instance of the runner (more than one test can be run with it).
     * <p>
     * The Runner static method {@link #newRunner()} may also be used.
     */
    public Runner() {
    }

    @Override
    public RunnerProgram run(ThrowingConsumer2<Scanner, BufferedWriter> program) {
        return new RunnerProgram(forProgram(program), UTF_8);
    }

    @Override
    public RunnerPreProgram run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
        return new RunnerPreProgram(forProgram(program), UTF_8);
    }

    @Override
    public RunnerProgram runConsole(ThrowingConsumer2<InputStream, OutputStream> program) {
        return new RunnerProgram(program, UTF_8);
    }

    @Override
    public RunnerPreProgram runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program) {
        return new RunnerPreProgram(program, UTF_8);
    }

    @Override
    public RunnerProgram runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program) {
        return new RunnerProgram(program, charset);
    }

    @Override
    public RunnerPreProgram runConsole(Charset charset, ThrowingConsumer3<String[], InputStream, OutputStream> program)
    {
        return new RunnerPreProgram(program, charset);
    }

    @Override
    public RunnerInput input(String... input) {
        return new RunnerInput(() -> forInput(input));
    }

    @Override
    public RunnerInput input(ThrowingFunction0<InputStream> getInputStream) {
        return new RunnerInput(getInputStream);
    }

    @Override
    public RunnerInput input(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
        return new RunnerInput(getUTF8Inputted(getInputStream, charset));
    }

    @Override
    public RunnerInput loadInput(String resourceName, Class<?> withClass) {
        return new RunnerInput(() -> withClass.getResourceAsStream(resourceName));
    }

    @Override
    public RunnerInput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        final ThrowingFunction0<InputStream> resource = () -> withClass.getResourceAsStream(resourceName);
        final ThrowingFunction0<InputStream> getInputStream = getUTF8Inputted(resource, charset);
        return new RunnerInput(getInputStream);
    }

    private InputStream forInput(String[] input) throws IOException {
        if (input == null || input.length == 0) {
            return new ByteArrayInputStream(new byte[0]);
        }

        try (ByteArrayOutputStream outputStream = getOutputStream()) {
            try (BufferedWriter writer = getWriter(outputStream)) {
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

    private ThrowingConsumer2<InputStream, OutputStream> forProgram( //
        ThrowingConsumer2<Scanner, BufferedWriter> program)
    {
        return (inputStream, outputStream) -> {
            try (Scanner scanner = new Scanner(inputStream, UTF_8.name())) {
                try (BufferedWriter writer = getWriter(outputStream)) {
                    program.accept(scanner, writer);
                }
            }
        };
    }

    private ThrowingConsumer3<String[], InputStream, OutputStream> forProgram( //
        ThrowingConsumer3<String[], Scanner, BufferedWriter> program)
    {
        return (array, inputStream, outputStream) -> {
            try (Scanner s = new Scanner(inputStream, UTF_8.name())) {
                try (BufferedWriter w = getWriter(outputStream)) {
                    program.accept(array, s, w);
                }
            }
        };
    }

    private ByteArrayOutputStream getOutputStream() {
        return new ByteArrayOutputStream(1024);
    }

    private BufferedWriter getWriter(OutputStream outputStream) {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, UTF_8);
        return new BufferedWriter(writer);
    }

    private RunnerOutput getProgramResult( //
        ThrowingConsumer3<String[], InputStream, OutputStream> program, //
        Charset charset, //
        String[] arguments, //
        ThrowingFunction0<InputStream> input) //
    {
        ByteArrayOutputStream outputStream = null;
        Throwable throwable = null;

        try (ByteArrayOutputStream byteArrayOutputStream = getOutputStream(); //
            InputStream inputStream = input.accept()) //
        {
            outputStream = byteArrayOutputStream;
            if (inputStream == null) {
                throw new NullPointerException("bolt.runner.load.input.input.stream.null");
            }
            try {
                program.accept(arguments, inputStream, byteArrayOutputStream);
            }
            catch (Throwable e) {
                throwable = e;
            }

            if (throwable != null) {
                throw throwable;
            }
        }
        catch (Throwable e) {
            throwable = e;
        }

        final byte[] data = outputStream != null ? outputStream.toByteArray() : null;
        final String[] output = data != null ? readArray(() -> new RunnerReader(data, charset)) : null;
        return new RunnerOutput(output, throwable);
    }

    private ThrowingFunction0<InputStream> getUTF8Inputted( //
        ThrowingFunction0<InputStream> input, Charset decode)
    {
        if (decode.name().equals(UTF_8.name())) {
            return input;
        }

        return () -> new InputStream() {
            final InputStreamReader decoder = new InputStreamReader(input.accept(), decode);

            @Override
            public int read() throws IOException {
                return decoder.read();
            }

            @Override
            public void close() throws IOException {
                decoder.close();
            }
        };
    }

    private Exception fromThrowable(Throwable throwable) {
        if (throwable == null) { return null;}
        if (throwable instanceof Exception) { return (Exception) throwable;}
        return new Exception(throwable);
    }

    private RunnerTestResult buildTestResult(String[] expected, String[] output, Throwable throwable,
        Comparator<String> comparator)
    {
        final String message = throwable == null ? testMessage(expected, output, comparator) : null;
        return new RunnerTestResult(output, expected, fromThrowable(throwable), message);
    }

    private String testMessage(String[] expected, String[] output, Comparator<String> comparator) {
        if (expected.length != output.length) {
            return String.format("Lengths to not match. Expected %d, found %d.", expected.length, output.length);
        }
        final int size = output.length;
        if (comparator == null) {
            for (int i = 0; i < size; i++) {
                if (!Objects.equals(expected[i], output[i])) {
                    return String.format("Line %d: Expected \"%s\". Found \"%s\"", i + 1, expected[i], output[i]);
                }
            }
            return null;
        }
        for (int i = 0; i < size; i++) {
            if (comparator.compare(expected[i], output[i]) != 0) {
                return String.format("Line %d: Expected \"%s\". Found \"%s\"", i + 1, expected[i], output[i]);
            }
        }
        return null;
    }

    /**
     * This is an immutable runner (so get one, and run all the tests you need).
     *
     * @return an immutable runner instance
     */
    public static Runner newRunner() {
        return new Runner();
    }

    public class RunnerPreProgram implements RunnerInterfaces.RunnerPreProgram {

        private final ThrowingConsumer3<String[], InputStream, OutputStream> program;

        private final Charset outputCharset;

        RunnerPreProgram(ThrowingConsumer3<String[], InputStream, OutputStream> program, Charset outputCharset) {
            this.program = program;
            this.outputCharset = outputCharset;
        }

        @Override
        public RunnerProgram argument(String... arguments) {
            return new RunnerProgram(program, arguments, outputCharset);
        }
    }

    public class RunnerProgram implements RunnerInterfaces.RunnerProgram {

        final ThrowingConsumer3<String[], InputStream, OutputStream> program;

        final String[] arguments;

        private final Charset outputCharset;

        RunnerProgram(ThrowingConsumer3<String[], InputStream, OutputStream> program, String[] arguments,
            Charset outputCharset)
        {
            this.program = program;
            this.arguments = arguments;
            this.outputCharset = outputCharset;
        }

        RunnerProgram(ThrowingConsumer2<InputStream, OutputStream> program, Charset outputCharset) {
            this.program = (strings, inputStream, outputStream) -> { /**/
                program.accept(inputStream, outputStream);
            };
            this.arguments = null;
            this.outputCharset = outputCharset;
        }

        @Override
        public RunnerOutput input(String... input) {
            final ThrowingFunction0<InputStream> getInput = () -> forInput(input);
            return getProgramResult(program, UTF_8, arguments, getInput);
        }

        @Override
        public RunnerOutput input(ThrowingFunction0<InputStream> getInputStream) {
            return create(getInputStream, UTF_8);
        }

        @Override
        public RunnerOutput input(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
            return create(getInputStream, charset);
        }

        @Override
        public RunnerOutput loadInput(String resourceName, Class<?> withClass) {
            final ThrowingFunction0<InputStream> inputSupplier = () -> withClass.getResourceAsStream(resourceName);
            return create(inputSupplier, UTF_8);
        }

        @Override
        public RunnerOutput loadInput(String resourceName, Class<?> withClass, Charset charset) {
            final ThrowingFunction0<InputStream> inputSupplier = () -> withClass.getResourceAsStream(resourceName);
            return create(inputSupplier, charset);
        }

        /**
         * Creates a {@code RunnerOutput} for the program input {@code InputStream}.
         *
         * @param getInputStream function to return the {@code InputStream} for the program input
         * @param charset         the charset of the {@code InputStream}
         * @return a {@code RunnerOutput instance}
         */
        private RunnerOutput create(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
            final ThrowingFunction0<InputStream> getInput = getUTF8Inputted(getInputStream, charset);
            return getProgramResult(program, outputCharset, arguments, getInput);
        }
    }

    public class RunnerInput implements RunnerInterfaces.RunnerInput {

        private final ThrowingFunction0<InputStream> getInput;

        RunnerInput(ThrowingFunction0<InputStream> getInputStream) {
            this.getInput = getInputStream;
        }

        @Override
        public RunnerLoader argument(String... arguments) {
            return new RunnerLoader(getInput, arguments);
        }

        public RunnerOutput runConsole(ThrowingConsumer2<InputStream, OutputStream> program) {
            ThrowingConsumer3<String[], InputStream, OutputStream> internal = /**/
                (strings, inputStream, outputStream) -> program.accept(inputStream, outputStream);
            return getProgramResult(internal, UTF_8, null, getInput);
        }

        public RunnerOutput run(ThrowingConsumer2<Scanner, BufferedWriter> program) {
            ThrowingConsumer3<String[], InputStream, OutputStream> internal = /**/
                (strings, inputStream, outputStream) -> forProgram(program).accept(inputStream, outputStream);
            return getProgramResult(internal, UTF_8, null, getInput);
        }

        @Override
        public RunnerOutput runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program) {
            ThrowingConsumer3<String[], InputStream, OutputStream> internal = /**/
                (strings, inputStream, outputStream) -> program.accept(inputStream, outputStream);
            return getProgramResult(internal, charset, null, getInput);
        }
    }

    public class RunnerLoader implements RunnerInterfaces.RunnerLoader {

        /** Program input {@code InputStream} function */
        private final ThrowingFunction0<InputStream> getInput;

        private final String[] arguments;

        RunnerLoader(ThrowingFunction0<InputStream> getInput, String[] arguments) {
            this.getInput = getInput;
            this.arguments = arguments;
        }

        @Override
        public RunnerOutput run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
            return getProgramResult(forProgram(program), UTF_8, arguments, getInput);
        }

        @Override
        public RunnerOutput runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> console) {
            return getProgramResult(console, UTF_8, arguments, getInput);
        }

        @Override
        public RunnerOutput runConsole(Charset charset, ThrowingConsumer3<String[], InputStream, OutputStream> console)
        {
            return getProgramResult(console, charset, arguments, getInput);
        }
    }

    public class RunnerOutput extends RunnerOutputCommon implements RunnerInterfaces.RunnerOutput {

        RunnerOutput(String[] output, Throwable throwable) {
            super(output, throwable, null);
        }

        @Override
        public RunnerPreTest comparator(Comparator<String> comparator) {
            return new RunnerPreTest(output(), exception(), comparator);
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

        RunnerOutputCommon(String[] output, Throwable throwable, Comparator<String> comparator) {
            this.output = output;
            this.exception = fromThrowable(throwable);
            this.comparator = comparator;
        }

        @Override
        public String[] output() {
            return output;
        }

        @Override
        public Exception exception() {
            return exception;
        }

        public RunnerAsserter expected(String... expected) {
            final String[] array = expected != null && expected.length > 0 ? expected : new String[] {""};
            final RunnerTestResult testResult = buildTestResult(array, output, exception, comparator);
            return new RunnerAsserter(testResult);
        }

        public RunnerAsserter expected(ThrowingFunction0<InputStream> getInputStream) {
            return create(getInputStream, UTF_8);
        }

        public RunnerAsserter expected(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
            return create(getInputStream, charset);
        }

        public RunnerAsserter loadExpectation(String resourceName, Class<?> withClass) {
            return create(() -> withClass.getResourceAsStream(resourceName), UTF_8);
        }

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
                    throw new NullPointerException("bolt.load.expectation.input.stream.null");
                }
                final String[] expected = readArray(() -> new RunnerReader(inputStream, charset));
                return expected(expected);
            }
            catch (Throwable e) {
                throw new BoltAssertionException("bolt.load.expectation.error", e);
            }
        }
    }

    public class RunnerAsserter implements RunnerInterfaces.RunnerAsserter<RunnerTestResult> {

        private final RunnerTestResult result;

        RunnerAsserter(RunnerTestResult result) {
            this.result = result;
        }

        @Override
        public void assertSuccess() {
            if (result.isSuccess()) {
                return;
            }

            final String message = result.message().orElse(null);
            final Exception exception = result.exception().orElse(null);
            throw new BoltAssertionException(message, exception);
        }

        @Override
        public void assertFail() {
            if (result.exception().isPresent()) {
                throw new BoltAssertionException(result.exception().get());
            }

            if (result.message().isPresent() && result.isFail()) {
                return;
            }

            throw new BoltAssertionException("bolt.runner.assertion.expected.failure", null);
        }

        @Override
        public void assertException() {
            if (result.exception().isPresent()) {
                return;
            }
            throw new BoltAssertionException("bolt.runner.assertion.expected.exception", null);
        }

        @Override
        public void assertCheck(ThrowingConsumer1<RunnerTestResult> custom) {
            try {
                custom.accept(result);
            }
            catch (Throwable throwable) {
                throw new BoltAssertionException(throwable.getMessage(), throwable.getCause());
            }
        }

        @Override
        public RunnerTestResult result() {
            return result;
        }
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class RunnerTestResult extends RunnerInterfaces.AbstractTestResult {

        private final boolean success;

        private final String[] output;

        private final String[] expected;

        private final Exception exception;

        private final String message;

        RunnerTestResult(String[] output, String[] expected, Exception exception, String message) {
            this.output = output;
            this.expected = expected;
            this.exception = exception;
            this.message = message;
            this.success = exception == null && message == null;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        @Override
        public boolean isFail() {
            return !success;
        }

        @Override
        public String[] output() {
            return output;
        }

        @Override
        public String[] expected() {
            return expected;
        }

        @Override
        public Optional<Exception> exception() {
            return Optional.ofNullable(exception);
        }

        @Override
        public Optional<String> message() {
            return Optional.ofNullable(message);
        }
    }

    static class BoltAssertionException extends RuntimeException {

        BoltAssertionException(Throwable cause) {
            super(cause);
        }

        BoltAssertionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
