package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;

import static app.zoftwhere.bolt.BoltReader.readArray;
import static java.nio.charset.StandardCharsets.UTF_8;

interface BoltProvide {

    default String[] emptyOnNull(String[] value) {
        return value != null ? value : new String[0];
    }

    default InputStreamSupplier newInputStreamSupplier(String... input) {
        return () -> {
            if (input == null || input.length <= 0) {
                return new ByteArrayInputStream(new byte[0]);
            }

            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(output, UTF_8))) {
                bufferedWriter.write(input[0]);
                for (int i = 1, s = input.length; i < s; i++) {
                    bufferedWriter.newLine();
                    bufferedWriter.write(input[i]);
                }
                bufferedWriter.flush();
            }
            return new ByteArrayInputStream(output.toByteArray());
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

    default BufferedWriter newWriter(OutputStream outputStream, Charset charset) {
        return new BufferedWriter(new OutputStreamWriter(outputStream, charset));
    }

    default Throwable executeStandardArgued(String[] arguments,
        Charset inputCharset,
        InputStreamSupplier inputStreamSupplier,
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

        try (InputStream inputStream = inputStreamSupplier.get()) {
            if (inputStream == null) {
                return new RunnerException("bolt.runner.load.input.input.stream.null");
            }
            try (Scanner scanner = newScanner(inputStream, inputCharset)) {
                try (BufferedWriter bufferedWriter = newWriter(outputStream, outputCharset)) {
                    program.call(arguments, scanner, bufferedWriter);
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
        InputStreamSupplier inputStreamSupplier,
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

        try (InputStream baseInputStream = inputStreamSupplier.get()) {
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

        final byte[] data = outputStream.toByteArray();
        final String[] output = outputCharset != null ? readArray(
            () -> new BoltReader(data, outputCharset)) : new String[0];
        return new BoltProgramOutput(output, fromThrowable(throwable));
    }

    /**
     * Helper method to convert {@code Throwable} to {@code Exception}.
     *
     * @param throwable the throwable
     * @return {@code Exception}
     */
    default Exception fromThrowable(Throwable throwable) {
        if (throwable == null) { return null; }
        if (throwable instanceof Exception) { return (Exception) throwable; }
        return new Exception(throwable.getMessage(), throwable.getCause());
    }

}