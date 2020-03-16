package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.function.ThrowingConsumer2;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

import static app.zoftwhere.bolt.RunnerHelper.forInput;
import static app.zoftwhere.bolt.RunnerHelper.forProgram;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Bolt Assertion Runner.
 */
public class Runner extends AbstractRunner {

    /**
     * This is an immutable runner (so get one, and run all the tests you need).
     *
     * @return an immutable runner instance
     * @since 1.0.0
     */
    public static Runner newRunner() {
        return new Runner();
    }

    /**
     * <p>Constructor for a reusable, immutable instance of the runner (more than one test can be run with it).
     * </p>
     * <p>The Runner static method {@link #newRunner()} may also be used.
     * </p>
     *
     * @since 2.0.0
     */
    public Runner() {
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerProgram}
     * @since 1.0.0
     */
    @Override
    public RunnerProgram run(ThrowingConsumer2<Scanner, BufferedWriter> program) {
        return new BoltProgram(forProgram(program, UTF_8), UTF_8);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@link BufferedWriter}
     * @param program the program
     * @return {@link RunnerProgram}
     * @since 4.0.0
     */
    @Override
    public RunnerProgram run(Charset charset, ThrowingConsumer2<Scanner, BufferedWriter> program) {
        return new BoltProgram(forProgram(program, charset), charset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerPreProgram}
     * @since 1.0.0
     */
    @Override
    public RunnerPreProgram run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
        return new BoltPreProgram(forProgram(program, UTF_8), UTF_8);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@link BufferedWriter}
     * @param program the program
     * @return {@link RunnerPreProgram}
     * @since 4.0.0
     */
    @Override
    public RunnerPreProgram run(Charset charset, ThrowingConsumer3<String[], Scanner, BufferedWriter> program) {
        return new BoltPreProgram(forProgram(program, charset), charset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerProgram}
     * @since 1.0.0
     */
    @Override
    public RunnerProgram runConsole(ThrowingConsumer2<InputStream, OutputStream> program) {
        return new BoltProgram(program, UTF_8);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@code InputStream} and {@code OutputStream}
     * @param program the program
     * @return {@link RunnerProgram}
     * @since 2.0.0
     */
    @Override
    public RunnerProgram runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program) {
        return new BoltProgram(program, charset);
    }

    /**
     * Specify the program.
     *
     * @param program the program
     * @return {@link RunnerPreProgram}
     * @since 1.0.0
     */
    @Override
    public RunnerPreProgram runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program) {
        return new BoltPreProgram(program, UTF_8);
    }

    /**
     * Specify the program.
     *
     * @param charset the charset for {@code InputStream} and {@code OutputStream}
     * @param program the program
     * @return {@link RunnerPreProgram}
     * @since 2.0.0
     */
    @Override
    public RunnerPreProgram runConsole(Charset charset,
        ThrowingConsumer3<String[], InputStream, OutputStream> program)
    {
        return new BoltPreProgram(program, charset);
    }

    /**
     * Specify the input.
     *
     * @param input {@code String} array for input
     * @return {@link RunnerProgramInput}
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput input(String... input) {
        return new BoltProgramInput(() -> forInput(input), UTF_8);
    }

    /**
     * Specify the input.
     *
     * @param getInputStream {@code InputStream} function for input
     * @return {@link RunnerProgramInput}
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput input(ThrowingFunction0<InputStream> getInputStream) {
        return new BoltProgramInput(getInputStream, UTF_8);
    }

    /**
     * Specify the input.
     *
     * @param getInputStream {@code InputStream} function for input
     * @param charset        character set encoding for the program
     * @return {@link RunnerProgramInput}
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput input(ThrowingFunction0<InputStream> getInputStream, Charset charset) {
        return new BoltProgramInput(getInputStream, charset);
    }

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading input
     * @param withClass    resource class for retrieving resource as {@code InputStream}
     * @return {@link RunnerProgramInput}
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
        return new BoltProgramInput(() -> withClass.getResourceAsStream(resourceName), UTF_8);
    }

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading input
     * @param withClass    resource class for retrieving resource as {@code InputStream}
     * @param charset      resource character set encoding
     * @return {@link RunnerProgramInput}
     * @since 1.0.0
     */
    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        final ThrowingFunction0<InputStream> resource = () -> withClass.getResourceAsStream(resourceName);
        return new BoltProgramInput(resource, charset);
    }

}
