package app.zoftwhere.bolt;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerProvideProgram;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Bolt Provide Program class.
 *
 * @since 6.0.0
 */
class BoltProvideProgram implements RunnerProvideProgram, RunnerPreProgram, RunnerProgram, BoltProvide {

    private final String[] argumentArray;

    private final Charset outputCharset;

    private final BoltExecutor executor;

    private final RunnerException error;

    /**
     * Constructor for {@link Runner} program first interface implementation.
     *
     * @since 6.0.0
     */
    BoltProvideProgram() {
        argumentArray = null;
        executor = (arguments, inputCharset, inputStream, outputCharset, outputStream) -> null;
        outputCharset = UTF_8;
        error = null;
    }

    /**
     * Private constructor for the multi-interfaced class.
     *
     * @param arguments program argument array
     * @param charset   character encoding of program output
     * @param executor  program executor interface
     * @param error     execution error
     * @since 9.0.0
     */
    private BoltProvideProgram(String[] arguments, Charset charset, BoltExecutor executor, RunnerException error) {
        this.argumentArray = arguments;
        this.outputCharset = charset;
        this.executor = executor;
        this.error = error;
    }

    @Override
    public RunnerProgram run(RunStandard program) {
        BoltExecutor executor = buildStandardExecutor(proxyRunStandard(program));
        return new BoltProvideProgram(argumentArray, UTF_8, executor, error);
    }

    @Override
    public RunnerProgram run(Charset charset, RunStandard program) {
        BoltExecutor executor = buildStandardExecutor(proxyRunStandard(program));
        return new BoltProvideProgram(argumentArray, charset, executor, error);
    }

    @Override
    public RunnerProgram runConsole(RunConsole program) {
        BoltExecutor executor = buildConsoleExecutor(proxyRunConsole(program));
        return new BoltProvideProgram(argumentArray, UTF_8, executor, error);
    }

    @Override
    public RunnerProgram runConsole(Charset charset, RunConsole program) {
        BoltExecutor executor = buildConsoleExecutor(proxyRunConsole(program));
        return new BoltProvideProgram(argumentArray, charset, executor, error);
    }

    @Override
    public RunnerPreProgram run(RunStandardArgued program) {
        BoltExecutor executor = buildStandardExecutor(program);
        return new BoltProvideProgram(argumentArray, UTF_8, executor, error);
    }

    @Override
    public RunnerPreProgram run(Charset charset, RunStandardArgued program) {
        BoltExecutor executor = buildStandardExecutor(program);
        return new BoltProvideProgram(argumentArray, charset, executor, error);
    }

    @Override
    public RunnerPreProgram runConsole(RunConsoleArgued program) {
        BoltExecutor executor = buildConsoleExecutor(program);
        return new BoltProvideProgram(argumentArray, UTF_8, executor, error);
    }

    @Override
    public RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program) {
        BoltExecutor executor = buildConsoleExecutor(program);
        return new BoltProvideProgram(argumentArray, charset, executor, error);
    }

    @Override
    public RunnerProgram argument(String... arguments) {
        return new BoltProvideProgram(emptyOnNull(arguments), outputCharset, executor, error);
    }

    @Override
    public RunnerProgramOutput input(String... input) {
        for (String item : emptyOnNull(input)) {
            if (item == null) {
                RunnerException error = new RunnerException("bolt.runner.variable.array.input.has.null");
                return buildProgramOutput(argumentArray, UTF_8, () -> null, outputCharset, executor, error);
            }
        }

        InputStreamSupplier supplier = newInputStreamSupplier(input);
        return buildProgramOutput(argumentArray, UTF_8, supplier, outputCharset, executor, error);
    }

    @Override
    public RunnerProgramOutput input(InputStreamSupplier supplier) {
        return buildProgramOutput(argumentArray, UTF_8, supplier, outputCharset, executor, error);
    }

    @Override
    public RunnerProgramOutput input(InputStreamSupplier supplier, Charset charset) {
        return buildProgramOutput(argumentArray, charset, supplier, outputCharset, executor, error);
    }

    @Override
    public RunnerProgramOutput loadInput(String resourceName, Class<?> withClass) {
        return loadInput(resourceName, withClass, UTF_8);
    }

    @Override
    public RunnerProgramOutput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        if (resourceName == null) {
            RunnerException error = new RunnerException("bolt.runner.load.input.resource.name.null");
            return buildProgramOutput(argumentArray, charset, () -> null, outputCharset, executor, error);
        }
        if (withClass == null) {
            RunnerException error = new RunnerException("bolt.runner.load.input.resource.class.null");
            return buildProgramOutput(argumentArray, charset, () -> null, outputCharset, executor, error);
        }
        if (withClass.getResource(resourceName) == null) {
            RunnerException error = new RunnerException("bolt.runner.load.input.resource.not.found");
            return buildProgramOutput(argumentArray, charset, () -> null, outputCharset, executor, error);
        }

        InputStreamSupplier supplier = () -> withClass.getResourceAsStream(resourceName);
        return buildProgramOutput(argumentArray, charset, supplier, outputCharset, executor, error);
    }

}
