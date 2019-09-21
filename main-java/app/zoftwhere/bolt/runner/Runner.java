package app.zoftwhere.bolt.runner;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import app.zoftwhere.function.ThrowingConsumer1;
import app.zoftwhere.function.ThrowingConsumer2;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Runner implements RunnerInterfaces.IRunner<Runner.RunnerTestResult> {

    /**
     * User Runner static method {@link #newRunner()}<p>
     */
    private Runner() {
    }

    @Override
    public RunnerPreProgram run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
        return new RunnerPreProgram(forProgram(program, UTF_8));
    }

    @Override
    public RunnerPreProgram run(Charset charset, ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
        return new RunnerPreProgram(forProgram(program, charset));
    }

    @Override
    public RunnerPreProgram runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program) {
        return new RunnerPreProgram(program);
    }

    @Override
    public RunnerProgram run(ThrowingConsumer2<Scanner, BufferedWriter> program) {
        return new RunnerProgram(forProgram(program, UTF_8));
    }

    @Override
    public RunnerProgram run(Charset charset, ThrowingConsumer2<Scanner, BufferedWriter> program) {
        return new RunnerProgram(forProgram(program, charset));
    }

    public RunnerProgram runConsole(ThrowingConsumer2<InputStream, OutputStream> program) {
        return new RunnerProgram(program);
    }

    @Override
    public RunnerInput input(String... input) {
        return new RunnerInput(() -> forInput(input, UTF_8));
    }

    @Override
    public RunnerInput input(ThrowingFunction0<InputStream> getInputStream) {
        return new RunnerInput(getInputStream);
    }

    @Override
    public RunnerInput input(ThrowingFunction0<InputStream> getInputStream, Charset decode) {
        return new RunnerInput(getInputStreamCodec(getInputStream, decode, UTF_8));
    }

    @Override
    public RunnerInput input(ThrowingFunction0<InputStream> getInputStream, Charset decode, Charset encode) {
        return new RunnerInput(getInputStreamCodec(getInputStream, decode, encode));
    }

    @Override
    public RunnerInput loadInput(String resourceName, Class<?> withClass) {
        return new RunnerInput(() -> withClass.getResourceAsStream(resourceName));
    }

    @Override
    public RunnerInput loadInput(String resourceName, Class<?> withClass, Charset decode) {
        ThrowingFunction0<InputStream> input = () -> withClass.getResourceAsStream(resourceName);
        ThrowingFunction0<InputStream> codec = getInputStreamCodec(input, decode, UTF_8);
        return new RunnerInput(codec);
    }

    @Override
    public RunnerInput loadInput(String resourceName, Class<?> withClass, Charset decode, Charset encode) {
        ThrowingFunction0<InputStream> input = () -> withClass.getResourceAsStream(resourceName);
        ThrowingFunction0<InputStream> codec = getInputStreamCodec(input, decode, encode);
        return new RunnerInput(codec);
    }

    private InputStream forInput(String[] input, Charset charset) throws IOException {
        final ByteArrayOutputStream outputStream = getOutputStream();
        final BufferedWriter writer = getWriter(outputStream, charset);
        final int size = input.length;
        if (size > 0) {
            writer.write(input[0]);
            for (int i = 1; i < size; i++) {
                writer.newLine();
                writer.write(input[i]);
            }
        }
        writer.flush();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private ThrowingConsumer2<InputStream, OutputStream> forProgram(
        ThrowingConsumer2<Scanner, BufferedWriter> program,
        Charset charset)
    {
        return (inputStream, outputStream) ->
        {
            try (Scanner s = new Scanner(inputStream, charset.name())) {
                try (BufferedWriter w = getWriter(outputStream, UTF_8)) {
                    program.accept(s, w);
                }
            }
        };
    }

    private ThrowingConsumer3<String[], InputStream, OutputStream> forProgram(
        ThrowingConsumer3<String[], Scanner, BufferedWriter> program,
        Charset charset)
    {
        return (array, inputStream, outputStream) ->
        {
            try (Scanner s = new Scanner(inputStream, charset.name())) {
                try (BufferedWriter w = getWriter(outputStream, UTF_8)) {
                    program.accept(array, s, w);
                }
            }
        };
    }

    private ByteArrayOutputStream getOutputStream() {
        return new ByteArrayOutputStream(1024);
    }

    private BufferedWriter getWriter(OutputStream outputStream, Charset charset) {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset);
        return new BufferedWriter(writer);
    }

    private String[] splitOutput(ByteArrayOutputStream outputStream) {
        return new String(outputStream.toByteArray(), java.nio.charset.StandardCharsets.UTF_8).split(System.lineSeparator());
    }

    private RunnerOutput getProgramResult(
        ThrowingConsumer3<String[], InputStream, OutputStream> program,
        String[] arguments,
        ThrowingFunction0<InputStream> input)
    {
        final ByteArrayOutputStream outputStream = getOutputStream();
        try (InputStream inputStream = input.accept()) {
            if (inputStream == null) {
                throw new NullPointerException("bolt.runner.load.input.input.stream.null");
            }
            program.accept(arguments, inputStream, outputStream);
            outputStream.flush();
            final String[] found = splitOutput(outputStream);
            return new RunnerOutput(found, null);
        }
        catch (Throwable throwable) {
            final String[] found = splitOutput(outputStream);
            return new RunnerOutput(found, throwable);
        }
        finally {
            try {
                outputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ThrowingFunction0<InputStream> getInputStreamCodec(
        ThrowingFunction0<InputStream> input,
        Charset decode,
        Charset encode)
    {
        return () -> {
            try (Scanner scanner = new Scanner(input.accept(), decode.name())) {
                final List<String> list = new ArrayList<>();
                while (scanner.hasNext()) {
                    list.add(scanner.nextLine());
                }
                final int size = list.size();
                String[] array = new String[size];
                array = list.toArray(array);
                return forInput(array, encode);
            }
        };
    }

    private Exception fromThrowable(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        if (throwable instanceof Exception) {
            return (Exception) throwable;
        }
        return new Exception(throwable);
    }

    /**
     * This is an immutable runner (so get one, and run all the tests you need).
     *
     * @return an new immutable runner instance
     */
    public static Runner newRunner() {
        return new Runner() { };
    }

    public class RunnerPreProgram implements RunnerInterfaces.RunnerPreProgram<RunnerTestResult> {

        final ThrowingConsumer3<String[], InputStream, OutputStream> program;

        RunnerPreProgram(ThrowingConsumer3<String[], InputStream, OutputStream> program) {
            this.program = program;
        }

        @Override
        public RunnerProgram argument(String... arguments) {
            return new RunnerProgram(program, arguments);
        }
    }

    public class RunnerProgram implements RunnerInterfaces.RunnerProgram<RunnerTestResult> {

        final ThrowingConsumer3<String[], InputStream, OutputStream> program;

        final String[] arguments;

        RunnerProgram(ThrowingConsumer3<String[], InputStream, OutputStream> program, String[] arguments) {
            this.program = program;
            this.arguments = arguments;
        }

        RunnerProgram(ThrowingConsumer2<InputStream, OutputStream> program) {
            this.program = (strings, inputStream, outputStream) -> { //
                program.accept(inputStream, outputStream);
            };
            this.arguments = null;
        }

        @Override
        public RunnerOutput input(String... input) {
            final ThrowingFunction0<InputStream> getInput = () -> forInput(input, UTF_8);
            return getProgramResult(program, arguments, getInput);
        }

        @Override
        public RunnerOutput input(ThrowingFunction0<InputStream> getInputStream) {
            return create(getInputStream);
        }

        public RunnerOutput input(ThrowingFunction0<InputStream> getInputStream, Charset decode) {
            return create(getInputStream, decode, UTF_8);
        }

        public RunnerOutput input(ThrowingFunction0<InputStream> getInputStream, Charset decode, Charset encode) {
            return create(getInputStream, decode, encode);
        }

        @Override
        public RunnerOutput loadInput(String resourceName, Class<?> withClass) {
            ThrowingFunction0<InputStream> inputSupplier = () -> withClass.getResourceAsStream(resourceName);
            return create(inputSupplier);
        }

        public RunnerOutput loadInput(String resourceName, Class<?> withClass, Charset decode) {
            ThrowingFunction0<InputStream> inputSupplier = () -> withClass.getResourceAsStream(resourceName);
            return create(inputSupplier, decode, UTF_8);
        }

        public RunnerOutput loadInput(String resourceName, Class<?> withClass, Charset decode, Charset encode) {
            ThrowingFunction0<InputStream> inputSupplier = () -> withClass.getResourceAsStream(resourceName);
            return create(inputSupplier, decode, encode);
        }

        private RunnerOutput create(ThrowingFunction0<InputStream> getInputStream) {
            return getProgramResult(program, arguments, getInputStream);
        }

        private RunnerOutput create(ThrowingFunction0<InputStream> getInputStream,
            Charset decode,
            Charset encode)
        {
            ThrowingFunction0<InputStream> codec = getInputStreamCodec(getInputStream, decode, encode);
            return getProgramResult(program, arguments, codec);
        }
    }

    public class RunnerInput implements RunnerInterfaces.RunnerInput<RunnerTestResult> {

        private final ThrowingFunction0<InputStream> getInput;

        RunnerInput(ThrowingFunction0<InputStream> getInputStream) {
            this.getInput = getInputStream;
        }

        @Override
        public RunnerLoader argument(String... arguments) {
            return new RunnerLoader(getInput, arguments);
        }

        public RunnerOutput runConsole(ThrowingConsumer2<InputStream, OutputStream> program) {
            ThrowingConsumer3<String[], InputStream, OutputStream> internal =
                (strings, inputStream, outputStream) -> { //
                    program.accept(inputStream, outputStream);
                };
            return getProgramResult(internal, null, getInput);
        }

        public RunnerOutput run(ThrowingConsumer2<Scanner, BufferedWriter> program) {
            ThrowingConsumer3<String[], InputStream, OutputStream> internal =
                (strings, inputStream, outputStream) -> { //
                    forProgram(program, UTF_8).accept(inputStream, outputStream);
                };
            return getProgramResult(internal, null, getInput);
        }

        public RunnerOutput run(Charset charset, ThrowingConsumer2<Scanner, BufferedWriter> program) {
            ThrowingConsumer3<String[], InputStream, OutputStream> internal =
                (strings, inputStream, outputStream) -> { //
                    forProgram(program, charset).accept(inputStream, outputStream);
                };
            return getProgramResult(internal, null, getInput);
        }
    }

    public class RunnerLoader implements RunnerInterfaces.RunnerLoader<RunnerTestResult> {

        private final ThrowingFunction0<InputStream> getInput;

        private final String[] arguments;

        RunnerLoader(ThrowingFunction0<InputStream> getInput, String[] arguments) {
            this.getInput = getInput;
            this.arguments = arguments;
        }

        @Override
        public RunnerOutput run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
            return getProgramResult(forProgram(program, UTF_8), arguments, getInput);
        }

        @Override
        public RunnerOutput run(Charset charset, ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
            return getProgramResult(forProgram(program, charset), arguments, getInput);
        }

        @Override
        public RunnerOutput runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program) {
            return getProgramResult(program, arguments, getInput);
        }
    }

    public class RunnerOutput extends RunnerOutputCommon implements RunnerInterfaces.RunnerOutput<RunnerTestResult> {

        private final String[] found;

        private final Exception exception;

        RunnerOutput(String[] found, Throwable throwable) {
            super(found, throwable, null);
            this.found = found;
            this.exception = fromThrowable(throwable);
        }

        @Override
        public RunnerPreTest comparator(Comparator<String> comparator) {
            return new RunnerPreTest(output(), exception(), comparator);
        }

        @Override
        public String[] output() {
            return found;
        }

        @Override
        public Exception exception() {
            return exception;
        }
    }

    public class RunnerPreTest extends RunnerOutputCommon implements RunnerInterfaces.RunnerPreTest<RunnerTestResult> {

        private final String[] found;

        private final Exception exception;

        RunnerPreTest(String[] found, Exception exception, Comparator<String> comparator) {
            super(found, exception, comparator);
            this.found = found;
            this.exception = exception;
        }

        @Override
        public String[] output() {
            return found;
        }

        @Override
        public Exception exception() {
            return exception;
        }
    }

    private abstract class RunnerOutputCommon {

        private final String[] found;

        private final Exception exception;

        private final Comparator<String> comparator;

        RunnerOutputCommon(String[] found, Throwable throwable, Comparator<String> comparator) {
            this.found = found;
            this.exception = fromThrowable(throwable);
            this.comparator = comparator;
        }

        public RunnerAsserter expected(String... expected) {
            String[] array = expected != null && expected.length > 0 ? expected : new String[] {""};
            RunnerTestResult testResult = buildTestResult(array, found, exception, comparator);
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

        private RunnerAsserter create(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
            try (InputStream inputStream = getInputStream.accept()) {
                if (inputStream == null) {
                    throw new NullPointerException("bolt.load.expectation.input.stream.null");
                }
                try (Scanner scanner = new Scanner(inputStream, charset.name())) {
                    List<String> list = new ArrayList<>();
                    while (scanner.hasNextLine()) {
                        list.add(scanner.nextLine());
                    }
                    final int size = list.size();
                    final String[] array = list.toArray(new String[size]);
                    return expected(array);
                }
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
        public void assertResult() {
            if (result.isSuccess()) {
                return;
            }

            if (result.message().isPresent()) {
                if (result.exception().isPresent()) {
                    throw new BoltAssertionException(result.message().get(), result.exception().get());
                }
                throw new BoltAssertionException(result.exception().get());
            }
            else if (result.message().isPresent()) {
                throw new BoltAssertionException(result.message().get());
            }
            else {
                throw new BoltAssertionException();
            }
        }

        @Override
        public void assertException() {
            if (result.exception().isPresent()) {
                return;
            }
            throw new BoltAssertionException("bolt.runner.assertion.expected.exception");
        }

        public void assertCheck(ThrowingConsumer1<RunnerTestResult> custom) {
            try {
                custom.accept(result);
            }
            catch (Throwable throwable) {
                throw new BoltAssertionException(throwable.getMessage(), throwable.getCause());
            }
        }

        public RunnerTestResult result() {
            return result;
        }
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class RunnerTestResult implements RunnerInterfaces.RunnerTestResult, RunnerInterfaces.TestResult {

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

    private RunnerTestResult buildTestResult(String[] expected,
        String[] output,
        Throwable throwable,
        Comparator<String> comparator)
    {
        String message = throwable == null ? testMessage(expected, output, comparator) : null;
        return new RunnerTestResult(output, expected, fromThrowable(throwable), message);
    }

    private String testMessage(String[] expected, String[] output, Comparator<String> comparator) {
        if (expected == null) {
            return "expected == null";
        }

        if (output == null) {
            return "output == null";
        }

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

    static class BoltAssertionException extends RuntimeException {

        BoltAssertionException() {
            super();
        }

        BoltAssertionException(String message) {
            super(message);
        }

        BoltAssertionException(String message, Throwable cause) {
            super(message, cause);
        }

        BoltAssertionException(Throwable cause) {
            super(cause);
        }
    }

}
