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

class BoltProvideProgram implements RunnerProvideProgram, RunnerPreProgram, RunnerProgram, BoltProvide {

    private final String[] argumentArray;

    private final BoltProgramExecutor executor;

    private final Charset outputCharset;

    BoltProvideProgram() {
        argumentArray = null;
        executor = (arguments, inputCharset, streamSupplier, outputCharset, outputStream) -> null;
        outputCharset = UTF_8;
    }

    private BoltProvideProgram(BoltProgramExecutor executor, Charset outputCharset) {
        this.argumentArray = null;
        this.executor = executor;
        this.outputCharset = outputCharset;
    }

    private BoltProvideProgram(String[] argumentArray, BoltProgramExecutor executor, Charset outputCharset) {
        this.outputCharset = outputCharset;
        this.executor = executor;
        this.argumentArray = argumentArray;
    }

    @Override
    public RunnerProgram run(RunStandard program) {
        return buildStandard(UTF_8, (arguments, scanner, bufferedWriter) -> //
            program.call(scanner, bufferedWriter));
    }

    @Override
    public RunnerProgram run(Charset charset, RunStandard program) {
        return buildStandard(charset, (arguments, scanner, bufferedWriter) -> //
            program.call(scanner, bufferedWriter));
    }

    @Override
    public RunnerProgram runConsole(RunConsole program) {
        return buildConsole(UTF_8, (arguments, inputStream, outputStream) -> //
            program.call(inputStream, outputStream));
    }

    @Override
    public RunnerProgram runConsole(Charset charset, RunConsole program) {
        return buildConsole(charset, (arguments, inputStream, outputStream) -> //
            program.call(inputStream, outputStream));
    }

    @Override
    public RunnerPreProgram run(RunStandardArgued program) {
        return buildStandard(UTF_8, program);
    }

    @Override
    public RunnerPreProgram run(Charset charset, RunStandardArgued program) {
        return buildStandard(charset, program);
    }

    @Override
    public RunnerPreProgram runConsole(RunConsoleArgued program) {
        return buildConsole(UTF_8, program);
    }

    @Override
    public RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program) {
        return buildConsole(charset, program);
    }

    @Override
    public RunnerProgram argument(String... arguments) {
        return new BoltProvideProgram(emptyOnNull(arguments), executor, outputCharset);
    }

    @Override
    public RunnerProgramOutput input(String... input) {
        return executeProgram(UTF_8, newInputStreamSupplier(input));
    }

    @Override
    public RunnerProgramOutput input(InputStreamSupplier supplier) {
        return executeProgram(UTF_8, supplier);
    }

    @Override
    public RunnerProgramOutput input(InputStreamSupplier supplier, Charset charset) {
        return executeProgram(charset, supplier);
    }

    @Override
    public RunnerProgramOutput loadInput(String resourceName, Class<?> withClass) {
        return loadInput(resourceName, withClass, UTF_8);
    }

    @Override
    public RunnerProgramOutput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        if (resourceName == null) {
            return executeProgram(charset, () -> {
                throw new RunnerException("bolt.runner.load.input.resource.name.null");
            });
        }
        if (withClass == null) {
            return executeProgram(charset, () -> {
                throw new RunnerException("bolt.runner.load.input.resource.class.null");
            });
        }
        if (withClass.getResource(resourceName) == null) {
            return executeProgram(charset, () -> {
                throw new RunnerException("bolt.runner.load.input.resource.not.found");
            });
        }

        return executeProgram(charset, () -> withClass.getResourceAsStream(resourceName));
    }

    private BoltProvideProgram buildStandard(Charset charset, RunStandardArgued program) {
        BoltProgramExecutor executor = (arguments, inputCharset, supplier, outputCharset, outputStream) -> //
            executeStandardArgued(arguments, inputCharset, supplier, outputCharset, outputStream, program);

        return new BoltProvideProgram(executor, charset);
    }

    private BoltProvideProgram buildConsole(Charset charset, RunConsoleArgued program) {
        BoltProgramExecutor executor = (arguments, inputCharset, supplier, outputCharset, outputStream) -> //
            executeConsoleArgued(arguments, inputCharset, supplier, outputCharset, outputStream, program);

        return new BoltProvideProgram(executor, charset);
    }

    private BoltProgramOutput executeProgram(Charset inputCharset, InputStreamSupplier streamSupplier) {
        return buildProgramOutput(argumentArray, inputCharset, streamSupplier, outputCharset, executor);
    }

}
