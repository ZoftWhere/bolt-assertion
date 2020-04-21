package app.zoftwhere.bolt.deluge;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Scanner;

import app.zoftwhere.bolt.BoltTestHelper;
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
import static app.zoftwhere.bolt.deluge.DelugeDataType.ARRAY;
import static app.zoftwhere.bolt.deluge.DelugeDataType.RESOURCE;
import static app.zoftwhere.bolt.deluge.DelugeDataType.RESOURCE_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeDataType.STREAM;
import static app.zoftwhere.bolt.deluge.DelugeDataType.STREAM_ENCODED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_CONSOLE;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_CONSOLE_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_STANDARD;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.INPUT_STANDARD_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_CONSOLE;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_CONSOLE_ARGUED;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_STANDARD;
import static app.zoftwhere.bolt.deluge.DelugeProgramType.PROGRAM_STANDARD_ARGUED;

class DelugeProgram {

    static DelugeProgram from(DelugeProgramType programType, DelugeSettings settings, DelugeData data) {
        return new DelugeProgram(programType, settings, data);
    }

    private final DelugeBuilder configuration;

    private final DelugeProgramType type;

    private final DelugeSettings setting;

    private final DelugeData input;

    private DelugeProgram(DelugeProgramType type, DelugeSettings setting, DelugeData input) {
        this.configuration = DelugeBuilder.from(type, setting, input);
        this.type = type;
        this.input = input;
        this.setting = setting;

        if (type.isArgued() && !setting.hasArgumentArray()) {
            throw new IllegalArgumentException("deluge.no.argument.for.argued.type");
        }
        if (!type.isArgued() && setting.hasArgumentArray()) {
            throw new IllegalArgumentException("deluge.program.found.argument.for.argument-free.type");
        }
    }

    DelugeResult buildActualResult() {
        if (type.isProgramFirst() && !type.isInputFirst()) {
            if (setting.hasEncoding()) {
                return testProgramFirst(newRunner().encoding(setting.defaultEncoding()));
            }
            else {
                return testProgramFirst(newRunner());
            }
        }
        else if (type.isInputFirst() && !type.isProgramFirst()) {
            if (setting.hasEncoding()) {
                return testInputFirst(newRunner().encoding(setting.defaultEncoding()));
            }
            else {
                return testInputFirst(newRunner());
            }
        }
        else {
            throw new DelugeException("deluge.program.program.type.exclusion");
        }
    }

    private DelugeResult testProgramFirst(RunnerProvideProgram runner) {
        if (PROGRAM_STANDARD == type) {
            RunStandard program = (scanner, out) -> callStandard(null, scanner, out);

            if (setting.hasCharSet()) {
                return testProgramInput(runner.run(setting.charset(), program));
            }
            else {
                return testProgramInput(runner.run(program));
            }
        }
        else if (PROGRAM_CONSOLE == type) {
            RunConsole program = (inputStream, outputStream) -> callConsole(null, inputStream, outputStream);

            if (setting.hasCharSet()) {
                return testProgramInput(runner.runConsole(setting.charset(), program));
            }
            else {
                return testProgramInput(runner.runConsole(program));
            }
        }
        else if (PROGRAM_STANDARD_ARGUED == type) {
            RunStandardArgued program = this::callStandard;

            if (setting.hasCharSet()) {
                return testProgramArgument(runner.run(setting.charset(), program));
            }
            else {
                return testProgramArgument(runner.run(program));
            }
        }
        else if (PROGRAM_CONSOLE_ARGUED == type) {
            RunConsoleArgued program = this::callConsole;

            if (setting.hasCharSet()) {
                return testProgramArgument(runner.runConsole(setting.charset(), program));
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
        return testProgramInput(preProgram.argument(setting.argumentArray()));
    }

    private DelugeResult testProgramInput(RunnerProgram program) {
        if (ARRAY == input.type()) {
            return buildOutput(program.input(input.array()));
        }
        else if (STREAM == input.type()) {
            return buildOutput(program.input(input.streamSupplier()));
        }
        else if (STREAM_ENCODED == input.type()) {
            return buildOutput(program.input(input.streamSupplier(), input.charset()));
        }
        else if (RESOURCE == input.type()) {
            return buildOutput(program.loadInput(input.resource(), input.withClass()));
        }
        else if (RESOURCE_ENCODED == input.type()) {
            return buildOutput(program.loadInput(input.resource(), input.withClass(), input.charset()));
        }
        else {
            throw new IllegalArgumentException("deluge.data.type.expected");
        }
    }

    private DelugeResult testInputFirst(RunnerProvideInput runner) {
        if (ARRAY == input.type()) {
            return testProgramInput(runner.input(input.array()));
        }
        else if (STREAM == input.type()) {
            return testProgramInput(runner.input(input.streamSupplier()));
        }
        else if (STREAM_ENCODED == input.type()) {
            return testProgramInput(runner.input(input.streamSupplier(), input.charset()));
        }
        else if (RESOURCE == input.type()) {
            return testProgramInput(runner.loadInput(input.resource(), input.withClass()));
        }
        else if (RESOURCE_ENCODED == input.type()) {
            return testProgramInput(runner.loadInput(input.resource(), input.withClass(), input.charset()));
        }
        else {
            throw new IllegalArgumentException("deluge.data.type.expected");
        }
    }

    private DelugeResult testProgramInput(RunnerProgramInput next) {
        if (type.isArgued()) {
            return testProgramWithArguments(next.argument(setting.argumentArray()));
        }
        else {
            return testProgramNoArguments(next);
        }
    }

    private DelugeResult testProgramNoArguments(RunnerProgramInput runner) {
        if (INPUT_STANDARD == type) {
            RunStandard program = (scanner, out) -> callStandard(null, scanner, out);

            if (setting.hasCharSet()) {
                return buildOutput(runner.run(setting.charset(), program));
            }
            else {
                return buildOutput(runner.run(program));
            }
        }
        else if (INPUT_CONSOLE == type) {
            RunConsole program = (inputStream, outputStream) -> callConsole(null, inputStream, outputStream);

            if (setting.hasCharSet()) {
                return buildOutput(runner.runConsole(setting.charset(), program));
            }
            else {
                return buildOutput(runner.runConsole(program));
            }
        }
        else {
            throw new IllegalArgumentException("deluge.program.first.expected");
        }
    }

    private DelugeResult testProgramWithArguments(RunnerLoader runner) {
        if (INPUT_STANDARD_ARGUED == type) {
            RunStandardArgued program = this::callStandard;

            if (setting.hasCharSet()) {
                return buildOutput(runner.run(setting.charset(), program));
            }
            else {
                return buildOutput(runner.run(program));
            }
        }
        else if (INPUT_CONSOLE_ARGUED == type) {
            RunConsoleArgued program = this::callConsole;

            if (setting.hasCharSet()) {
                return buildOutput(runner.runConsole(setting.charset(), program));
            }
            else {
                return buildOutput(runner.runConsole(program));
            }
        }
        else {
            throw new IllegalArgumentException("deluge.input.first.expected");
        }
    }

    private DelugeResult buildOutput(RunnerProgramOutput programOutput) {
        String[] output = programOutput.output();
        Duration duration = programOutput.executionDuration();
        Exception error = programOutput.error().orElse(null);
        return new DelugeResult(output, duration, error);
    }

    private void callConsole(String[] arguments, InputStream inputStream, OutputStream outputStream) throws Exception {
        if (setting.hasError()) {
            throw setting.error();
        }

        Charset outputCharset = configuration.outputCharset();

        try (DelugeLineScanner scanner = new DelugeLineScanner(inputStream, outputCharset)) {
            try (PrintStream out = new PrintStream(outputStream, false, outputCharset)) {
                processCall(arguments, scanner, out);
            }
        }
    }

    private void callStandard(String[] arguments, Scanner scanner, PrintStream out) throws Exception {
        if (setting.hasError()) {
            throw setting.error();
        }

        processCall(arguments, new DelugeLineScanner(scanner), out);
    }

    private void processCall(String[] arguments, DelugeLineScanner scanner, PrintStream out) throws Exception {
        if (setting.hasError()) {
            throw setting.error();
        }

        if (arguments == null) {
            out.println("Argument: <null>");
        }
        else if (arguments.length == 0) {
            out.println("Argument: <none>");
        }
        else {
            out.printf("Argument: %s", escapeString(arguments[0]));
            out.println();

            for (int i = 1, s = arguments.length; i < s; i++) {
                out.printf("Argument: %s", escapeString(arguments[i]));
                out.println();
            }
        }

        String line = scanner.firstLine();
        out.printf("Line: %s", escapeString(line));

        while (scanner.hasMore()) {
            line = scanner.readLine();
            out.println();
            out.printf("Line: %s", escapeString(line));
        }
    }

    private String escapeString(String value) {
        if (value == null) {
            return "<null>";
        }
        return '"' + BoltTestHelper.escapeString(value) + '"';
    }

}
