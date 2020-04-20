package app.zoftwhere.bolt.deluge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Scanner;

import app.zoftwhere.bolt.RunnerException;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;

import static app.zoftwhere.bolt.BoltTestHelper.array;
import static app.zoftwhere.bolt.BoltTestHelper.escapeString;
import static app.zoftwhere.bolt.BoltTestHelper.isOrHasNull;
import static app.zoftwhere.bolt.BoltTestHelper.readArray;
import static app.zoftwhere.bolt.BoltTestHelper.transcode;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.ARRAY;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.RESOURCE;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.RESOURCE_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.STREAM;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.STREAM_ENCODED;

class DelugeMock {

    static DelugeMock from(DelugeProgramType type, DelugeSettings settings, DelugeData data) {
        return new DelugeMock(type, settings, data);
    }

    private final DelugeBuilder builder;
    private final DelugeProgramType programType;
    private final DelugeSettings settings;
    private final DelugeData data;

    private DelugeMock(DelugeProgramType programType, DelugeSettings settings, DelugeData data) {
        this.builder = DelugeBuilder.from(programType, settings, data);
        this.programType = programType;
        this.settings = settings;
        this.data = data;
    }

    DelugeResult buildExpectedOutput() {

        if (STREAM_ENCODED == data.type() || RESOURCE_ENCODED == data.type()) {
            if (data.charset() == null) {
                String exceptionMessage = "bolt.runner.input.charset.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeResult.from(array(""), Duration.ZERO, error);
            }
        }

        if (settings.hasCharSet() && settings.charset() == null) {
            String exceptionMessage = "bolt.runner.output.charset.null";
            Exception error = new RunnerException(exceptionMessage, null);
            return DelugeResult.from(array(""), Duration.ZERO, error);
        }

        if (ARRAY == data.type()) {
            if (data.array() != null && isOrHasNull(data.array())) {
                String exceptionMessage = "bolt.runner.variable.array.input.has.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeResult.from(array(""), Duration.ZERO, error);
            }
        }

        if (RESOURCE == data.type() || RESOURCE_ENCODED == data.type()) {
            if (data.resource() == null) {
                String exceptionMessage = "bolt.runner.load.input.resource.name.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeResult.from(array(""), Duration.ZERO, error);
            }

            if (data.withClass() == null) {
                String exceptionMessage = "bolt.runner.load.input.resource.class.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeResult.from(array(""), Duration.ZERO, error);
            }

            final var url = data.withClass().getResource(data.resource());
            if (url == null) {
                String exceptionMessage = "bolt.runner.load.input.resource.not.found";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeResult.from(array(""), Duration.ZERO, error);
            }
        }

        if (STREAM == data.type() || STREAM_ENCODED == data.type()) {
            if (data.error() != null) {
                return DelugeResult.from(array(""), Duration.ZERO, data.error());
            }

            if (data.array() == null) {
                String exceptionMessage = "bolt.runner.input.stream.supplier.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeResult.from(array(""), Duration.ZERO, error);
            }

            if (isOrHasNull(data.array())) {
                String exceptionMessage = "bolt.runner.load.input.input.stream.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeResult.from(array(""), Duration.ZERO, error);
            }
        }

        if (settings.hasError()) {
            return DelugeResult.from(array(""), Duration.ofDays(1), settings.error());
        }
        if (data.hasError()) {
            return DelugeResult.from(array(""), Duration.ofDays(1), data.error());
        }

        return buildOutput();
    }

    private DelugeResult buildOutput() {
        Exception error = null;
        Charset inEnc = builder.inputCharset();
        Charset outEnc = builder.outputCharset();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream, false, outEnc);

        if (!programType.isArgued()) {
            out.println("Argument: <null>");
        }
        else if (settings.argumentArray() == null) {
            // Runner should pass an empty array.
            out.println("Argument: <none>");
        }
        else if (settings.argumentArray().length == 0) {
            out.println("Argument: <none>");
        }
        else {
            for (String argument : settings.argumentArray()) {
                if (argument == null) {
                    out.println("Argument: <null>");
                }
                else {
                    out.println(String.format("Argument: \"%s\"", escapeString(argument)));
                }
            }
        }

        InputStreamSupplier supplier = newSupplier();
        try (InputStream inputStream = supplier != null ? supplier.get() : null) {
            try (Scanner scanner = newScanner(inputStream, inEnc, outEnc)) {
                DelugeLineScanner lineScanner = new DelugeLineScanner(scanner);

                out.printf("Line: \"%s\"", escapeString(lineScanner.firstLine()));
                while (lineScanner.hasMore()) {
                    out.println();
                    out.printf("Line: \"%s\"", escapeString(lineScanner.readLine()));
                }
            }
        }
        catch (Exception e) {
            error = e;
        }

        final byte[] data = outputStream.toByteArray();

        String[] lines = readArray(data, outEnc);
        return DelugeResult.from(lines, Duration.ofDays(1), error);
    }

    private InputStreamSupplier newSupplier() {
        if (ARRAY == data.type()) {
            if (data.array() == null) {
                return () -> new ByteArrayInputStream(new byte[0]);
            }

            Charset charset = builder.inputCharset();
            return data.newInputStreamSupplier(charset);
        }

        return data.streamSupplier();
    }

    private Scanner newScanner(InputStream inputStream, Charset inEnc, Charset outEnc) {
        if (programType.isStandard()) {
            return new Scanner(inputStream, builder.inputCharset());
        }
        else if (programType.isConsole()) {
            return new Scanner(transcode(inputStream, inEnc, outEnc), outEnc);
        }
        else {
            throw new RuntimeException();
        }
    }

}
