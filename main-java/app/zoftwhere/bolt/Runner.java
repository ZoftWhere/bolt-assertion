package app.zoftwhere.bolt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;

/**
 * Bolt Assertion Runner.
 *
 * @since 1.0.0
 */
public class Runner extends AbstractRunner {

    public static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

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

    /** Default character encoding to use for input and program when none is specified. */
    private final Charset encoding;

    /**
     * <p>Constructor for a reusable, immutable, instance (more than one test can be run with it).
     * </p>
     * <p>The Runner static method {@link #newRunner()} may also be used.
     * </p>
     *
     * @since 2.0.0
     */
    public Runner() {
        this.encoding = DEFAULT_ENCODING;
    }

    /**
     * Private constructor creating an instance with a user-defined default encoding.
     *
     * @param encoding default encoding
     * @since 11.0.0
     */
    private Runner(Charset encoding) {
        this.encoding = encoding != null ? encoding : DEFAULT_ENCODING;
    }

    /**
     * {@inheritDoc}
     *
     * @since 11.0.0
     */
    @Override
    public RunnerInterface encoding(Charset encoding) {
        return new Runner(encoding);
    }

    /**
     * {@inheritDoc}
     *
     * @since 11.0.0
     */
    @Override
    public Charset encoding() {
        return encoding;
    }

    /**
     * {@inheritDoc}
     *
     * @since 7.0.0
     */
    @Override
    public RunnerProgram run(RunStandard program) {
        return new BoltProvideProgram(encoding).run(program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 7.0.0
     */
    @Override
    public RunnerProgram run(Charset charset, RunStandard program) {
        return new BoltProvideProgram(encoding).run(charset, program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerProgram runConsole(RunConsole program) {
        return new BoltProvideProgram(encoding).runConsole(program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerProgram runConsole(Charset charset, RunConsole program) {
        return new BoltProvideProgram(encoding).runConsole(charset, program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 7.0.0
     */
    @Override
    public RunnerPreProgram run(RunStandardArgued program) {
        return new BoltProvideProgram(encoding).run(program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 7.0.0
     */
    @Override
    public RunnerPreProgram run(Charset charset, RunStandardArgued program) {
        return new BoltProvideProgram(encoding).run(charset, program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerPreProgram runConsole(RunConsoleArgued program) {
        return new BoltProvideProgram(encoding).runConsole(program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program) {
        return new BoltProvideProgram(encoding).runConsole(charset, program);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput input(String... input) {
        return new BoltProvideInput(encoding).input(input);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerProgramInput input(InputStreamSupplier supplier) {
        return new BoltProvideInput(encoding).input(supplier);
    }

    /**
     * {@inheritDoc}
     *
     * @since 6.0.0
     */
    @Override
    public RunnerProgramInput input(InputStreamSupplier supplier, Charset charset) {
        return new BoltProvideInput(encoding).input(supplier, charset);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
        return new BoltProvideInput(encoding).loadInput(resourceName, withClass);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        return new BoltProvideInput(encoding).loadInput(resourceName, withClass, charset);
    }

}
