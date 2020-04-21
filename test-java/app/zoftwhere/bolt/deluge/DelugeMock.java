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
import static app.zoftwhere.bolt.BoltTestHelper.arrayHasNull;
import static app.zoftwhere.bolt.BoltTestHelper.escapeString;
import static app.zoftwhere.bolt.BoltTestHelper.readArray;
import static app.zoftwhere.bolt.BoltTestHelper.transcode;
import static app.zoftwhere.bolt.deluge.DelugeDataType.ARRAY;
import static app.zoftwhere.bolt.deluge.DelugeDataType.ARRAY_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeDataType.RESOURCE;
import static app.zoftwhere.bolt.deluge.DelugeDataType.RESOURCE_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeDataType.STREAM;
import static app.zoftwhere.bolt.deluge.DelugeDataType.STREAM_ENCODED;

class DelugeMock {

    static DelugeMock from(DelugeProgramType type, DelugeSetting setting, DelugeData input) {
        return new DelugeMock(type, setting, input);
    }

    private final DelugeBuilder builder;
    private final DelugeProgramType type;
    private final DelugeSetting setting;
    private final DelugeData input;

    private DelugeMock(DelugeProgramType type, DelugeSetting setting, DelugeData input) {
        this.builder = DelugeBuilder.from(type, setting, input);
        this.type = type;
        this.setting = setting;
        this.input = input;
    }

    DelugeProgramOutput buildExpectedOutput() {

        if (input.hasCharset() && input.charset() == null) {
            String exceptionMessage = "bolt.runner.input.charset.null";
            Exception error = new RunnerException(exceptionMessage, null);
            return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
        }

        if (setting.hasCharSet() && setting.charset() == null) {
            String exceptionMessage = "bolt.runner.output.charset.null";
            Exception error = new RunnerException(exceptionMessage, null);
            return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
        }

        if (ARRAY == input.type() || ARRAY_ENCODED == input.type()) {
            if (input.array() == null) {
                String exceptionMessage = "bolt.runner.variable.argument.input.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }

            if (arrayHasNull(input.array())) {
                String exceptionMessage = "bolt.runner.variable.argument.input.has.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }
        }
        else if (RESOURCE == input.type() || RESOURCE_ENCODED == input.type()) {
            if (input.resource() == null) {
                String exceptionMessage = "bolt.runner.load.input.resource.name.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }

            if (input.withClass() == null) {
                String exceptionMessage = "bolt.runner.load.input.resource.class.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }

            final var url = input.withClass().getResource(input.resource());
            if (url == null) {
                String exceptionMessage = "bolt.runner.load.input.resource.not.found";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }
        }
        else if (STREAM == input.type() || STREAM_ENCODED == input.type()) {
            if (input.error() != null) {
                return DelugeProgramOutput.from(array(""), Duration.ZERO, input.error());
            }

            if (input.array() == null) {
                String exceptionMessage = "bolt.runner.input.stream.supplier.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }

            if (arrayHasNull(input.array())) {
                String exceptionMessage = "bolt.runner.load.input.input.stream.null";
                Exception error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }
        }
        else {
            throw new DelugeException("deluge.mock.type.switch.default: " + input.type());
        }

        if (setting.hasError()) {
            return DelugeProgramOutput.from(array(""), Duration.ofDays(1), setting.error());
        }

        if (input.hasError()) {
            return DelugeProgramOutput.from(array(""), Duration.ofDays(1), input.error());
        }

        return buildOutput();
    }

    private DelugeProgramOutput buildOutput() {
        Exception error = null;
        Charset inEnc = builder.inputCharset();
        Charset outEnc = builder.outputCharset();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream, false, outEnc);

        if (!type.isArgued()) {
            out.println("Argument: <null>");
        }
        else if (setting.argumentArray() == null) {
            // Runner should pass an empty array.
            out.println("Argument: <none>");
        }
        else if (setting.argumentArray().length == 0) {
            out.println("Argument: <none>");
        }
        else {
            for (String argument : setting.argumentArray()) {
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
            try (Scanner scanner = inputStream != null ? newScanner(inputStream, inEnc, outEnc) : new Scanner("")) {
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
        return DelugeProgramOutput.from(lines, Duration.ofDays(1), error);
    }

    private InputStreamSupplier newSupplier() {
        if (ARRAY == input.type()) {
            if (input.array() == null) {
                return () -> new ByteArrayInputStream(new byte[0]);
            }

            Charset charset = builder.inputCharset();
            return input.newInputStreamSupplier(charset);
        }

        return input.streamSupplier();
    }

    private Scanner newScanner(InputStream inputStream, Charset inEnc, Charset outEnc) {
        if (type.isStandard()) {
            return new Scanner(inputStream, builder.inputCharset());
        }
        else if (type.isConsole()) {
            return new Scanner(transcode(inputStream, inEnc, outEnc), outEnc);
        }
        else {
            throw new RuntimeException();
        }
    }

}
