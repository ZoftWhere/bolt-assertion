package app.zoftwhere.bolt.deluge;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import app.zoftwhere.bolt.BoltLineScanner;
import app.zoftwhere.bolt.BoltSingleReturn;

import static app.zoftwhere.bolt.BoltLineScanner.escapeString;
import static app.zoftwhere.bolt.BoltTestHelper.array;
import static app.zoftwhere.bolt.BoltTestHelper.isOrHasNull;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.ARRAY;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.RESOURCE;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.RESOURCE_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.STREAM;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.STREAM_ENCODED;
import static java.nio.charset.StandardCharsets.UTF_8;

class DelugeControl {

    private final DelugeData data;

    private final DelugeSettings settings;

    private final DelugeProgramType programType;

    static DelugeControl from(DelugeProgramType programType, DelugeSettings settings, DelugeData data) {
        return new DelugeControl(programType, settings, data);
    }

    private DelugeControl(DelugeProgramType programType, DelugeSettings settings, DelugeData data) {
        this.programType = programType;
        this.data = data;
        this.settings = settings;
    }

    void runTest() {
        DelugeProgram program = DelugeProgram.from(programType, data, settings);

        if (data.streamSupplier() != null) {
            data.resetFlags();
        }

        DelugeResult actual = program.buildProgramResult();
        DelugeResult expected = buildExpectation();

        BoltSingleReturn<String> switcher = new BoltSingleReturn<>();

        switcher.block(() -> {
            if (data.streamSupplier() == null) {
                return null;
            }

            if (data.isOpened()) {
                return data.isClosed() ? null : "deluge.program.data.input.stream.auto.closing";
            }
            else {
                return !data.isClosed() ? null : "deluge.program.data.input.stream.auto.closing.unopened";
            }
        }).block(
            () -> runComparison(expected, actual)
        );

        String message = switcher.end();
        if (message != null) {
            throw new DelugeException(message, actual.exception());
        }
    }

    private String runComparison(DelugeResult expected, DelugeResult actual) {
        BoltSingleReturn<String> switcher = new BoltSingleReturn<>();

        switcher.block(() -> compareResult(expected, actual, DelugeResult::exceptionClass,
            "deluge.program.exception.expected",
            "deluge.program.exception.found",
            "deluge.program.exception.mismatch"));

        switcher.block(() -> compareResult(expected, actual, DelugeResult::exceptionMessage,
            "deluge.program.exception.message.expected",
            "deluge.program.exception.message.found",
            "deluge.program.exception.message.mismatch"));

        switcher.block(() -> compareResult(expected, actual, DelugeResult::causeClass,
            "deluge.program.exception.cause.expected",
            "deluge.program.exception.cause.found",
            "deluge.program.exception.cause.mismatch"));

        switcher.block(() -> compareResult(expected, actual, DelugeResult::causeMessage,
            "deluge.program.exception.cause.message.expected",
            "deluge.program.exception.cause.message.found",
            "deluge.program.exception.cause.message.mismatch"));

        switcher.block(() -> {
            if (expected.output() == null) {
                return "deluge.program.expectation.output.null";
            }

            if (actual.output() == null) {
                return "deluge.program.actual.output.null";
            }

            if (expected.output().length != actual.output().length) {
                return "deluge.program.check.length.mismatch";
            }

            for (int s = expected.output().length, i = 0; i < s; i++) {
                if (!Objects.equals(expected.output()[i], actual.output()[i])) {
                    return String.format("deluge.program.check.comparison.failed[%d]", i);
                }
            }
            return null;
        });

        return switcher.end();
    }

    private String compareResult(DelugeResult expected, DelugeResult actual, Function<DelugeResult, String> getter,
        String noActual, String noExpected, String noMatch)
    {
        String expectedString = getter.apply(expected);
        String actualString = getter.apply(actual);
        try {
            if (expectedString != null && actualString == null) {
                return noActual;
            }
            if (expectedString == null && actualString != null) {
                return noExpected;
            }
            if (expectedString != null) {
                if (!Objects.equals(expectedString, actualString)) {
                    return noMatch;
                }
            }

            return null;
        }
        catch (Throwable throwable) {
            throw new DelugeException("deluge.comparison.exception", throwable);
        }
    }

    private DelugeResult buildExpectation() {

        if (STREAM_ENCODED == data.type() || RESOURCE_ENCODED == data.type()) {
            if (data.charset() == null) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.input.charset.null";
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, null);
            }
        }

        if (settings.hasCharSet() && settings.charset() == null) {
            String exceptionClass = "app.zoftwhere.bolt.RunnerException";
            String exceptionMessage = "bolt.runner.output.charset.null";
            return new DelugeResult(array(""), exceptionClass, exceptionMessage, null);
        }

        if (ARRAY == data.type()) {
            if (data.array() != null && isOrHasNull(data.array())) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.variable.array.input.has.null";
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, null);
            }
        }

        if (RESOURCE == data.type() || RESOURCE_ENCODED == data.type()) {
            if (data.resource() == null) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.load.input.resource.name.null";
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, null);
            }

            if (data.withClass() == null) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.load.input.resource.class.null";
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, null);
            }

            final var url = data.withClass().getResource(data.resource());
            if (url == null) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.load.input.resource.not.found";
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, null);
            }
        }

        if (STREAM == data.type() || STREAM_ENCODED == data.type() || RESOURCE == data.type() ||
            RESOURCE_ENCODED == data.type()) {
            if (data.array() == null) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.load.input.input.stream.null";
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, null);
            }
        }

        if (settings.hasThrowable()) {
            if (settings.throwable() instanceof Exception) {
                return new DelugeResult(array(""), (Exception) settings.throwable());
            }

            String exceptionClass = "app.zoftwhere.bolt.RunnerException";
            String exceptionMessage = "bolt.runner.throwable.as.cause";
            Throwable cause = settings.throwable();
            return new DelugeResult(array(""), exceptionClass, exceptionMessage, cause);
        }

        List<String> output = new ArrayList<>();
        output.addAll(forArgument());
        output.addAll(forInput());
        String[] array = output.toArray(new String[0]);

        return new DelugeResult(array);
    }

    private List<String> forArgument() {
        List<String> list = new ArrayList<>();
        if (settings.hasThrowable()) {
            return list;
        }

        if (!programType.isArgued()) {
            list.add("Argument: <null>");
        }
        else if (settings.argumentArray() == null) {
            // Runner should pass an empty array.
            list.add("Argument: <none>");
        }
        else if (settings.argumentArray().length == 0) {
            list.add("Argument: <none>");
        }
        else {
            for (String argument : settings.argumentArray()) {
                if (argument == null) {
                    list.add("Argument: <null>");
                }
                else {
                    list.add(String.format("Argument: \"%s\"", escapeString(argument)));
                }
            }
        }
        return list;
    }

    private List<String> forInput() {
        List<String> list = new ArrayList<>();
        if (settings.hasThrowable()) {
            return list;
        }
        else if (data.array() == null || isOrHasNull(data.array())) {
            list.add("Line: \"\"");
            return list;
        }
        else if (data.array().length == 0) {
            list.add("Line: \"\"");
            return list;
        }
        else {
            try (InputStream inputStream = DelugeData.newInputStreamSupplier(data.array()).get()) {
                try (BoltLineScanner scanner = new BoltLineScanner(inputStream, UTF_8)) {
                    String item = scanner.firstLine();
                    list.add(String.format("Line: \"%s\"", escapeString(item)));
                    while (scanner.hasNextLine()) {
                        item = scanner.nextLine();
                        list.add(String.format("Line: \"%s\"", escapeString(item)));
                    }
                }
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new RuntimeException(throwable);
            }
            return list;
        }
    }

}
