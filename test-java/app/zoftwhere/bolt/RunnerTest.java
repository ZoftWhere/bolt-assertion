package app.zoftwhere.bolt;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerResult;
import org.junit.jupiter.api.Test;

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

    private final RunnerInterface.RunnerResultConsumer consumer =
        (RunnerResult result) -> {
            if (result.isError()) {
                throw result.error().orElse(new Exception());
            }

            String errorMessage = result.message().orElse("") +
                "\r\n" + "Expected : " + Arrays.toString(result.expected()) +
                "\r\n" + "Found    : " + Arrays.toString(result.output());
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
    void testDeprecatedThrowable() {
        boolean caught = false;
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
    void testPartialOutput() {
        var result = runner.input("1", "2", "3", "4.5")
            .run((scanner, out) -> {
                scanner.useDelimiter("\\R");
                while (scanner.hasNext()) {
                    out.printf("%d%n", Integer.parseInt(scanner.next()));
                }
            })
            .expected("1", "2", "3")
            .result();

        Exception error = result.error().orElse(null);
        assertNotNull(error);
        BoltTestHelper.assertClass(NumberFormatException.class, error);
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
                try (Scanner scanner = new Scanner(in, UTF_16LE)) {
                    try (PrintStream print = new PrintStream(out, false, UTF_16LE)) {
                        scanner.useDelimiter("\\R");
                        while (scanner.hasNext()) {
                            print.println(scanner.next());
                        }
                    }
                }
            })
            .comparator(String::compareTo)
            .expected("Hello World!", "1 ≤ A[i] ≤ 1014", "")
            .onOffence(consumer);
    }

    @Test
    void testResourceUTF16() {
        newRunner()
            .encoding(UTF_16)
            .loadInput("RunnerTestUTF16.txt", Runner.class)
            .runConsole(UTF_8, (in, out) -> {
                try (Scanner scanner = new Scanner(in, UTF_8)) {
                    try (PrintStream print = new PrintStream(out, false, UTF_8)) {
                        scanner.useDelimiter("\\R");
                        while (scanner.hasNext()) {
                            print.println(scanner.next());
                        }
                    }
                }
            })
            .expected("1", "2", "3", "4", "5", "6", "7", "8", "")
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

}
