package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerProgramOutput;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Bolt program loader class.
 *
 * @since 6.0.0
 */
class BoltLoader implements RunnerLoader {

    private final InputStreamSupplier getInput;

    private final Charset inputCharset;

    private final String[] arguments;

    BoltLoader(Charset charset, InputStreamSupplier getInput, String[] arguments) {
        this.inputCharset = charset;
        this.getInput = getInput;
        this.arguments = arguments;
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    public RunnerProgramOutput run(RunStandardArgued program) {
        return RunnerHelper.executeRun(program, UTF_8, arguments, getInput, inputCharset);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@link BufferedWriter}
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    public RunnerProgramOutput run(Charset charset, RunStandardArgued program) {
        return RunnerHelper.executeRun(program, charset, arguments, getInput, inputCharset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    public RunnerProgramOutput runConsole(RunConsoleArgued program) {
        return RunnerHelper.executeRunConsole(program, UTF_8, arguments, getInput, inputCharset);
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
    public RunnerProgramOutput runConsole(Charset charset, RunConsoleArgued program) {
        return RunnerHelper.executeRunConsole(program, charset, arguments, getInput, inputCharset);
    }

}
