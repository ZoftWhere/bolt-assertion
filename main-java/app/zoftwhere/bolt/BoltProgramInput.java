package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;

import static app.zoftwhere.bolt.RunnerHelper.forProgram;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Bolt program input class.
 *
 * @since 6.0.0
 */
class BoltProgramInput implements RunnerProgramInput {

    private final InputStreamSupplier getInput;

    private final Charset inputCharset;

    BoltProgramInput(InputStreamSupplier getInputStream, Charset charset) {
        this.getInput = getInputStream;
        this.inputCharset = charset;
    }

    /**
     * Specify the program arguments.
     *
     * @param arguments program arguments
     * @return {@link RunnerLoader}
     * @since 1.0.0
     */
    @Override
    public RunnerLoader argument(String... arguments) {
        return new BoltLoader(inputCharset, getInput, arguments);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    public RunnerProgramOutput run(RunStandard program) {
        RunStandardArgued internal = /**/
            (strings, scanner, writer) -> program.call(scanner, writer);
        return RunnerHelper.executeRun(internal, UTF_8, null, getInput, inputCharset);
    }
    /**
     * Specify the program.
     *
     * @param program the program
     * @param charset the charset for {@link BufferedWriter}
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    public RunnerProgramOutput run(Charset charset, RunStandard program) {
        RunConsoleArgued internal = /**/
            (strings, inputStream, outputStream) -> forProgram(program, charset).call(inputStream, outputStream);
        return RunnerHelper.executeRunConsole(internal, charset, null, getInput, inputCharset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    public RunnerProgramOutput runConsole(RunConsole program) {
        RunConsoleArgued internal = /**/
            (strings, inputStream, outputStream) -> program.call(inputStream, outputStream);
        return RunnerHelper.executeRunConsole(internal, UTF_8, null, getInput, inputCharset);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@code InputStream} and {@code OutputStream}
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    public RunnerProgramOutput runConsole(Charset charset, RunConsole program) {
        RunConsoleArgued internal = /**/
            (strings, inputStream, outputStream) -> program.call(inputStream, outputStream);
        return RunnerHelper.executeRunConsole(internal, charset, null, getInput, inputCharset);
    }

}
