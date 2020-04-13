package app.zoftwhere.bolt.deluge;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerProvideInput;
import app.zoftwhere.bolt.api.RunnerProvideProgram;

import static app.zoftwhere.bolt.Runner.newRunner;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.ARRAY;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.RESOURCE;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.RESOURCE_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.STREAM;
import static app.zoftwhere.bolt.deluge.DelugeData.DataType.STREAM_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_CONSOLE;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_CONSOLE_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_STANDARD;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_STANDARD_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_CONSOLE;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_CONSOLE_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_STANDARD;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_STANDARD_ARGUED;

class DelugeProgram {

    private final DelugeProgramType type;

    private final DelugeData data;

    private final DelugeSettings settings;

    static DelugeProgram from(DelugeProgramType type, DelugeData data, DelugeSettings settings) {
        return new DelugeProgram(type, data, settings);
    }

    private DelugeProgram(DelugeProgramType type, DelugeData data, DelugeSettings settings) {
        this.type = type;
        this.data = data;
        this.settings = settings;

        if (type.isArgued() && !settings.hasArgumentArray()) {
            throw new IllegalArgumentException("deluge.no.argument.for.argued.type");
        }
        if (!type.isArgued() && settings.hasArgumentArray()) {
            throw new IllegalArgumentException("deluge.program.found.argument.for.argument-free.type");
        }
    }

    DelugeResult buildProgramResult() {
        if (type.isProgramFirst() && !type.isInputFirst()) {
            return testProgramFirst(newRunner());
        }
        else if (type.isInputFirst() && !type.isProgramFirst()) {
            return testInputFirst(newRunner());
        }
        else {
            throw new DelugeException("deluge.program.program.type.exclusion");
        }
    }

    private DelugeResult testProgramFirst(RunnerProvideProgram runner) {
        if (PROGRAM_STANDARD == type) {
            RunStandard program = (scanner, out) -> process(null, scanner, out);

            if (settings.hasCharSet()) {
                return testProgramInput(runner.run(settings.charset(), program));
            }
            else {
                return testProgramInput(runner.run(program));
            }
        }
        else if (PROGRAM_CONSOLE == type) {
            RunConsole program = ((inputStream, outputStream) -> process(null, inputStream, outputStream));

            if (settings.hasCharSet()) {
                return testProgramInput(runner.runConsole(settings.charset(), program));
            }
            else {
                return testProgramInput(runner.runConsole(program));
            }
        }
        else if (PROGRAM_STANDARD_ARGUED == type) {
            RunStandardArgued program = (this::process);

            if (settings.hasCharSet()) {
                return testProgramArgument(runner.run(settings.charset(), program));
            }
            else {
                return testProgramArgument(runner.run(program));
            }
        }
        else if (PROGRAM_CONSOLE_ARGUED == type) {
            RunConsoleArgued program = (this::process);

            if (settings.hasCharSet()) {
                return testProgramArgument(runner.runConsole(settings.charset(), program));
            }
            else {
                return testProgramArgument(runner.runConsole(program));
            }
        }
        else {
            throw new IllegalArgumentException("deluge.program.first.expected");
        }
    }

    private DelugeResult testProgramArgument(RunnerPreProgram preProgram) {
        return testProgramInput(preProgram.argument(settings.argumentArray()));
    }

    private DelugeResult testProgramInput(RunnerProgram program) {
        if (ARRAY == data.type()) {
            return outputToResult(program.input(data.array()));
        }
        else if (STREAM == data.type()) {
            return outputToResult(program.input(data.streamSupplier()));
        }
        else if (STREAM_ENCODED == data.type()) {
            return outputToResult(program.input(data.streamSupplier(), data.charset()));
        }
        else if (RESOURCE == data.type()) {
            return outputToResult(program.loadInput(data.resource(), data.withClass()));
        }
        else if (RESOURCE_ENCODED == data.type()) {
            return outputToResult(program.loadInput(data.resource(), data.withClass(), data.charset()));
        }
        else {
            throw new IllegalArgumentException("deluge.data.type.expected");
        }
    }

    private DelugeResult testInputFirst(RunnerProvideInput runner) {
        if (ARRAY == data.type()) {
            return testProgramInput(runner.input(data.array()));
        }
        else if (STREAM == data.type()) {
            return testProgramInput(runner.input(data.streamSupplier()));
        }
        else if (STREAM_ENCODED == data.type()) {
            return testProgramInput(runner.input(data.streamSupplier(), data.charset()));
        }
        else if (RESOURCE == data.type()) {
            return testProgramInput(runner.loadInput(data.resource(), data.withClass()));
        }
        else if (RESOURCE_ENCODED == data.type()) {
            return testProgramInput(runner.loadInput(data.resource(), data.withClass(), data.charset()));
        }
        else {
            throw new IllegalArgumentException("deluge.data.type.expected");
        }
    }

    private DelugeResult testProgramInput(RunnerProgramInput next) {
        if (type.isArgued()) {
            return testProgramWithArguments(next.argument(settings.argumentArray()));
        }
        else {
            return testProgramNoArguments(next);
        }
    }

    private DelugeResult testProgramNoArguments(RunnerProgramInput runner) {
        if (INPUT_STANDARD == type) {
            RunStandard program = (scanner, out) -> process(null, scanner, out);

            if (settings.hasCharSet()) {
                return outputToResult(runner.run(settings.charset(), program));
            }
            else {
                return outputToResult(runner.run(program));
            }
        }
        else if (INPUT_CONSOLE == type) {
            RunConsole program = ((inputStream, outputStream) -> process(null, inputStream, outputStream));

            if (settings.hasCharSet()) {
                return outputToResult(runner.runConsole(settings.charset(), program));
            }
            else {
                return outputToResult(runner.runConsole(program));
            }
        }
        else {
            throw new IllegalArgumentException("deluge.program.first.expected");
        }
    }

    private DelugeResult testProgramWithArguments(RunnerLoader runner) {
        if (INPUT_STANDARD_ARGUED == type) {
            RunStandardArgued program = (this::process);

            if (settings.hasCharSet()) {
                return outputToResult(runner.run(settings.charset(), program));
            }
            else {
                return outputToResult(runner.run(program));
            }
        }
        else if (INPUT_CONSOLE_ARGUED == type) {
            RunConsoleArgued program = (this::process);

            if (settings.hasCharSet()) {
                return outputToResult(runner.runConsole(settings.charset(), program));
            }
            else {
                return outputToResult(runner.runConsole(program));
            }
        }
        else {
            throw new IllegalArgumentException("deluge.input.first.expected");
        }
    }

    private DelugeResult outputToResult(RunnerProgramOutput preTest) {
        if (preTest.exception().isPresent()) {
            return new DelugeResult(preTest.output(), preTest.exception().get());
        }
        return new DelugeResult(preTest.output());
    }

    private void process(String[] arguments, InputStream inputStream, OutputStream outputStream) throws Throwable {
        if (settings.hasThrowable()) {
            throw settings.throwable();
        }

        //noinspection CaughtExceptionImmediatelyRethrown
        try (DelugeLineScanner scanner = new DelugeLineScanner(inputStream, settings.charset())) {
            try (PrintStream out = new PrintStream(outputStream, false, settings.charset())) {
                process(arguments, scanner, out);
            }
        }
        catch (Throwable throwable) {
            throw throwable;
        }
    }

    private void process(String[] arguments, Scanner scanner, PrintStream out) throws Throwable {
        process(arguments, new DelugeLineScanner(scanner), out);
    }

    private void process(String[] arguments, DelugeLineScanner scanner, PrintStream out) throws Throwable {
        if (settings.hasThrowable()) {
            throw settings.throwable();
        }

        if (arguments == null) {
            out.print("Argument: <null>");
        }
        else if (arguments.length == 0) {
            out.print("Argument: <none>");
        }
        else {
            out.printf("Argument: %s", escapeString(arguments[0]));

            for (int i = 1, s = arguments.length; i < s; i++) {
                out.println();
                out.printf("Argument: %s", escapeString(arguments[i]));
            }
        }

        String line = scanner.firstLine();
        out.println();
        out.printf("Line: %s", escapeString(line));

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            out.println();
            out.printf("Line: %s", escapeString(line));
        }
    }

    private String escapeString(String value) {
        if (value == null) {
            return "<null>";
        }
        return '"' + DelugeLineScanner.escapeString(value) + '"';
    }

}
