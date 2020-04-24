package app.zoftwhere.bolt;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.deluge.DelugeBuilder;
import app.zoftwhere.bolt.deluge.DelugeData;
import app.zoftwhere.bolt.deluge.DelugeLineScanner;
import app.zoftwhere.bolt.deluge.DelugeProgramOutput;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.Runner.newRunner;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.newLineScanner;
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
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;

class BoltDelugeBasicTest {

    @SuppressWarnings("WeakerAccess")
    public static void main(String[] args) {
        BoltDelugeBasicTest test = new BoltDelugeBasicTest();
        long rx = 2 + 2 * test.encodings.length;
        long ax = test.arguments.length;
        long cx = test.charsets.length;
        long ex = test.exceptions.length;
        long dx = test.data.length;

        long expected = rx * 2 * (1 + ax + cx + ax * cx) * ex * dx;
        System.out.println("Tests expected : " + expected);

        var start = Instant.now();
        test.runnerTest();
        var finish = Instant.now();
        var duration = Duration.ofMillis(start.until(finish, ChronoUnit.MILLIS));

        System.out.println("Tests run      : " + test.count);
        System.out.println("Duration       : " + duration);
    }

    private final Charset[] encodings = {
        US_ASCII,
        UTF_16LE,
    };

    private final Charset[] charsets = {
        US_ASCII,
        UTF_16LE,
    };

    private final String[][] arguments = {
        new String[] {null, "µ", null},
    };

    private final Exception[] exceptions = {
        null,
        new RuntimeException("test.nested.cause", new NullPointerException("test.cause")),
    };

    private final DelugeData[] data = {
        DelugeBuilder.forStringArray(null),
        DelugeBuilder.forStringArray(null, null),
        DelugeBuilder.forStringArray(new String[] {null}),
        DelugeBuilder.forStringArray(new String[] {"Hello"}),
        DelugeBuilder.forStringArray(new String[] {"Hello"}, null),
        DelugeBuilder.forStringArray(new String[] {"Hello"}, ISO_8859_1),
        DelugeBuilder.forStringArray(new String[] {null}, UTF_8),
        DelugeBuilder.forStringArray(new String[] {"Hello"}, UTF_8),
        DelugeBuilder.forStringArray(null, UTF_16),
        DelugeBuilder.forStringArray(new String[] {null}, UTF_16),
        DelugeBuilder.forStringArray(new String[] {"Hello"}, UTF_16),
        //
        DelugeBuilder.forInputStream(null, null, false),
        DelugeBuilder.forInputStream(null, null, true),
        DelugeBuilder.forInputStream(new String[] {null}, US_ASCII, true),
        DelugeBuilder.forInputStream(new String[] {"1\r", "\n2", "3", "4"}, US_ASCII, false),
        DelugeBuilder.forInputStream(new String[] {"1\r", "\n2", "3", "4"}, US_ASCII, true),
        //
        DelugeBuilder.forInputStream(new RuntimeException("TestInputError")),
        DelugeBuilder.forInputStream(new Exception("TestInputError", new Exception("TestInputCause"))),
        //
        DelugeBuilder.forResource("RunnerTest.txt", Runner.class, UTF_8),
        DelugeBuilder.forResource("RunnerTestUTF16.txt", Runner.class, UTF_16BE),
        DelugeBuilder.forResource("<none>", Runner.class),
        DelugeBuilder.forResource("<null>", null),
        DelugeBuilder.forResource(null, Runner.class),
        DelugeBuilder.forResource(null, null),
        DelugeBuilder.forResource("<null>", Runner.class, null),
        DelugeBuilder.forResource("<null>", null, US_ASCII),
        DelugeBuilder.forResource("<null>", null, null),
        DelugeBuilder.forResource(null, Runner.class, US_ASCII),
        DelugeBuilder.forResource(null, Runner.class, null),
        DelugeBuilder.forResource(null, null, US_ASCII),
        DelugeBuilder.forResource(null, null, null),
    };

    private int count;

    @Test
    void runnerTest() {
        withRunner(newRunner());
    }

    private void withRunner(Runner runner) {
        DelugeBuilder config = new DelugeBuilder();
        withInputFirst(config, runner);
        withProgramFirst(config, runner);
        for (Charset encoding : encodings) {
            withInputFirst(config.withEncoding(encoding), runner.encoding(encoding));
            withProgramFirst(config.withEncoding(encoding), runner.encoding(encoding));
        }
    }

    private void withProgramFirst(DelugeBuilder builder, RunnerInterface runner) {
        {
            withProgramSet(builder, runner);
        }
        for (String[] argument : arguments) {
            withProgramSet(builder, argument, runner);
        }
        for (Charset charset : charsets) {
            withProgramSet(builder, charset, runner);
        }
        for (String[] argument : arguments) {
            for (Charset charset : charsets) {
                withProgramSet(builder, argument, charset, runner);
            }
        }
    }

    private void withProgramSet(DelugeBuilder builder, RunnerInterface runner) {
        for (DelugeData data : data) {
            for (Exception error : exceptions) {
                DelugeBuilder updated = builder
                    .withProgram(PROGRAM_STANDARD, error)
                    .withInput(data);
                RunnerProgram program = runner
                    .run((scanner, out) -> process(null, scanner, out, error));
                withRunnerProgram(updated, program);
            }
            for (Exception error : exceptions) {
                DelugeBuilder updated = builder
                    .withProgram(PROGRAM_CONSOLE, error)
                    .withInput(data);
                Charset outEnc = updated.outputCharset();
                RunnerProgram program = runner
                    .runConsole((in, out) -> process(null, outEnc, in, outEnc, out, error));
                withRunnerProgram(updated, program);
            }
        }
    }

    private void withProgramSet(DelugeBuilder builder, String[] arguments, RunnerInterface runner) {
        for (DelugeData data : data) {
            for (Exception error : exceptions) {
                DelugeBuilder updated = builder
                    .withProgram(PROGRAM_STANDARD_ARGUED, arguments, error)
                    .withInput(data);
                RunnerProgram program = runner
                    .run((strings, scanner, out) -> process(strings, scanner, out, error))
                    .argument(arguments);
                withRunnerProgram(updated, program);
            }
            for (Exception error : exceptions) {
                DelugeBuilder updated = builder
                    .withProgram(PROGRAM_CONSOLE_ARGUED, arguments, error)
                    .withInput(data);
                Charset outEnc = updated.outputCharset();
                RunnerProgram program = runner
                    .runConsole((strings, in, out) -> process(strings, outEnc, in, outEnc, out, error))
                    .argument(arguments);
                withRunnerProgram(updated, program);
            }
        }
    }

    private void withProgramSet(DelugeBuilder builder, Charset charset, RunnerInterface runner) {
        for (DelugeData data : data) {
            for (Exception error : exceptions) {
                DelugeBuilder updated = builder
                    .withProgram(PROGRAM_STANDARD, error, charset)
                    .withInput(data);
                RunnerProgram program = runner
                    .run(charset, (scanner, out) -> process(null, scanner, out, error));
                withRunnerProgram(updated, program);
            }
            for (Exception error : exceptions) {
                DelugeBuilder updated = builder
                    .withProgram(PROGRAM_CONSOLE, error, charset)
                    .withInput(data);
                Charset outEnc = updated.outputCharset();
                RunnerProgram program = runner
                    .runConsole(charset, (in, out) -> process(null, outEnc, in, outEnc, out, error));
                withRunnerProgram(updated, program);
            }
        }
    }

    private void withProgramSet(DelugeBuilder builder, String[] arguments, Charset charset,
        RunnerInterface runner)
    {
        for (DelugeData data : data) {
            for (Exception error : exceptions) {
                DelugeBuilder updated = builder
                    .withProgram(PROGRAM_STANDARD_ARGUED, arguments, error, charset)
                    .withInput(data);
                RunnerProgram program = runner
                    .run(charset, (strings, scanner, out) -> process(strings, scanner, out, error))
                    .argument(arguments);
                withRunnerProgram(updated, program);
            }
            for (Exception error : exceptions) {
                DelugeBuilder updated = builder
                    .withProgram(PROGRAM_CONSOLE_ARGUED, arguments, error, charset)
                    .withInput(data);
                Charset outEnc = updated.outputCharset();
                RunnerProgram program = runner
                    .runConsole(charset, (strings, in, out) -> process(strings, outEnc, in, outEnc, out, error))
                    .argument(arguments);
                withRunnerProgram(updated, program);
            }
        }
    }

    private void withRunnerProgram(DelugeBuilder builder, RunnerProgram program) {
        DelugeData input = builder.input();
        Charset inEnc = builder.inputCharset();
        if (ARRAY == input.type()) {
            RunnerProgramOutput output = program.input(input.array());
            withRunnerProgramOutput(builder, output);
        }
        else if (ARRAY_ENCODED == input.type()) {
            RunnerProgramOutput output = program.input(inEnc, input.array());
            withRunnerProgramOutput(builder, output);
        }
        else if (STREAM == input.type()) {
            RunnerProgramOutput output = program.input(input.streamSupplier());
            withRunnerProgramOutput(builder, output);
        }
        else if (STREAM_ENCODED == input.type()) {
            RunnerProgramOutput output = program.input(input.streamSupplier(), inEnc);
            withRunnerProgramOutput(builder, output);
        }
        else if (RESOURCE == input.type()) {
            RunnerProgramOutput output = program.loadInput(input.resource(), input.withClass());
            withRunnerProgramOutput(builder, output);
        }
        else if (RESOURCE_ENCODED == input.type()) {
            RunnerProgramOutput output = program.loadInput(input.resource(), input.withClass(), inEnc);
            withRunnerProgramOutput(builder, output);
        }
        else {
            throw new RunnerException("deluge.type.switch.default: " + input.type());
        }
    }

    private void withInputFirst(DelugeBuilder config, RunnerInterface runner) {
        for (DelugeData data : data) {
            DelugeBuilder updated = config.withInput(data);
            Charset inputCharset = updated.inputCharset();
            if (ARRAY == data.type()) {
                RunnerProgramInput programInput = runner.input(data.array());
                withRunnerProgramInput(updated, programInput);
            }
            else if (ARRAY_ENCODED == data.type()) {
                RunnerProgramInput programInput = runner.input(inputCharset, data.array());
                withRunnerProgramInput(updated, programInput);
            }
            else if (STREAM == data.type()) {
                RunnerProgramInput programInput = runner.input(data.streamSupplier());
                withRunnerProgramInput(updated, programInput);
            }
            else if (STREAM_ENCODED == data.type()) {
                RunnerProgramInput programInput = runner.input(data.streamSupplier(), inputCharset);
                withRunnerProgramInput(updated, programInput);
            }
            else if (RESOURCE == data.type()) {
                RunnerProgramInput programInput = runner.loadInput(data.resource(), data.withClass());
                withRunnerProgramInput(updated, programInput);
            }
            else if (RESOURCE_ENCODED == data.type()) {
                RunnerProgramInput programInput = runner.loadInput(data.resource(), data.withClass(), inputCharset);
                withRunnerProgramInput(updated, programInput);
            }
            else {
                throw new RunnerException("deluge.data.type.switch.default: " + data.type());
            }
        }
    }

    private void withRunnerProgramInput(DelugeBuilder builder,
        RunnerProgramInput programInput)
    {
        {
            withProgramInputSet(builder, programInput);
        }
        for (String[] argument : arguments) {
            withProgramInputSet(builder, argument, programInput);
        }
        for (Charset charset : charsets) {
            withProgramInputSet(builder, charset, programInput);
        }
        for (String[] argument : arguments) {
            for (Charset charset : charsets) {
                withProgramInputSet(builder, argument, charset, programInput);
            }
        }
    }

    private void withProgramInputSet(DelugeBuilder builder, RunnerProgramInput runner) {
        DelugeData data = builder.input();
        for (Exception error : exceptions) {
            DelugeBuilder updated = builder
                .withProgram(INPUT_STANDARD, error)
                .withInput(data);
            RunnerProgramOutput output = runner
                .run((scanner, out) -> process(null, scanner, out, error));
            withRunnerProgramOutput(updated, output);
        }
        for (Exception error : exceptions) {
            DelugeBuilder updated = builder
                .withProgram(INPUT_CONSOLE, error)
                .withInput(data);
            Charset outEnc = updated.outputCharset();
            RunnerProgramOutput output = runner
                .runConsole((in, out) -> process(null, outEnc, in, outEnc, out, error));
            withRunnerProgramOutput(updated, output);
        }
    }

    private void withProgramInputSet(DelugeBuilder builder, String[] arguments,
        RunnerProgramInput runner)
    {
        DelugeData data = builder.input();
        for (Exception error : exceptions) {
            DelugeBuilder updated = builder
                .withProgram(INPUT_STANDARD_ARGUED, arguments, error)
                .withInput(data);
            RunnerProgramOutput output = runner
                .argument(arguments)
                .run((strings, scanner, out) -> process(strings, scanner, out, error));
            withRunnerProgramOutput(updated, output);
        }
        for (Exception error : exceptions) {
            DelugeBuilder updated = builder
                .withProgram(INPUT_CONSOLE_ARGUED, arguments, error)
                .withInput(data);
            Charset outEnc = updated.outputCharset();
            RunnerProgramOutput output = runner
                .argument(arguments)
                .runConsole((strings, in, out) -> process(strings, outEnc, in, outEnc, out, error));
            withRunnerProgramOutput(updated, output);
        }

    }

    private void withProgramInputSet(DelugeBuilder builder, Charset charset,
        RunnerProgramInput runner)
    {
        DelugeData data = builder.input();
        for (Exception error : exceptions) {
            DelugeBuilder updated = builder
                .withProgram(INPUT_STANDARD, error, charset)
                .withInput(data);
            RunnerProgramOutput output = runner
                .run(charset, (scanner, out) -> process(null, scanner, out, error));
            withRunnerProgramOutput(updated, output);
        }
        for (Exception error : exceptions) {
            DelugeBuilder updated = builder
                .withProgram(INPUT_CONSOLE, error, charset)
                .withInput(data);
            RunnerProgramOutput output = runner
                .runConsole(charset, (in, out) -> process(null, charset, in, charset, out, error));
            withRunnerProgramOutput(updated, output);
        }
    }

    private void withProgramInputSet(DelugeBuilder builder, String[] arguments,
        Charset charset, RunnerProgramInput runner)
    {
        for (Exception error : exceptions) {
            DelugeBuilder updated = builder
                .withProgram(INPUT_STANDARD_ARGUED, arguments, error, charset);
            RunnerProgramOutput output = runner
                .argument(arguments)
                .run(charset, (strings, scanner, out) -> process(strings, scanner, out, error));
            withRunnerProgramOutput(updated, output);
        }
        for (Exception error : exceptions) {
            DelugeBuilder updated = builder
                .withProgram(INPUT_CONSOLE_ARGUED, arguments, error, charset);
            Charset outEnc = updated.outputCharset();
            RunnerProgramOutput output = runner
                .argument(arguments)
                .runConsole(charset, (strings, in, out) -> process(strings, outEnc, in, outEnc, out, error));
            withRunnerProgramOutput(updated, output);
        }
    }

    private void withRunnerProgramOutput(DelugeBuilder builder, RunnerProgramOutput programOutput) {
        DelugeProgramOutput actual = DelugeProgramOutput.from(programOutput);
        DelugeProgramOutput expected = builder.buildExpectedOutput();

        String message = DelugeBuilder.runComparison(expected, actual);
        if (message != null) {
            throw new RunnerException(message, null);
        }
        count++;
    }

    private void process(
        String[] arguments,
        Charset inputCharset,
        InputStream inputStream,
        Charset outputCharset,
        OutputStream outputStream,
        Exception error
    ) throws Exception
    {
        if (error != null) {
            throw error;
        }
        try (Scanner scanner = new Scanner(inputStream, inputCharset)) {
            try (PrintStream out = new PrintStream(outputStream, false, outputCharset)) {
                process(arguments, scanner, out);
            }
        }
    }

    private void process(String[] arguments, Scanner scanner, PrintStream out) {
        process(arguments, newLineScanner(scanner), out);
    }

    private void process(String[] arguments, Scanner scanner, PrintStream out, Exception error) throws Exception {
        if (error != null) {
            throw error;
        }
        process(arguments, newLineScanner(scanner), out);
    }

    private void process(String[] arguments, DelugeLineScanner scanner, PrintStream out) {
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
