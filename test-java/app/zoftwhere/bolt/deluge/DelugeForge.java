package app.zoftwhere.bolt.deluge;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

import app.zoftwhere.bolt.BoltPlaceHolder;
import app.zoftwhere.bolt.BoltTestHelper;
import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;

import static app.zoftwhere.bolt.BoltTestHelper.newStringIterator;
import static app.zoftwhere.bolt.Runner.DEFAULT_ENCODING;
import static app.zoftwhere.bolt.deluge.DelugeDataType.ARRAY;
import static app.zoftwhere.bolt.deluge.DelugeDataType.ARRAY_ENCODED;
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

class DelugeForge {

    private final Runner runner = Runner.newRunner();

    private final List<Charset> encodingList;

    private final List<DelugeSetting> settingList;

    private final List<DelugeData> inputList;

    private final BoltPlaceHolder<Exception> globalError = new BoltPlaceHolder<>(null);

    private final BoltPlaceHolder<Charset> outEnc = new BoltPlaceHolder<>(null);

    private final RunStandardArgued runStandardArgued = (arguments, scanner, out) -> {
        if (globalError.get() != null) {
            throw globalError.get();
        }

        if (arguments == null) {
            out.print("Argument: <null>\n");
        }
        else if (arguments.length == 0) {
            out.print("Argument: <none>\n");
        }
        else {
            for (var argument : arguments) {
                out.print("Argument: " + escapeString(argument) + "\n");
            }
        }

        final var iterator = newStringIterator(scanner);
        out.print("Line: " + escapeString(iterator.next()));
        while (iterator.hasNext()) {
            out.print("\nLine: " + escapeString(iterator.next()));
        }
    };

    private final RunStandard runStandard = (scanner, out) ->
        runStandardArgued.call(null, scanner, out);

    private final RunConsoleArgued runConsoleArgued = (arguments, inputStream, outputStream) -> {
        if (globalError.get() != null) {
            throw globalError.get();
        }

        var charset = outEnc.get();
        try (var out = new PrintStream(outputStream, false, charset)) {
            if (arguments == null) {
                out.print("Argument: <null>\n");
            }
            else if (arguments.length == 0) {
                out.print("Argument: <none>\n");
            }
            else {
                for (var argument : arguments) {
                    out.print("Argument: " + escapeString(argument) + "\n");
                }
            }
            final var stringIterator = newStringIterator(inputStream, charset);
            out.print("Line: " + escapeString(stringIterator.next()));
            while (stringIterator.hasNext()) {
                out.print("\nLine: " + escapeString(stringIterator.next()));
            }
        }
    };

    private final RunConsole runConsole = (inputStream, outputStream) ->
        runConsoleArgued.call(null, inputStream, outputStream);

    DelugeForge(List<Charset> encodingList, List<DelugeSetting> settingList, List<DelugeData> inputList) {
        this.encodingList = encodingList;
        this.settingList = settingList;
        this.inputList = inputList;
    }

    int runTest() {
        return programFirst() + inputFirst();
    }

    private int programFirst() {
        var count = 0;

        count += programFirst(runner, false, DEFAULT_ENCODING);
        for (var encoding : encodingList) {
            count += programFirst(runner.encoding(encoding), true, encoding);
        }

        return count;
    }

    private int programFirst(RunnerInterface runner, boolean withEncoding, Charset encoding) {
        var count = 0;
        var typeArray = DelugeProgramType.values();
        for (var type : typeArray) {
            if (!type.isProgramFirst()) {
                continue;
            }
            for (var programSetting : settingList) {
                if (type.isArgued() != programSetting.hasArgumentArray()) {
                    continue;
                }
                globalError.set(programSetting.error());

                var program = forProgram(runner, type, programSetting);

                for (var input : inputList) {
                    var builder = DelugeBuilder.from(type, programSetting, input);
                    if (withEncoding) {
                        builder = builder.withEncoding(encoding);
                    }
                    outEnc.set(builder.outputCharset());

                    var programOutput = withInput(program, input);
                    var actual = DelugeProgramOutput.from(programOutput);
                    var expected = builder.buildExpectedOutput();
                    var message = DelugeBuilder.runComparison(expected, actual);
                    if (message != null) {
                        throw new DelugeException(message, null);
                    }
                    count++;
                }
            }
        }
        return count;
    }

    private int inputFirst() {
        var count = 0;

        count += inputFirst(runner, false, DEFAULT_ENCODING);
        for (var encoding : encodingList) {
            count += inputFirst(runner.encoding(encoding), true, encoding);
        }

        return count;
    }

    private int inputFirst(RunnerInterface runner, boolean withEncoding, Charset encoding) {
        var count = 0;
        var typeArray = DelugeProgramType.values();
        for (var type : typeArray) {
            if (!type.isInputFirst()) {
                continue;
            }

            for (var input : inputList) {
                var programInput = forInput(runner, input);

                for (var programSetting : settingList) {
                    if (type.isArgued() != programSetting.hasArgumentArray()) {
                        continue;
                    }
                    globalError.set(programSetting.error());
                    var builder = DelugeBuilder.from(type, programSetting, input);
                    if (withEncoding) {
                        builder = builder.withEncoding(encoding);
                    }
                    outEnc.set(builder.outputCharset());

                    var programOutput = withProgram(programInput, type, programSetting);
                    var actual = DelugeProgramOutput.from(programOutput);
                    var expected = builder.buildExpectedOutput();
                    var message = DelugeBuilder.runComparison(expected, actual);
                    if (message != null) {
                        throw new DelugeException(message, null);
                    }
                    count++;
                }
            }
        }
        return count;
    }

    private RunnerProgram forProgram(RunnerInterface runner, DelugeProgramType type, DelugeSetting setting) {
        if (PROGRAM_STANDARD == type) {
            if (setting.hasCharSet()) {
                return runner.run(setting.charset(), runStandard);
            }
            return runner.run(runStandard);
        }
        else if (PROGRAM_STANDARD_ARGUED == type) {
            if (setting.hasCharSet()) {
                return runner
                    .run(setting.charset(), runStandardArgued)
                    .argument(setting.argumentArray());
            }
            return runner
                .run(runStandardArgued)
                .argument(setting.argumentArray());
        }
        else if (PROGRAM_CONSOLE == type) {
            if (setting.hasCharSet()) {
                return runner.runConsole(setting.charset(), runConsole);
            }
            return runner.runConsole(runConsole);
        }
        else if (PROGRAM_CONSOLE_ARGUED == type) {
            if (setting.hasCharSet()) {
                return runner
                    .runConsole(setting.charset(), runConsoleArgued)
                    .argument(setting.argumentArray());
            }
            return runner
                .runConsole(runConsoleArgued)
                .argument(setting.argumentArray());
        }
        else {
            throw new DelugeException("deluge.program.type.switch.default: " + type);
        }
    }

    private RunnerProgramOutput withInput(RunnerProgram program, DelugeData input) {
        var type = input.type();
        if (ARRAY == type) {
            return program.input(input.array());
        }
        else if (ARRAY_ENCODED == type) {
            return program.input(input.charset(), input.array());
        }
        else if (STREAM == type) {
            return program.input(input.streamSupplier());
        }
        else if (STREAM_ENCODED == type) {
            return program.input(input.streamSupplier(), input.charset());
        }
        else if (RESOURCE == type) {
            return program.loadInput(input.resource(), input.withClass());
        }
        else if (RESOURCE_ENCODED == type) {
            return program.loadInput(input.resource(), input.withClass(), input.charset());
        }
        else {
            throw new DelugeException("deluge.data.type.switch.default: " + type);
        }
    }

    private RunnerProgramInput forInput(RunnerInterface runner, DelugeData input) {
        var type = input.type();
        if (ARRAY == type) {
            return runner.input(input.array());
        }
        else if (ARRAY_ENCODED == type) {
            return runner.input(input.charset(), input.array());
        }
        else if (STREAM == type) {
            return runner.input(input.streamSupplier());
        }
        else if (STREAM_ENCODED == type) {
            return runner.input(input.streamSupplier(), input.charset());
        }
        else if (RESOURCE == type) {
            return runner.loadInput(input.resource(), input.withClass());
        }
        else if (RESOURCE_ENCODED == type) {
            return runner.loadInput(input.resource(), input.withClass(), input.charset());
        }
        else {
            throw new DelugeException("deluge.data.type.switch.default: " + type);
        }
    }

    private RunnerProgramOutput withProgram(RunnerProgramInput programInput, DelugeProgramType type,
        DelugeSetting setting)
    {
        if (INPUT_STANDARD == type) {
            if (setting.hasCharSet()) {
                return programInput.run(setting.charset(), runStandard);
            }
            return programInput.run(runStandard);
        }
        else if (INPUT_STANDARD_ARGUED == type) {
            if (setting.hasCharSet()) {
                return programInput
                    .argument(setting.argumentArray())
                    .run(setting.charset(), runStandardArgued);
            }
            return programInput
                .argument(setting.argumentArray())
                .run(runStandardArgued);
        }
        else if (INPUT_CONSOLE == type) {
            if (setting.hasCharSet()) {
                return programInput.runConsole(setting.charset(), runConsole);
            }
            return programInput.runConsole(runConsole);
        }
        else if (INPUT_CONSOLE_ARGUED == type) {
            if (setting.hasCharSet()) {
                return programInput
                    .argument(setting.argumentArray())
                    .runConsole(setting.charset(), runConsoleArgued);
            }
            return programInput
                .argument(setting.argumentArray())
                .runConsole(runConsoleArgued);
        }
        else {
            throw new DelugeException("deluge.program.type.switch.default: " + type);
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
