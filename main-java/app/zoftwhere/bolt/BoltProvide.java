package app.zoftwhere.bolt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;

import static app.zoftwhere.bolt.BoltReader.readArray;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * <p>Bolt Provide interface for Bolt Provide Input and Bolt Provide Program classes.
 * </p>
 * <p>This is a package-private interface for providing default functionality.
 * </p>
 *
 * @since 6.0.0
 */
interface BoltProvide {

    default String[] emptyOnNull(String[] value) {
        return value != null ? value : new String[0];
    }

    default InputStreamSupplier newInputStreamSupplier(String... input) {
        return () -> {
            if (input == null || input.length <= 0) {
                return new ByteArrayInputStream(new byte[0]);
            }

            for (String item : input) {
                if (item == null) {
                    throw new RunnerException("bolt.runner.variable.array.input.has.null");
                }
            }

            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                try (OutputStreamWriter writer = new OutputStreamWriter(output, UTF_8)) {
                    writer.append(input[0]);
                    for (int i = 1, s = input.length; i < s; i++) {
                        writer.append(System.lineSeparator());
                        writer.append(input[i]);
                    }
                    writer.flush();
                }
                return new ByteArrayInputStream(output.toByteArray());
            }
        };
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

    default Throwable executeStandardArgued(String[] arguments,
        Charset inputCharset,
        InputStreamSupplier streamSupplier,
        Charset outputCharset,
        OutputStream outputStream,
        RunStandardArgued program)
    {
        if (inputCharset == null) {
            return new RunnerException("bolt.runner.input.charset.null");
        }
        if (outputCharset == null) {
            return new RunnerException("bolt.runner.output.charset.null");
        }

        try (InputStream inputStream = streamSupplier.get()) {
            if (inputStream == null) {
                return new RunnerException("bolt.runner.load.input.input.stream.null");
            }
            try (Scanner scanner = newScanner(inputStream, inputCharset)) {
                try (PrintStream out = newPrintStream(outputStream, outputCharset)) {
                    program.call(arguments, scanner, out);
                }
            }
        }
        catch (Throwable throwable) {
            return throwable;
        }

        return null;
    }

    default Throwable executeConsoleArgued(String[] arguments,
        Charset inputCharset,
        InputStreamSupplier streamSupplier,
        Charset outputCharset,
        OutputStream outputStream,
        RunConsoleArgued program)
    {
        if (inputCharset == null) {
            return new RunnerException("bolt.runner.input.charset.null");
        }
        if (outputCharset == null) {
            return new RunnerException("bolt.runner.output.charset.null");
        }

        try (InputStream baseInputStream = streamSupplier.get()) {
            if (baseInputStream == null) {
                return new RunnerException("bolt.runner.load.input.input.stream.null");
            }
            try (InputStream inputStream = newInputStream(baseInputStream, inputCharset, outputCharset)) {
                program.call(arguments, inputStream, outputStream);
            }
        }
        catch (Throwable throwable) {
            return throwable;
        }

        return null;
    }

    default BoltProgramOutput buildProgramOutput(String[] arguments,
        Charset inputCharset,
        InputStreamSupplier streamSupplier,
        Charset outputCharset,
        BoltProgramExecutor executor)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Throwable throwable = executor.execute(arguments, inputCharset, streamSupplier, outputCharset, outputStream);

        if (inputCharset == null || outputCharset == null) {
            return new BoltProgramOutput(new String[] {""}, throwable);
        }

        final byte[] data = outputStream.toByteArray();
        final String[] output = readArray(() -> new BoltReader(data, outputCharset));
        return new BoltProgramOutput(output, throwable);
    }

}
