package app.zoftwhere.bolt.api;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;

/**
 * <p>Runner accept program interface.
 * </p>
 * <p>This interface that forms the basis for Runner#run() and Runner#runConsole().
 * </p>
 *
 * @since 6.0.0
 */
public interface RunnerProvideProgram
    extends AbstractUnit.RunNoArguments<RunnerProgram>, AbstractUnit.RunWithArguments<RunnerPreProgram>
{

    /**
     * Specify the scanner-writer program without arguments.
     *
     * @param program scanner-writer program without arguments
     * @return {@link RunnerProgram}
     * @since 6.0.0
     */
    @Override
    RunnerProgram run(RunStandard program);

    /**
     * Specify the scanner-writer program without arguments.
     *
     * @param program scanner-writer program without arguments
     * @param charset program {@link BufferedWriter} {@link Charset}
     * @return {@link RunnerProgram}
     * @since 6.0.0
     */
    @Override
    RunnerProgram run(Charset charset, RunStandard program);

    /**
     * Specify the input-output-stream program without arguments.
     *
     * @param program input-output-stream program without arguments
     * @return {@link RunnerProgram}
     * @since 6.0.0
     */
    @Override
    RunnerProgram runConsole(RunConsole program);

    /**
     * Specify the input-output-stream program without arguments.
     *
     * @param program input-output-stream program without arguments
     * @param charset program {@link OutputStream} {@link Charset}
     * @return {@link RunnerProgram}
     * @since 6.0.0
     */
    @Override
    RunnerProgram runConsole(Charset charset, RunConsole program);

    /**
     * Specify the scanner-writer program with arguments.
     *
     * @param program scanner-writer program with arguments
     * @return {@link RunnerProgram}
     * @since 6.0.0
     */
    @Override
    RunnerPreProgram run(RunStandardArgued program);

    /**
     * Specify the scanner-writer program with arguments.
     *
     * @param program scanner-writer program with arguments
     * @param charset program {@link BufferedWriter} {@link Charset}
     * @return {@link RunnerProgram}
     * @since 6.0.0
     */
    @Override
    RunnerPreProgram run(Charset charset, RunStandardArgued program);

    /**
     * Specify the input-output-stream program with arguments.
     *
     * @param program input-output-stream program with arguments
     * @return {@link RunnerProgram}
     * @since 6.0.0
     */
    @Override
    RunnerPreProgram runConsole(RunConsoleArgued program);

    /**
     * Specify the input-output-stream program with arguments.
     *
     * @param program input-output-stream program with arguments
     * @param charset program {@link OutputStream} {@link Charset}
     * @return {@link RunnerProgram}
     * @since 6.0.0
     */
    @Override
    RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program);

}
