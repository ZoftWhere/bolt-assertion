package app.zoftwhere.bolt.api;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;

/**
 * Runner program loader interface.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public interface RunnerLoader extends AbstractUnit.RunWithArguments<RunnerProgramOutput> {

    /**
     * Specify the scanner-printer program with arguments.
     *
     * @param program scanner-printer program with arguments
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 7.0.0
     */
    @Override
    RunnerProgramOutput run(RunStandardArgued program);

    /**
     * Specify the scanner-printer program with arguments.
     *
     * @param program scanner-printer program with arguments
     * @param charset character encoding of program {@link java.io.PrintStream}
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 7.0.0
     */
    @Override
    RunnerProgramOutput run(Charset charset, RunStandardArgued program);

    /**
     * Specify the input-output-stream program with arguments.
     *
     * @param program input-output-stream program with arguments
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    RunnerProgramOutput runConsole(RunConsoleArgued program);

    /**
     * Specify the input-output-stream program with arguments.
     *
     * @param program input-output-stream program with arguments
     * @param charset character encoding of program {@link java.io.OutputStream}
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    RunnerProgramOutput runConsole(Charset charset, RunConsoleArgued program);

}
