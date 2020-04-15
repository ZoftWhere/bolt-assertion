package app.zoftwhere.bolt;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerProvideInput;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Bolt Provide Input class.
 *
 * @since 6.0.0
 */
class BoltProvideInput implements RunnerProvideInput, RunnerProgramInput, RunnerLoader, BoltProvide {

    private final String[] arguments;

    private final Charset charset;

    private final InputStreamSupplier supplier;

    private final RunnerException error;

    /**
     * Create instance of this multi-interfaced class for handling of runners that accept input first.
     *
     * @since 6.0.0
     */
    BoltProvideInput() {
        charset = UTF_8;
        supplier = () -> null;
        arguments = null;
        error = null;
    }

    /**
     * Private constructor for the multi-interfaced class.
     *
     * @param arguments program arguments
     * @param charset   character encoding of {@link java.io.InputStream}
     * @param supplier  {@link java.io.InputStream} supplier for program input
     * @param error     execution error
     * @since 9.0.0
     */
    private BoltProvideInput(String[] arguments, Charset charset, InputStreamSupplier supplier, RunnerException error) {
        this.arguments = arguments;
        this.charset = charset;
        this.supplier = supplier;
        this.error = error;
    }

    @Override
    public RunnerProgramInput input(String... input) {
        for (String item : emptyOnNull(input)) {
            if (item == null) {
                RunnerException error = new RunnerException("bolt.runner.variable.array.input.has.null");
                return new BoltProvideInput(arguments, charset, supplier, error);
            }
        }

        InputStreamSupplier supplier = newInputStreamSupplier(input);
        return new BoltProvideInput(arguments, UTF_8, supplier, error);
    }

    @Override
    public RunnerProgramInput input(InputStreamSupplier supplier) {
        return new BoltProvideInput(arguments, charset, supplier, null);
    }

    @Override
    public RunnerProgramInput input(InputStreamSupplier supplier, Charset charset) {
        return new BoltProvideInput(arguments, charset, supplier, null);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
        return loadInput(resourceName, withClass, UTF_8);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        if (resourceName == null) {
            RunnerException error = new RunnerException("bolt.runner.load.input.resource.name.null");
            return new BoltProvideInput(arguments, charset, this.supplier, error);
        }
        if (withClass == null) {
            RunnerException error = new RunnerException("bolt.runner.load.input.resource.class.null");
            return new BoltProvideInput(arguments, charset, this.supplier, error);
        }
        if (withClass.getResource(resourceName) == null) {
            RunnerException error = new RunnerException("bolt.runner.load.input.resource.not.found");
            return new BoltProvideInput(arguments, charset, this.supplier, error);
        }

        InputStreamSupplier supplier = () -> withClass.getResourceAsStream(resourceName);
        return new BoltProvideInput(arguments, charset, supplier, error);
    }

    @Override
    public RunnerLoader argument(String... arguments) {
        return new BoltProvideInput(emptyOnNull(arguments), charset, supplier, error);
    }

    @Override
    public RunnerProgramOutput run(RunStandard program) {
        return buildStandardOutput(UTF_8, ((arguments, scanner, out) -> //
            program.call(scanner, out)));
    }

    @Override
    public RunnerProgramOutput run(Charset charset, RunStandard program) {
        return buildStandardOutput(charset, ((arguments, scanner, out) -> //
            program.call(scanner, out)));
    }

    @Override
    public RunnerProgramOutput runConsole(RunConsole program) {
        return buildConsoleOutput(UTF_8, ((arguments, inputStream, outputStream) -> //
            program.call(inputStream, outputStream)));
    }

    @Override
    public RunnerProgramOutput runConsole(Charset charset, RunConsole program) {
        return buildConsoleOutput(charset, ((arguments, inputStream, outputStream) -> //
            program.call(inputStream, outputStream)));
    }

    @Override
    public RunnerProgramOutput run(RunStandardArgued program) {
        return buildStandardOutput(UTF_8, program);
    }

    @Override
    public RunnerProgramOutput run(Charset charset, RunStandardArgued program) {
        return buildStandardOutput(charset, program);
    }

    @Override
    public RunnerProgramOutput runConsole(RunConsoleArgued program) {
        return buildConsoleOutput(UTF_8, program);
    }

    @Override
    public RunnerProgramOutput runConsole(Charset charset, RunConsoleArgued program) {
        return buildConsoleOutput(charset, program);
    }

    private BoltProgramOutput buildStandardOutput(Charset charset, RunStandardArgued program) {
        BoltExecutor call = (arguments, inCharset, inputStream, outCharset, outputStream) ->
            callStandardArgued(arguments, inCharset, inputStream, outCharset, outputStream, program);

        return buildProgramOutput(arguments, this.charset, supplier, charset, call, error);
    }

    private BoltProgramOutput buildConsoleOutput(Charset charset, RunConsoleArgued program) {
        BoltExecutor call = (arguments, inCharset, inputStream, outCharset, outputStream) ->
            callConsoleArgued(arguments, inCharset, inputStream, outCharset, outputStream, program);

        return buildProgramOutput(arguments, this.charset, supplier, charset, call, error);
    }

}
