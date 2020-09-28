package app.zoftwhere.bolt.api;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;

/**
 * Runner program execution input interface.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public interface RunnerProgramInput
    extends AbstractUnit.Arguments<RunnerLoader>, AbstractUnit.RunNoArguments<RunnerProgramOutput>
{

    /**
     * Specify the program arguments.
     *
     * @param arguments program argument array
     * @return {@link app.zoftwhere.bolt.api.RunnerLoader}
     * @since 1.0.0
     */
    @Override
    RunnerLoader argument(String... arguments);

    /**
     * Specify the scanner-printer program without arguments.
     *
     * @param program scanner-printer program without arguments
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 7.0.0
     */
    @Override
    RunnerProgramOutput run(RunStandard program);

    /**
     * Specify the scanner-printer program without arguments.
     *
     * @param charset character encoding of program {@link java.io.PrintStream}
     * @param program scanner-printer program without arguments
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 7.0.0
     */
    @Override
    RunnerProgramOutput run(Charset charset, RunStandard program);

    /**
     * Specify the input-output-stream program without arguments.
     *
     * @param program input-output-stream program without arguments
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    RunnerProgramOutput runConsole(RunConsole program);

    /**
     * Specify the input-output-stream program without arguments.
     *
     * @param charset character encoding of program {@link java.io.OutputStream}
     * @param program input-output-stream program without arguments
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    RunnerProgramOutput runConsole(Charset charset, RunConsole program);

}
