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

    private final Charset inputCharset;

    private final InputStreamSupplier supplier;

    private final String[] arguments;

    /**
     * Create instance of this multi-interfaced class for handling of runners that accept input first.
     *
     * @since 6.0.0
     */
    BoltProvideInput() {
        inputCharset = UTF_8;
        supplier = () -> null;
        arguments = null;
    }

    /**
     * Private constructor for the multi-interfaced class.
     *
     * @param inputCharset character encoding of the {@link java.io.InputStream}
     * @param supplier     {@link java.io.InputStream} supplier
     * @since 6.0.0
     */
    private BoltProvideInput(Charset inputCharset, InputStreamSupplier supplier) {
        this.inputCharset = inputCharset;
        this.supplier = supplier;
        this.arguments = null;
    }

    /**
     * Private constructor for the multi-interfaced class.
     *
     * @param inputCharset character encoding of the {@link java.io.InputStream}
     * @param supplier     {@link java.io.InputStream} supplier
     * @param arguments    program arguments
     * @since 6.0.0
     */
    private BoltProvideInput(Charset inputCharset, InputStreamSupplier supplier, String[] arguments) {
        this.inputCharset = inputCharset;
        this.supplier = supplier;
        this.arguments = arguments;
    }

    @Override
    public RunnerProgramInput input(String... input) {
        return this.input(newInputStreamSupplier(input), UTF_8);
    }

    @Override
    public RunnerProgramInput input(InputStreamSupplier supplier) {
        return new BoltProvideInput(UTF_8, supplier);
    }

    @Override
    public RunnerProgramInput input(InputStreamSupplier supplier, Charset charset) {
        return new BoltProvideInput(charset, supplier);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
        return loadInput(resourceName, withClass, UTF_8);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        if (resourceName == null) {
            return new BoltProvideInput(charset, () -> {
                throw new RunnerException("bolt.runner.load.input.resource.name.null");
            });
        }
        if (withClass == null) {
            return new BoltProvideInput(charset, () -> {
                throw new RunnerException("bolt.runner.load.input.resource.class.null");
            });
        }
        if (withClass.getResource(resourceName) == null) {
            return new BoltProvideInput(charset, () -> {
                throw new RunnerException("bolt.runner.load.input.resource.not.found");
            });
        }

        return new BoltProvideInput(charset, () -> withClass.getResourceAsStream(resourceName));
    }

    @Override
    public RunnerLoader argument(String... arguments) {
        return new BoltProvideInput(inputCharset, supplier, emptyOnNull(arguments));
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
        BoltProgramExecutor executor = (arguments, inputCharset, supplier, outputCharset, outputStream) -> //
            executeStandardArgued(arguments, inputCharset, supplier, outputCharset, outputStream, program);

        return buildProgramOutput(arguments, inputCharset, supplier, charset, executor);
    }

    private BoltProgramOutput buildConsoleOutput(Charset charset, RunConsoleArgued program) {
        BoltProgramExecutor executor = (arguments, inputCharset, supplier, outputCharset, outputStream) -> //
            executeConsoleArgued(arguments, inputCharset, supplier, outputCharset, outputStream, program);

        return buildProgramOutput(arguments, inputCharset, supplier, charset, executor);
    }

}
