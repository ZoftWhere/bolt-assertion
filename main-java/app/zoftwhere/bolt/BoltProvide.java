package app.zoftwhere.bolt;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;

import static app.zoftwhere.bolt.BoltReader.readArray;

/**
 * <p>Bolt Provide interface for Bolt Provide Input and Bolt Provide Program classes.
 * </p>
 * <p>This is a package-private interface for providing default functionality.
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
interface BoltProvide {

    /** New line definition for parsing that allows execution to be system agnostic. */
    String NEW_LINE = "\r\n";

    default String[] emptyOnNull(String[] value) {
        return value != null ? value : new String[0];
    }

    default InputStream newInputStream(InputStream inputStream, Charset source, Charset destination) {
        if (Objects.equals(source, destination)) {
            return inputStream;
        }

        return new BoltInputStream(inputStream, source, destination);
    }

    default Scanner newScanner(InputStream inputStream, Charset charset) {
        // Charset.name() for backwards compatibility.
        return new Scanner(inputStream, charset.name());
    }

    default PrintStream newPrintStream(OutputStream outputStream, Charset charset)
    throws UnsupportedEncodingException
    {
        // Charset.name() for backwards compatibility.
        return new PrintStream(outputStream, false, charset.name());
    }

    /**
     * Create a scanner-printer program proxy.
     *
     * @param program scanner-printer program without arguments
     * @return scanner-printer proxy program with arguments
     * @since 10.0.0
     */
    default RunStandardArgued proxyRunStandard(RunStandard program) {
        return program != null ? (arguments, scanner, out) -> program.call(scanner, out) : null;
    }

    /**
     * Create a input-output-stream program proxy.
     *
     * @param program input-output-stream program without arguments
     * @return input-output-stream proxy program with arguments
     * @since 10.0.0
     */
    default RunConsoleArgued proxyRunConsole(RunConsole program) {
        return program != null ? (arguments, input, output) -> program.call(input, output) : null;
    }

    /**
     * Retrieve {@link app.zoftwhere.bolt.BoltExecutor} for program.
     *
     * @param program scanner-printer program with arguments (or proxy)
     * @return {@link app.zoftwhere.bolt.BoltExecutor} if program non-null, null otherwise
     * @since 10.0.0
     */
    default BoltExecutor buildStandardExecutor(RunStandardArgued program) {
        if (program == null) {
            return null;
        }

        return (arguments, inputCharset, inputStream, outputCharset, outputStream) -> {
            try (Scanner scanner = newScanner(inputStream, inputCharset)) {
                try (PrintStream out = newPrintStream(outputStream, outputCharset)) {
                    program.call(arguments, scanner, out);
                }
            }
            catch (Exception e) {
                return e;
            }
            return null;
        };
    }

    /**
     * Retrieve {@link app.zoftwhere.bolt.BoltExecutor} for program.
     *
     * @param program input-output-stream program with arguments (or proxy)
     * @return {@link app.zoftwhere.bolt.BoltExecutor} if program non-null, null otherwise
     * @since 11.0.0
     */
    default BoltExecutor buildConsoleExecutor(RunConsoleArgued program) {
        if (program == null) {
            return null;
        }

        return (arguments, inputCharset, inputStream, outputCharset, outputStream) -> {
            try (InputStream stream = newInputStream(inputStream, inputCharset, outputCharset)) {
                program.call(arguments, stream, outputStream);
            }
            catch (Exception e) {
                return e;
            }
            return null;
        };
    }

    /**
     * Returns the program output.
     *
     * @param encoding       a {@link java.nio.charset.Charset} object.
     * @param arguments      program argument array
     * @param inputCharset   character encoding for program input {@link java.io.InputStream}
     * @param streamSupplier {@link java.io.InputStream} supplier for program input
     * @param outputCharset  character encoding for program output
     * @param executor       program executor
     * @param loadError      a {@link java.lang.Exception} object.
     * @return {@link app.zoftwhere.bolt.BoltProgramOutput}
     * @since 11.0.0
     */
    default BoltProgramOutput buildOutput(
        Charset encoding,
        String[] arguments,
        Charset inputCharset,
        InputStreamSupplier streamSupplier,
        Charset outputCharset,
        BoltExecutor executor,
        Exception loadError)
    {
        final String[] blank = new String[] {""};
        if (executor == null) {
            RunnerException error = new RunnerException("bolt.runner.program.null");
            return new BoltProgramOutput(encoding, blank, Duration.ZERO, error);
        }

        if (inputCharset == null) {
            RunnerException error = new RunnerException("bolt.runner.input.charset.null");
            return new BoltProgramOutput(encoding, blank, Duration.ZERO, error);
        }

        if (outputCharset == null) {
            RunnerException error = new RunnerException("bolt.runner.output.charset.null");
            return new BoltProgramOutput(encoding, blank, Duration.ZERO, error);
        }

        if (streamSupplier == null) {
            RunnerException error = new RunnerException("bolt.runner.input.stream.supplier.null");
            return new BoltProgramOutput(encoding, blank, Duration.ZERO, error);
        }

        if (loadError != null) {
            return new BoltProgramOutput(encoding, blank, Duration.ZERO, loadError);
        }

        try (InputStream inputStream = streamSupplier.get()) {
            if (inputStream == null) {
                RunnerException error = new RunnerException("bolt.runner.load.input.input.stream.null");
                return new BoltProgramOutput(encoding, blank, Duration.ZERO, error);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Call the executor.
            Instant from = Instant.now();
            Exception error = executor.execute(arguments, inputCharset, inputStream, outputCharset, outputStream);
            Instant to = Instant.now();

            // Execution duration calculation is correct if duration is less than 292 years.
            Duration time = Duration.ofNanos(from.until(to, ChronoUnit.NANOS));

            final byte[] data = outputStream.toByteArray();
            final String[] output = readArray(() -> new BoltReader(data, outputCharset));
            return new BoltProgramOutput(encoding, output, time, error);
        }
        catch (Exception e) {
            return new BoltProgramOutput(encoding, blank, Duration.ZERO, e);
        }
    }

}
