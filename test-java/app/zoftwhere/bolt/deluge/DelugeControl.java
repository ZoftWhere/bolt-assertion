package app.zoftwhere.bolt.deluge;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import app.zoftwhere.bolt.BoltLineScanner;
import app.zoftwhere.bolt.BoltSingleReturn;
import app.zoftwhere.bolt.deluge.DelugeProgram.ProgramType;

import static app.zoftwhere.bolt.BoltTestHelper.array;
import static app.zoftwhere.bolt.BoltLineScanner.escapeString;
import static app.zoftwhere.bolt.BoltTestHelper.isOrHasNull;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.ARRAY;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.RESOURCE;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.RESOURCE_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.STREAM;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.STREAM_ENCODED;
import static java.nio.charset.StandardCharsets.UTF_8;

@SuppressWarnings("ALL")
public
class DelugeControl {

    private final DelugeData data;

    private final DelugeSettings settings;

    private final ProgramType programType;

    public static DelugeControl from(ProgramType programType, DelugeData data, DelugeSettings settings) {
        return new DelugeControl(programType, data, settings);
    }

    private DelugeControl(ProgramType programType, DelugeData data, DelugeSettings settings) {
        this.programType = programType;
        this.data = data;
        this.settings = settings;
    }

    public void runTest() {
        DelugeProgram program = DelugeProgram.from(programType, data, settings);

        if (data.stream() != null) {
            data.resetFlags();
        }

        DelugeResult actual = program.buildProgramResult();
        DelugeResult expected = buildExpectation(actual);

        BoltSingleReturn<String> switcher = new BoltSingleReturn<>();

        switcher.block(() -> {
            if (data.stream() == null) {
                return null;
            }

            if (data.isOpened()) {
                return data.isClosed() ? null : "deluge.program.data.input.stream.auto.closing";
            }
            else {
                return !data.isClosed() ? null : "deluge.program.data.input.stream.auto.closing.unopened";
            }
        });

        switcher.block(() -> {
            return runComparison(expected, actual);
        });

        String message = switcher.end();
        if (message != null) {
            program.buildProgramResult();
            throw new DelugeException(message, actual.exception());
        }
    }

    private String runComparison(DelugeResult expected, DelugeResult actual) {
        BoltSingleReturn<String> switcher = new BoltSingleReturn<String>();
        switcher.block(() -> {
            return compareException(expected, actual, DelugeResult::exceptionClass,
                "deluge.program.exception.expected",
                "deluge.program.exception.found",
                "deluge.program.exception.mismatch");
        });
        switcher.block(() -> {
            return compareException(expected, actual, DelugeResult::exceptionMessage,
                "deluge.program.exception.message.expected",
                "deluge.program.exception.message.found",
                "deluge.program.exception.message.mismatch");
        });
        switcher.block(() -> {
            return compareException(expected, actual, DelugeResult::causeClass,
                "deluge.program.exception.cause.expected",
                "deluge.program.exception.cause.found",
                "deluge.program.exception.cause.mismatch");
        });
        switcher.block(() -> {
            return compareException(expected, actual, DelugeResult::causeMessage,
                "deluge.program.exception.cause.message.expected",
                "deluge.program.exception.cause.message.found",
                "deluge.program.exception.cause.message.mismatch");
        });
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

    private String compareException(DelugeResult expected, DelugeResult actual, Function<DelugeResult, String> getter,
        String noActual, String noExpected, String noMatch)
    {
        String expectedString = getter.apply(expected);
        String actualString = getter.apply(actual);
        try {
            if (expectedString != null && actualString == null) {
                //consumer.accept(noActual);
                return noActual;
            }
            if (expectedString == null && actualString != null) {
                //consumer.accept(noExpected);
                return noExpected;
            }
            if (expectedString != null && actualString != null) {
                if (Objects.equals(expectedString, actualString) == false) {
                    //consumer.accept(noMatch);
                    return noMatch;
                }
            }

            return null;
        }
        catch (Throwable throwable) {
            throw new DelugeException(throwable.getMessage(), throwable.getCause());
        }
    }

    @SuppressWarnings("ConstantConditions")
    private DelugeResult buildExpectation(DelugeResult actual) {

        if (STREAM_ENCODED == data.type() || RESOURCE_ENCODED == data.type()) {
            if (data.charset() == null) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.input.charset.null";
                Throwable cause = null;
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, cause);
            }
        }

        if (settings.hasCharSet() && settings.charset() == null) {
            String exceptionClass = "app.zoftwhere.bolt.RunnerException";
            String exceptionMessage = "bolt.runner.output.charset.null";
            Throwable cause = null;
            return new DelugeResult(array(""), exceptionClass, exceptionMessage, cause);
        }

        if (ARRAY == data.type()) {
            if (data.array() != null && isOrHasNull(data.array())) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.variable.array.input.has.null";
                Throwable cause = null;
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, cause);
            }
        }

        if (RESOURCE == data.type() || RESOURCE_ENCODED == data.type()) {
            if (data.resource() == null) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.load.input.resource.name.null";
                Throwable cause = null;
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, cause);
            }

            if (data.withClass() == null) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.load.input.resource.class.null";
                Throwable cause = null;
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, cause);
            }

            final var url = data.withClass().getResource(data.resource());
            if (url == null) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.load.input.input.stream.null";
                Throwable cause = null;
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, cause);
            }
        }

        if (STREAM == data.type() || STREAM_ENCODED == data.type() || RESOURCE == data.type() ||
            RESOURCE_ENCODED == data.type()) {
            if (data.array() == null) {
                String exceptionClass = "app.zoftwhere.bolt.RunnerException";
                String exceptionMessage = "bolt.runner.load.input.input.stream.null";
                Throwable cause = null;
                return new DelugeResult(array(""), exceptionClass, exceptionMessage, cause);
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

        if (!DelugeProgram.hasArgument(programType)) {
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
