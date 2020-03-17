package app.zoftwhere.bolt;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;

/**
 * Bolt Assertion Runner.
 *
 * @since 1.0.0
 */
public class Runner extends AbstractRunner {

    /**
     * <p>This is an immutable runner (so get one, and run all the tests you need).
     * </p>
     *
     * @return an immutable runner instance
     * @since 1.0.0
     */
    public static Runner newRunner() {
        return new Runner();
    }

    /**
     * <p>Constructor for a reusable, immutable instance of the runner (more than one test can be run with it).
     * </p>
     * <p>The Runner static method {@link #newRunner()} may also be used.
     * </p>
     *
     * @since 2.0.0
     */
    public Runner() {
    }

    @Override
    public RunnerProgram run(RunStandard program) {
        return new BoltProvideProgram().run(program);
    }

    @Override
    public RunnerProgram run(Charset charset, RunStandard program) {
        return new BoltProvideProgram().run(charset, program);
    }

    @Override
    public RunnerProgram runConsole(RunConsole program) {
        return new BoltProvideProgram().runConsole(program);
    }

    @Override
    public RunnerProgram runConsole(Charset charset, RunConsole program) {
        return new BoltProvideProgram().runConsole(charset, program);
    }

    @Override
    public RunnerPreProgram run(RunStandardArgued program) {
        return new BoltProvideProgram().run(program);
    }

    @Override
    public RunnerPreProgram run(Charset charset, RunStandardArgued program) {
        return new BoltProvideProgram().run(charset, program);
    }

    @Override
    public RunnerPreProgram runConsole(RunConsoleArgued program) {
        return new BoltProvideProgram().runConsole(program);
    }

    @Override
    public RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program) {
        return new BoltProvideProgram().runConsole(charset, program);
    }

    @Override
    public RunnerProgramInput input(String... input) {
        return new BoltProvideInput().input(input);
    }

    @Override
    public RunnerProgramInput input(InputStreamSupplier getInputStream) {
        return new BoltProvideInput().input(getInputStream);
    }

    @Override
    public RunnerProgramInput input(InputStreamSupplier getInputStream, Charset charset) {
        return new BoltProvideInput().input(getInputStream, charset);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
        return new BoltProvideInput().loadInput(resourceName, withClass);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        return new BoltProvideInput().loadInput(resourceName, withClass, charset);
    }

}
