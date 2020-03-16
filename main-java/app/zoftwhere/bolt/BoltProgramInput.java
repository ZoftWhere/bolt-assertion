package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.function.ThrowingConsumer2;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

import static app.zoftwhere.bolt.RunnerHelper.forProgram;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Bolt program input class.
 *
 * @since 6.0.0
 */
class BoltProgramInput implements RunnerProgramInput {

    private final ThrowingFunction0<InputStream> getInput;

    private final Charset inputCharset;

    BoltProgramInput(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
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
     * @since 1.0.0
     */
    public RunnerProgramOutput run(ThrowingConsumer2<Scanner, BufferedWriter> program) {
        ThrowingConsumer3<String[], Scanner, BufferedWriter> internal = /**/
            (strings, scanner, writer) -> program.accept(scanner, writer);
        return RunnerHelper.executeRun(internal, UTF_8, null, getInput, inputCharset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @param charset the charset for {@link BufferedWriter}
     * @return {@link RunnerProgramOutput}
     * @since 4.0.0
     */
    public RunnerProgramOutput run(Charset charset, ThrowingConsumer2<Scanner, BufferedWriter> program) {
        ThrowingConsumer3<String[], InputStream, OutputStream> internal = /**/
            (strings, inputStream, outputStream) -> forProgram(program, charset).accept(inputStream, outputStream);
        return RunnerHelper.executeRunConsole(internal, charset, null, getInput, inputCharset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    public RunnerProgramOutput runConsole(ThrowingConsumer2<InputStream, OutputStream> program) {
        ThrowingConsumer3<String[], InputStream, OutputStream> internal = /**/
            (strings, inputStream, outputStream) -> program.accept(inputStream, outputStream);
        return RunnerHelper.executeRunConsole(internal, UTF_8, null, getInput, inputCharset);
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
    public RunnerProgramOutput runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program) {
        ThrowingConsumer3<String[], InputStream, OutputStream> internal = /**/
            (strings, inputStream, outputStream) -> program.accept(inputStream, outputStream);
        return RunnerHelper.executeRunConsole(internal, charset, null, getInput, inputCharset);
    }

}
