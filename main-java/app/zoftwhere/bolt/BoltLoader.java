package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Bolt program loader class.
 *
 * @since 6.0.0
 */
class BoltLoader implements RunnerLoader {

    /** Program input {@code InputStream} function */
    private final ThrowingFunction0<InputStream> getInput;

    private final Charset inputCharset;

    private final String[] arguments;

    BoltLoader(Charset charset, ThrowingFunction0<InputStream> getInput, String[] arguments) {
        this.inputCharset = charset;
        this.getInput = getInput;
        this.arguments = arguments;
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    public RunnerProgramOutput run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
        return RunnerHelper.executeRun(program, UTF_8, arguments, getInput, inputCharset);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@link BufferedWriter}
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 4.0.0
     */
    @Override
    public RunnerProgramOutput run(Charset charset, ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
        return RunnerHelper.executeRun(program, charset, arguments, getInput, inputCharset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    public RunnerProgramOutput runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program) {
        return RunnerHelper.executeRunConsole(program, UTF_8, arguments, getInput, inputCharset);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@code InputStream} and {@code OutputStream}
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    public RunnerProgramOutput runConsole(Charset charset,
        ThrowingConsumer3<String[], InputStream, OutputStream> program)
    {
        return RunnerHelper.executeRunConsole(program, charset, arguments, getInput, inputCharset);
    }

}
