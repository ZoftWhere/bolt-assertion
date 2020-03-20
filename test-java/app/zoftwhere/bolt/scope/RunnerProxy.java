package app.zoftwhere.bolt.scope;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.AbstractRunner;
import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;

public class RunnerProxy extends AbstractRunner {

    private final Runner runner = new Runner();

    @SuppressWarnings("WeakerAccess")
    public RunnerProxy() {
    }

    @Override
    public RunnerProgramInput input(String... input) {
        return runner.input(input);
    }

    @Override
    public RunnerProgramInput input(InputStreamSupplier supplier) {
        return runner.input(supplier);
    }

    @Override
    public RunnerProgramInput input(InputStreamSupplier supplier, Charset decode) {
        return runner.input(supplier, decode);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
        return runner.loadInput(resourceName, withClass);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset decode) {
        return runner.loadInput(resourceName, withClass, decode);
    }

    @Override
    public RunnerProgram run(RunStandard program) {
        return runner.run(program);
    }

    @Override
    public RunnerProgram run(Charset charset, RunStandard program) {
        return runner.run(charset, program);
    }

    @Override
    public RunnerPreProgram run(RunStandardArgued program) {
        return runner.run(program);
    }

    @Override
    public RunnerPreProgram run(Charset charset, RunStandardArgued program) {
        return runner.run(charset, program);
    }

    @Override
    public RunnerProgram runConsole(RunConsole program) {
        return runner.runConsole(program);
    }

    @Override
    public RunnerProgram runConsole(Charset charset, RunConsole program) {
        return runner.runConsole(charset, program);
    }

    @Override
    public RunnerPreProgram runConsole(RunConsoleArgued program) {
        return runner.runConsole(program);
    }

    @Override
    public RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program) {
        return runner.runConsole(charset, program);
    }

}
