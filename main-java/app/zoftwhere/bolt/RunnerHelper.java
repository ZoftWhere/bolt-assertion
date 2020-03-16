package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.function.ThrowingConsumer2;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

import static app.zoftwhere.bolt.RunnerReader.readArray;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

class RunnerHelper {

    /**
     * Helper method for providing an {@code InputStream} for a array of String.
     *
     * @param input String array for input
     * @return {@code InputStream}
     * @throws IOException IO exception
     */
    static InputStream forInput(String[] input) throws IOException {
        if (input == null || input.length == 0) {
            return new ByteArrayInputStream(new byte[0]);
        }

        try (ByteArrayOutputStream outputStream = newOutputStream()) {
            try (BufferedWriter writer = newWriter(outputStream, UTF_8)) {
                writer.write(input[0]);
                for (int i = 1, size = input.length; i < size; i++) {
                    writer.newLine();
                    writer.write(input[i]);
                }
                writer.flush();
                return new ByteArrayInputStream(outputStream.toByteArray());
            }
        }
    }

    /**
     * Helper method for providing {@code Scanner} and {@link BufferedWriter} to program.
     *
     * @param program the program
     * @param charset the program character set encoding
     * @return standard {@code InputStream}, {@code OutputStream} consumer
     */
    static ThrowingConsumer2<InputStream, OutputStream> forProgram( //
        ThrowingConsumer2<Scanner, BufferedWriter> program, //
        Charset charset) //
    {
        return (inputStream, outputStream) -> {
            try (Scanner scanner = newScanner(inputStream, charset)) {
                try (BufferedWriter writer = newWriter(outputStream, charset)) {
                    program.accept(scanner, writer);
                }
            }
        };
    }

    /**
     * Helper method for providing {@code Scanner} and {@link BufferedWriter} to program.
     *
     * @param program the program
     * @param charset the program character set encoding
     * @return standard {@code InputStream}, {@code OutputStream} consumer
     */
    static ThrowingConsumer3<String[], InputStream, OutputStream> forProgram( //
        ThrowingConsumer3<String[], Scanner, BufferedWriter> program, //
        Charset charset) //
    {
        return (array, inputStream, outputStream) -> {
            try (Scanner scanner = newScanner(inputStream, charset)) {
                try (BufferedWriter writer = newWriter(outputStream, charset)) {
                    program.accept(array, scanner, writer);
                }
            }
        };
    }

    /**
     * Helper method for executing the program and collecting the output.
     *
     * @param program       the program
     * @param outputCharset the program output character set encoding
     * @param arguments     the program arguments
     * @param input         the program input
     * @param inputCharset  the program input character set encoding
     * @return {@link RunnerProgramOutput}
     */
    static BoltProgramOutput executeRun( //
        ThrowingConsumer3<String[], Scanner, BufferedWriter> program, //
        Charset outputCharset, //
        String[] arguments, //
        ThrowingFunction0<InputStream> input, //
        Charset inputCharset) //
    {
        ByteArrayOutputStream outputStream = newOutputStream();
        Throwable throwable = null;

        try (BufferedWriter writer = newWriter(outputStream, outputCharset); //
            Scanner scanner = newScanner(input.accept(), inputCharset)) //
        {
            program.accept(arguments, scanner, writer);
        }
        catch (Throwable e) {
            throwable = e;
        }

        final byte[] data = outputStream.toByteArray();
        final String[] output = readArray(() -> new RunnerReader(data, outputCharset));
        return new BoltProgramOutput(output, fromThrowable(throwable));
    }

    /**
     * Helper method for executing the program and collecting the output.
     *
     * @param program       the program
     * @param outputCharset the program output character set encoding
     * @param arguments     the program arguments
     * @param input         the program input
     * @param inputCharset  the program input character set encoding
     * @return {@link BoltProgramOutput}
     */
    static BoltProgramOutput executeRunConsole( //
        ThrowingConsumer3<String[], InputStream, OutputStream> program, //
        Charset outputCharset, //
        String[] arguments, //
        ThrowingFunction0<InputStream> input, //
        Charset inputCharset) //
    {
        ByteArrayOutputStream outputStream = newOutputStream();
        Throwable throwable = null;

        try (InputStream inputStream = newInputStreamSupplier(input, inputCharset, outputCharset).accept()) {
            if (inputStream == null) {
                throw new NullPointerException("bolt.runner.load.input.input.stream.null");
            }
            program.accept(arguments, inputStream, outputStream);
        }
        catch (Throwable e) {
            throwable = e;
        }

        final byte[] data = outputStream.toByteArray();
        final String[] output = readArray(() -> new RunnerReader(data, outputCharset));
        return new BoltProgramOutput(output, fromThrowable(throwable));
    }

    /**
     * Helper method for getting a new {@code Scanner}.
     *
     * @param inputStream program input {@code InputStream}
     * @param charset     program input character set encoding
     * @return {@code Scanner}
     */
    private static Scanner newScanner(InputStream inputStream, Charset charset) {
        // Scanner(InputStream, String) for backward compatibility.
        requireNonNull(inputStream, "bolt.runner.load.input.input.stream.null");
        return new Scanner(inputStream, charset.name());
    }

    /**
     * Helper method for getting a new {@code ByteArrayOutputStream}.
     *
     * @return {@code ByteArrayOutputStream}
     */
    private static ByteArrayOutputStream newOutputStream() {
        return new ByteArrayOutputStream(1024);
    }

    /**
     * Helper method for getting a {@link BufferedWriter} wrapping an {@code OutputStreamWriter}.
     *
     * @param outputStream {@code OutputStream}
     * @param charset      {@code OutputStream} character set encoding
     * @return {@link BufferedWriter}
     */
    private static BufferedWriter newWriter(OutputStream outputStream, Charset charset) {
        final OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset);
        return new BufferedWriter(writer);
    }

    /**
     * Helper method for setting up decoded {@code InputStream}.
     *
     * @param input   the input stream
     * @param charset the input stream character set encoding
     * @param decode  the desired character set encoding
     * @return {@code InputStream} with desired character set as encoding
     */
    private static ThrowingFunction0<InputStream> newInputStreamSupplier( //
        ThrowingFunction0<InputStream> input, //
        Charset charset, //
        Charset decode) //
    {
        if (charset.name().equals(decode.name())) {
            return input;
        }

        return () -> new RunnerInputStream(input.accept(), charset, decode);
    }

    /**
     * Helper method to convert {@code Throwable} to {@code Exception}.
     *
     * @param throwable the throwable
     * @return {@code Exception}
     */
    private static Exception fromThrowable(Throwable throwable) {
        if (throwable == null) { return null; }
        if (throwable instanceof Exception) { return (Exception) throwable; }
        return new Exception(throwable);
    }

}
