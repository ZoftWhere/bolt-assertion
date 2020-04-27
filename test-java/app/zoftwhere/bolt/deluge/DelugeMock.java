package app.zoftwhere.bolt.deluge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Iterator;

import app.zoftwhere.bolt.BoltTestHelper;
import app.zoftwhere.bolt.RunnerException;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;

import static app.zoftwhere.bolt.BoltTestHelper.array;
import static app.zoftwhere.bolt.BoltTestHelper.arrayHasNull;
import static app.zoftwhere.bolt.BoltTestHelper.newStringIterator;
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
            var exceptionMessage = "bolt.runner.input.charset.null";
            var error = new RunnerException(exceptionMessage, null);
            return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
        }

        if (setting.hasCharSet() && setting.charset() == null) {
            var exceptionMessage = "bolt.runner.output.charset.null";
            var error = new RunnerException(exceptionMessage, null);
            return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
        }

        if (ARRAY == input.type() || ARRAY_ENCODED == input.type()) {
            if (input.array() == null) {
                var exceptionMessage = "bolt.runner.variable.argument.input.null";
                var error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }

            if (arrayHasNull(input.array())) {
                var exceptionMessage = "bolt.runner.variable.argument.input.has.null";
                var error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }
        }
        else if (RESOURCE == input.type() || RESOURCE_ENCODED == input.type()) {
            if (input.resource() == null) {
                var exceptionMessage = "bolt.runner.load.input.resource.name.null";
                var error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }

            if (input.withClass() == null) {
                var exceptionMessage = "bolt.runner.load.input.resource.class.null";
                var error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }

            final var url = input.withClass().getResource(input.resource());
            if (url == null) {
                var exceptionMessage = "bolt.runner.load.input.resource.not.found";
                var error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }
        }
        else if (STREAM == input.type() || STREAM_ENCODED == input.type()) {
            if (input.error() != null) {
                return DelugeProgramOutput.from(array(""), Duration.ZERO, input.error());
            }

            if (input.array() == null) {
                var exceptionMessage = "bolt.runner.input.stream.supplier.null";
                var error = new RunnerException(exceptionMessage, null);
                return DelugeProgramOutput.from(array(""), Duration.ZERO, error);
            }

            if (arrayHasNull(input.array())) {
                var exceptionMessage = "bolt.runner.load.input.input.stream.null";
                var error = new RunnerException(exceptionMessage, null);
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
        var error = (Exception) null;
        var inEnc = builder.inputCharset();
        var outEnc = builder.outputCharset();
        var outputStream = new ByteArrayOutputStream();
        var out = new PrintStream(outputStream, false, outEnc);

        if (!type.isArgued()) {
            out.print("Argument: <null>\n");
        }
        else if (setting.argumentArray() == null) {
            // Runner should pass an empty array.
            out.print("Argument: <none>\n");
        }
        else if (setting.argumentArray().length == 0) {
            out.print("Argument: <none>\n");
        }
        else {
            for (var argument : setting.argumentArray()) {
                out.print("Argument: " + escapeString(argument) + "\n");
            }
        }

        var supplier = newSupplier();
        try (var inputStream = supplier != null ? supplier.get() : null) {
            var iterator = newIterator(inputStream, inEnc, outEnc);
            out.print("Line: " + escapeString(iterator.next()));
            while (iterator.hasNext()) {
                out.print("\nLine: " + escapeString(iterator.next()));
            }
        }
        catch (Exception e) {
            error = e;
        }

        var data = outputStream.toByteArray();

        var lines = readArray(data, outEnc);
        return DelugeProgramOutput.from(lines, Duration.ofDays(1), error);
    }

    private InputStreamSupplier newSupplier() {
        if (ARRAY == input.type()) {
            if (input.array() == null) {
                return () -> new ByteArrayInputStream(new byte[0]);
            }

            var charset = builder.inputCharset();
            return input.newInputStreamSupplier(charset);
        }

        return input.streamSupplier();
    }

    private Iterator<String> newIterator(InputStream inputStream, Charset inEnc, Charset outEnc) {
        if (type.isStandard()) {
            return newStringIterator(inputStream, builder.inputCharset());
        }
        else if (type.isConsole()) {
            return newStringIterator(transcode(inputStream, inEnc, outEnc), outEnc);
        }
        else {
            throw new RuntimeException();
        }
    }

    private static String escapeString(String string) {
        if (string == null) {
            return "<null>";
        }
        else {
            return "\"" + BoltTestHelper.escapeString(string) + "\"";
        }
    }

}
