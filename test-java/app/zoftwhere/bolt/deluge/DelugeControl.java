package app.zoftwhere.bolt.deluge;

import java.util.Objects;
import java.util.function.Function;

import app.zoftwhere.bolt.BoltSingleReturn;

class DelugeControl {

    static String runComparison(DelugeProgramOutput expected, DelugeProgramOutput actual) {
        BoltSingleReturn<String> switcher = new BoltSingleReturn<>();

        switcher.block(() -> compareResult(expected, actual, DelugeControl::exceptionClass,
            "deluge.program.exception.expected",
            "deluge.program.exception.found",
            "deluge.program.exception.mismatch"));

        switcher.block(() -> compareResult(expected, actual, DelugeControl::exceptionMessage,
            "deluge.program.exception.message.expected",
            "deluge.program.exception.message.found",
            "deluge.program.exception.message.mismatch"));

        switcher.block(() -> compareResult(expected, actual, DelugeControl::causeClass,
            "deluge.program.exception.cause.expected",
            "deluge.program.exception.cause.found",
            "deluge.program.exception.cause.mismatch"));

        switcher.block(() -> compareResult(expected, actual, DelugeControl::causeMessage,
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

        switcher.block(() -> {
            if (actual.executionDuration() == null) {
                return "deluge.program.found.actual.duration.null";
            }

            if (expected.executionDuration() == null) {
                return "deluge.program.found.expected.duration.null";
            }

            if (expected.executionDuration().compareTo(actual.executionDuration()) < 0) {
                return "deluge.program.found.actual.execution.duration.exceeds.expectation";
            }

            return null;
        });

        return switcher.end();
    }

    private static String compareResult(
        DelugeProgramOutput expected,
        DelugeProgramOutput actual,
        Function<DelugeProgramOutput, String> getter,
        String noActual,
        String noExpected,
        String noMatch
    )
    {
        String expectedString = getter.apply(expected);
        String actualString = getter.apply(actual);
        try {
            if (!Objects.equals(expectedString, actualString)) {
                if (actualString == null) { return noActual; }
                if (expectedString == null) { return noExpected; }
                return noMatch;
            }

            return null;
        }
        catch (Exception e) {
            throw new DelugeException("deluge.comparison.exception", e);
        }
    }

    private static String exceptionClass(DelugeProgramOutput programOutput) {
        if (programOutput.error() == null) { return null; }
        return programOutput.error().getClass().getName();
    }

    private static String exceptionMessage(DelugeProgramOutput programOutput) {
        if (programOutput.error() == null) { return null; }
        return programOutput.error().getMessage();
    }

    private static String causeClass(DelugeProgramOutput programOutput) {
        if (programOutput.error() == null) { return null; }
        if (programOutput.error().getCause() == null) { return null; }
        return programOutput.error().getCause().getClass().getName();
    }

    private static String causeMessage(DelugeProgramOutput programOutput) {
        if (programOutput.error() == null) { return null; }
        if (programOutput.error().getCause() == null) { return null; }
        return programOutput.error().getCause().getMessage();
    }

}
