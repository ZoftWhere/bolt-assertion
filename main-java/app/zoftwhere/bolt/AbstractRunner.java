package app.zoftwhere.bolt;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerEncoding;
import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;

/**
 * Bolt Assertion Abstract Runner.
 *
 * @author Osmund
 * @version 11.2.0
 * @see app.zoftwhere.bolt.Runner
 * @since 6.0.0
 */
public abstract class AbstractRunner implements RunnerInterface, RunnerEncoding {

    /**
     * Constructor for AbstractRunner.
     *
     * @since 6.0.0
     */
    public AbstractRunner() {
    }

    /** {@inheritDoc} */
    @Override
    public abstract Charset encoding();

    /** {@inheritDoc} */
    @Override
    public abstract RunnerInterface encoding(Charset encoding);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerProgram run(RunStandard program);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerProgram run(Charset charset, RunStandard program);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerProgram runConsole(RunConsole program);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerProgram runConsole(Charset charset, RunConsole program);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerPreProgram run(RunStandardArgued program);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerPreProgram run(Charset charset, RunStandardArgued program);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerPreProgram runConsole(RunConsoleArgued program);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerProgramInput input(String... input);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerProgramInput input(Charset charset, String... input);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerProgramInput input(InputStreamSupplier supplier);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerProgramInput input(InputStreamSupplier supplier, Charset charset);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerProgramInput loadInput(String resourceName, Class<?> withClass);

    /** {@inheritDoc} */
    @Override
    public abstract RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset);

}
