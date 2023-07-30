package app.zoftwhere.bolt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerInterface.RunnerResultConsumer;
import app.zoftwhere.bolt.api.RunnerResult;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltProvide.NEW_LINE;
import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static app.zoftwhere.bolt.Runner.newRunner;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerTest {

    private final Runner runner = newRunner();

    private final RunnerResultConsumer consumer = (RunnerResult result) -> {
        if (result.isError()) {
            throw result.error().orElse(new Exception());
        }

        final var errorMessage = result.message().orElse("") +
            NEW_LINE + "Expected : " + Arrays.toString(result.expected()) +
            NEW_LINE + "Found    : " + Arrays.toString(result.output());
        throw new RunnerException(errorMessage);
    };

    @Test
    void testEncoding() {
        assertEquals(UTF_8, newRunner().encoding());
        assertEquals(UTF_8, newRunner().encoding(null).encoding());
        assertEquals(UTF_8, newRunner().encoding(UTF_8).encoding());
        assertEquals(UTF_16, newRunner().encoding(UTF_16).encoding());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testCompiler() {
        // A purely paranoid test (based on a bug encountered).
        if (!"\r\n".equals(NEW_LINE)) {
            throw new RunnerException("bolt.runner.new.line.invalid");
        }
    }

    @Test
    void testDeprecatedThrowable() {
        var caught = false;
        try {
            runner.input()
                .run((scanner, out) -> {
                    throw new Error("bolt.runner.throwable.deprecated");
                })
                .expected()
                .assertError();
            caught = true;
        }
        catch (Throwable ignore) {
        }

        if (caught) {
            fail("bolt.runner.test.error.throwable.deprecated");
        }
    }

    @Test
    void testNewLine() {
        // BoltProvide NEW_LINE should be "\r\n" for this reason.
        runner
            .runConsole(UTF_8, (in, out) -> {
                try (final var reader = new InputStreamReader(in, UTF_8)) {
                    try (final var buffer = new BufferedReader(reader)) {
                        try (final var print = new PrintStream(out, false, UTF_8)) {
                            print.print(buffer.readLine());
                            while (buffer.ready()) {
                                print.print("\r\n");
                                print.print(buffer.readLine());
                            }
                        }
                    }
                }
            })
            // "1\r" "\r\n" "\n" "4\r\n" "done"
            .input("1\r", "\n4", "done")
            .expected("1", "", "", "4", "done")
            .onOffence(consumer);
    }

    @Test
    void testPartialOutput() {
        final var result = runner.input("1", "2", "3", "4.5")
            .run((scanner, out) -> {
                scanner.useDelimiter("\\R");
                while (scanner.hasNext()) {
                    out.printf("%d%n", Integer.parseInt(scanner.next()));
                }
            })
            .expected("1", "2", "3")
            .result();

        final var error = result.error().orElse(null);
        assertNotNull(error);
        assertClass(NumberFormatException.class, error);
    }

    @Test
    void testUTF16() {
        newRunner().encoding(UTF_16)
            .input(() -> new ByteArrayInputStream("\ufeffloop".getBytes(UTF_16LE)))
            .run((scanner, out) -> {
                scanner.useDelimiter("\\R");
                while (scanner.hasNext()) {
                    out.printf("[%s]", scanner.next());
                }
            })
            .expected(() -> new ByteArrayInputStream("\ufeff[loop]".getBytes(UTF_16LE)))
            .onOffence(consumer);
    }

    @Test
    void testResourceUTF8() {
        newRunner()
            .loadInput("RunnerTest.txt", Runner.class, UTF_8)
            .argument()
            .runConsole(UTF_16LE, (arguments, in, out) -> {
                try (final var scanner = new Scanner(in, UTF_16LE)) {
                    try (final var print = new PrintStream(out, false, UTF_16LE)) {
                        scanner.useDelimiter("\\R");
                        print.print(scanner.next());
                        while (scanner.hasNext()) {
                            print.print(NEW_LINE);
                            print.print(scanner.next());
                        }
                    }
                }
            })
            .comparator(String::compareTo)
            .expected("Hello World!", "1 ≤ A[i] ≤ 1014")
            .onOffence(consumer);
    }

    @Test
    void testResourceUTF16() {
        newRunner()
            .encoding(UTF_16)
            .loadInput("RunnerTestUTF16.txt", Runner.class)
            .runConsole(UTF_8, (in, out) -> {
                try (final var scanner = new Scanner(in, UTF_8)) {
                    try (final var print = new PrintStream(out, false, UTF_8)) {
                        scanner.useDelimiter("\\R");
                        print.print(scanner.next());
                        while (scanner.hasNext()) {
                            print.print(NEW_LINE);
                            print.print(scanner.next());
                        }
                    }
                }
            })
            .expected("1", "2", "3", "4", "5", "6", "7", "8")
            .onOffence(consumer);
    }

    @Test
    void testResourceToASCII() {
        newRunner()
            .encoding(UTF_8)
            .loadInput("RunnerTest.txt", Runner.class)
            .run(US_ASCII, ((scanner, out) -> {
                scanner.useDelimiter("\\R");
                while (scanner.hasNext()) {
                    out.println(scanner.next());
                }
            }))
            .expected("Hello World!", "1 ? A[i] ? 1014", "")
            .onOffence(consumer);
    }

    @Test
    void testUTF16Directional() {
        newRunner()
            .encoding(UTF_16)
            .input(() -> new ByteArrayInputStream(new byte[] {-2, -1, 0, 32, 0, 13, 0, 13, 0, 32}))
            .runConsole(UTF_16, (inputStream, outputStream) -> {
                try (final var scanner = new Scanner(inputStream, UTF_16)) {
                    try (final var out = new PrintStream(outputStream, false, UTF_16LE)) {
                        scanner.useDelimiter("\\R");
                        out.print('\ufeff');
                        out.print(scanner.next());
                        while (scanner.hasNext()) {
                            out.print(NEW_LINE);
                            out.print(scanner.next());
                        }
                    }
                }
                outputStream.flush();
            })
            .expected(" ", "", " ")
            .onOffence(consumer);
    }

    @Test
    void testSplittingFormFeed() {
        newRunner().input("1\f" + "2\r" + "3\r\n" + "4\n" + "5\f")
            .runConsole(RunnerTest::runEcho)
            .expected("1", "2", "3", "4", "5", "")
            .onOffence(consumer);
    }

    private static void runEcho(InputStream inputStream, OutputStream outputStream) throws Exception {
        final var buffer = new byte[1024];
        var n = inputStream.read(buffer, 0, 1024);
        while (n >= 0) {
            outputStream.write(buffer, 0, n);
            n = inputStream.read(buffer, 0, 1024);
        }
    }

}
