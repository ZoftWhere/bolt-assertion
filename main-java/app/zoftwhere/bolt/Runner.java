package app.zoftwhere.bolt;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;

import static app.zoftwhere.bolt.RunnerHelper.forInput;
import static app.zoftwhere.bolt.RunnerHelper.forProgram;
import static java.nio.charset.StandardCharsets.UTF_8;

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
        return new BoltProgram(forProgram(program, UTF_8), UTF_8);
    }

    @Override
    public RunnerProgram run(Charset charset, RunStandard program) {
        return new BoltProgram(forProgram(program, charset), charset);
    }

    @Override
    public RunnerPreProgram run(RunStandardArgued program) {
        return new BoltPreProgram(forProgram(program, UTF_8), UTF_8);
    }

    @Override
    public RunnerPreProgram run(Charset charset, RunStandardArgued program) {
        return new BoltPreProgram(forProgram(program, charset), charset);
    }

    @Override
    public RunnerProgram runConsole(RunConsole program) {
        return new BoltProgram(program, UTF_8);
    }

    @Override
    public RunnerProgram runConsole(Charset charset, RunConsole program) {
        return new BoltProgram(program, charset);
    }

    @Override
    public RunnerPreProgram runConsole(RunConsoleArgued program) {
        return new BoltPreProgram(program, UTF_8);
    }

    @Override
    public RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program) {
        return new BoltPreProgram(program, charset);
    }

    @Override
    public RunnerProgramInput input(String... input) {
        return new BoltProgramInput(() -> forInput(input), UTF_8);
    }

    @Override
    public RunnerProgramInput input(InputStreamSupplier getInputStream) {
        return new BoltProgramInput(getInputStream, UTF_8);
    }

    @Override
    public RunnerProgramInput input(InputStreamSupplier getInputStream, Charset charset) {
        return new BoltProgramInput(getInputStream, charset);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
        return new BoltProgramInput(() -> withClass.getResourceAsStream(resourceName), UTF_8);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        final InputStreamSupplier resource = () -> withClass.getResourceAsStream(resourceName);
        return new BoltProgramInput(resource, charset);
    }

}
