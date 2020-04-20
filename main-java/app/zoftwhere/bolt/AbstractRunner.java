package app.zoftwhere.bolt;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerEncoding;
import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;

/**
 * Runner abstract class.
 *
 * @since 6.0.0
 */
public abstract class AbstractRunner implements RunnerInterface, RunnerEncoding {

    public AbstractRunner() {
    }

    @Override
    public abstract Charset encoding();

    @Override
    public abstract RunnerProgram run(RunStandard program);

    @Override
    public abstract RunnerProgram run(Charset charset, RunStandard program);

    @Override
    public abstract RunnerProgram runConsole(RunConsole program);

    @Override
    public abstract RunnerProgram runConsole(Charset charset, RunConsole program);

    @Override
    public abstract RunnerPreProgram run(RunStandardArgued program);

    @Override
    public abstract RunnerPreProgram run(Charset charset, RunStandardArgued program);

    @Override
    public abstract RunnerPreProgram runConsole(RunConsoleArgued program);

    @Override
    public abstract RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program);

    @Override
    public abstract RunnerProgramInput input(String... input);

    @Override
    public abstract RunnerProgramInput input(InputStreamSupplier supplier);

    @Override
    public abstract RunnerProgramInput input(InputStreamSupplier supplier, Charset charset);

    @Override
    public abstract RunnerProgramInput loadInput(String resourceName, Class<?> withClass);

    @Override
    public abstract RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset);

}
