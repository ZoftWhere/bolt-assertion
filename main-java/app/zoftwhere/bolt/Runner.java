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
     * <p>Constructor for a reusable, immutable, instance (more than one test can be run with it).
     * </p>
     * <p>The Runner static method {@link #newRunner()} may also be used.
     * </p>
     *
     * @since 2.0.0
     */
    public Runner() {
    }

    /**
     * {@inheritDoc}
     *
     * @since 7.0.0
     */
    @Override
    public RunnerProgram run(RunStandard program) {
        return new BoltProvideProgram().run(program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 7.0.0
     */
    @Override
    public RunnerProgram run(Charset charset, RunStandard program) {
        return new BoltProvideProgram().run(charset, program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerProgram runConsole(RunConsole program) {
        return new BoltProvideProgram().runConsole(program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerProgram runConsole(Charset charset, RunConsole program) {
        return new BoltProvideProgram().runConsole(charset, program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 7.0.0
     */
    @Override
    public RunnerPreProgram run(RunStandardArgued program) {
        return new BoltProvideProgram().run(program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 7.0.0
     */
    @Override
    public RunnerPreProgram run(Charset charset, RunStandardArgued program) {
        return new BoltProvideProgram().run(charset, program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerPreProgram runConsole(RunConsoleArgued program) {
        return new BoltProvideProgram().runConsole(program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program) {
        return new BoltProvideProgram().runConsole(charset, program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput input(String... input) {
        return new BoltProvideInput().input(input);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerProgramInput input(InputStreamSupplier supplier) {
        return new BoltProvideInput().input(supplier);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerProgramInput input(InputStreamSupplier supplier, Charset charset) {
        return new BoltProvideInput().input(supplier, charset);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
        return new BoltProvideInput().loadInput(resourceName, withClass);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        return new BoltProvideInput().loadInput(resourceName, withClass, charset);
    }

}
