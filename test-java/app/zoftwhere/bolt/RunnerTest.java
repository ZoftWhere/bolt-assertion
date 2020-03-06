package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Comparator;

import app.zoftwhere.bolt.Runner.BoltAssertionException;
import app.zoftwhere.bolt.Runner.RunnerOutput;
import app.zoftwhere.mutable.MutableValue;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.Runner.newRunner;
import static app.zoftwhere.bolt.RunnerReader.readList;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerTest {

    private final Runner runner = newRunner();

    @Test
    void testInputStreamClose() {
        final Charset charset = UTF_16BE;
        final MutableValue<Boolean> closedFlag = new MutableValue<>(Boolean.FALSE);
        assertTrue(closedFlag.isPresent());
        assertFalse(closedFlag.get());

        final InputStream inputStream = new ByteArrayInputStream("–Great–".getBytes(charset)) {
            @Override
            public void close() throws IOException {
                closedFlag.set(Boolean.TRUE);
                super.close();
            }
        };

        runner.input(() -> inputStream, charset)
            .runConsole((input, output) -> {})
            .expected();

        assertNotNull(closedFlag);
        assertTrue(closedFlag.isPresent());
        assertTrue(closedFlag.get());
    }

    @Test
    void testCallerFirst() {
        RunnerOutput runnerOutput = runner
            .runConsole((scanner, bufferedWriter) -> {})
            .input();

        assertNotNull(runnerOutput.output());
        assertNull(runnerOutput.exception());
        runnerOutput.expected().assertSuccess();
        runnerOutput.expected("").assertSuccess();

        RunnerOutput resultBlank = runner
            .runConsole((scanner, bufferedWriter) -> {})
            .input("");

        assertNotNull(resultBlank.output());
        assertNull(resultBlank.exception());
        resultBlank.expected().assertSuccess();
        resultBlank.expected("").assertSuccess();
    }

    @Test
    void testInputFirst() {
        RunnerOutput resultEmpty = runner
            .input()
            .runConsole((scanner, bufferedWriter) -> {});

        assertNotNull(resultEmpty.output());
        assertNull(resultEmpty.exception());
        resultEmpty.expected().assertSuccess();
        resultEmpty.expected("").assertSuccess();

        RunnerOutput resultBlank = runner //
            .input("")
            .argument("")
            .runConsole((arguments, scanner, bufferedWriter) -> {});

        assertNotNull(resultBlank.output());
        assertNull(resultBlank.exception());
        resultBlank.expected().assertSuccess();
        resultBlank.expected("").assertSuccess();
    }

    @Test
    void testLoadingExpectation() {
        RunnerOutput output = runner
            .run((scanner, writer) -> {
                writer.write("Hello World!\n");
                writer.write("1 ≤ A[i] ≤ 1014\n");
            })
            .input();

        output.loadExpectation("RunnerTest.txt", getClass())
            .assertSuccess();

        output.loadExpectation("RunnerTest.txt", getClass(), UTF_8)
            .assertSuccess();
    }

    @Test
    void testLoadingInputAll() {
        runner //
            .runConsole(RunnerTest::echoConsole)
            .loadInput("RunnerTest.txt", Runner.class)
            .expected("Hello World!", "1 ≤ A[i] ≤ 1014", "")
            .assertSuccess();

        runner //
            .runConsole(RunnerTest::echoConsole)
            .loadInput("RunnerTest.txt", Runner.class, UTF_8)
            .expected("Hello World!", "1 ≤ A[i] ≤ 1014", "")
            .assertSuccess();

        runner //
            .runConsole(RunnerTest::echoConsole)
            .loadInput("RunnerTestUTF16.txt", Runner.class, UTF_16)
            .expected("1", "2", "3", "4", "5", "6", "7", "8")
            .assertSuccess();

        runner //
            .loadInput("RunnerTest.txt", Runner.class)
            .runConsole(RunnerTest::echoConsole)
            .expected("Hello World!", "1 ≤ A[i] ≤ 1014", "")
            .assertSuccess();

        runner //
            .loadInput("RunnerTest.txt", Runner.class)
            .runConsole(RunnerTest::echoConsole)
            .expected("Hello World!", "1 ≤ A[i] ≤ 1014", "")
            .assertSuccess();

        runner //
            .input(() -> Runner.class.getResourceAsStream("RunnerTest.txt"))
            .runConsole(RunnerTest::echoConsole)
            .expected("Hello World!", "1 ≤ A[i] ≤ 1014", "")
            .assertSuccess();

        runner //
            .loadInput("RunnerTestUTF16.txt", Runner.class, UTF_16)
            .runConsole(RunnerTest::echoConsole)
            .expected("1", "2", "3", "4", "5", "6", "7", "8")
            .assertSuccess();
    }

    @Test
    void testNullSupplier() {
        runner //
            .input(() -> null)
            .runConsole(RunnerTest::echoConsole)
            .expected()
            .assertException();

        try {
            runner //
                .input()
                .runConsole(RunnerTest::echoConsole)
                .expected(() -> null)
                .assertException();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof BoltAssertionException);
            assertNotNull(e.getMessage());
            assertEquals("bolt.load.expectation.error", e.getMessage());
            assertEquals("bolt.load.expectation.input.stream.null", e.getCause().getMessage());
        }
    }

    @Test
    void testAssertSuccessAntagonist() {

        // BoltAssertionException when checking success against failure.
        try {
            runner.runConsole(RunnerTest::echoConsole)
                .input("", "a")
                .expected("")
                .assertSuccess();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof BoltAssertionException);
            // Lengths to not match. Expected 1, found 2.
            assertNotNull(throwable.getMessage());
            assertNull(throwable.getCause());
        }

        // BoltAssertionException when checking success against failure (with comparator).
        try {
            runner.runConsole(RunnerTest::echoConsole)
                .input("a")
                .comparator(String::compareToIgnoreCase)
                .expected("z")
                .assertSuccess();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof BoltAssertionException);
            // Line 1: Expected "z". Found "a"
            assertNotNull(throwable.getMessage());
            assertNull(throwable.getCause());
        }

        // BoltAssertionException when checking success against exception.
        final var successAntagonist = "success.exception";
        try {
            runner.runConsole((inputStream, outputStream) -> { throw new Exception(successAntagonist); })
                .input("")
                .expected("")
                .assertSuccess();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof BoltAssertionException);
            assertNull(throwable.getMessage());
            assertNotNull(throwable.getCause());
            assertEquals(successAntagonist, throwable.getCause().getMessage());
        }
    }

    @Test
    void testAssertFail() {
        runner.runConsole(RunnerTest::echoConsole)
            .input("a")
            .expected("z")
            .assertFailure();

        runner.runConsole(RunnerTest::echoConsole)
            .input("a")
            .comparator(Comparator.naturalOrder())
            .expected("z")
            .assertFailure();

        try {
            runner.runConsole((inputStream, outputStream) -> {
                throw new Throwable(null, null);
            })
                .input()
                .comparator(null)
                .expected()
                .assertFailure();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof BoltAssertionException);
        }

        try {
            runner.runConsole(RunnerTest::echoConsole)
                .input("")
                .expected("")
                .assertFailure();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof BoltAssertionException);
        }
    }

    @Test
    void testAssertExceptionAntagonist() {

        // BoltAssertionException when checking exception against success.
        try {
            runner.runConsole(RunnerTest::echoConsole)
                .input("")
                .expected("")
                .assertException();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof BoltAssertionException);
            assertEquals("bolt.runner.assertion.expected.exception", throwable.getMessage());
            assertNull(throwable.getCause());
        }

        // BoltAssertionException when checking exception against failure.
        try {
            runner.runConsole(RunnerTest::echoConsole)
                .input("", "a")
                .expected("")
                .assertException();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof BoltAssertionException);
            assertEquals("bolt.runner.assertion.expected.exception", throwable.getMessage());
            assertNull(throwable.getCause());
        }
    }

    @Test
    void testAssertCheckAntagonist() {

        // BoltAssertionException when checking custom assertion with throwable.
        try {
            runner.runConsole(RunnerTest::echoConsole)
                .input("a")
                .expected("z")
                .assertCheck(result -> {
                    final Exception exception = new Exception("cause");
                    throw new RuntimeException("rethrow", exception);
                });
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof BoltAssertionException);
            assertEquals("rethrow", throwable.getMessage());
            assertEquals("cause", throwable.getCause().getMessage());
        }
    }

    private static void echoConsole(InputStream inputStream, OutputStream outputStream) throws IOException {
        final var list = readList(() -> new RunnerReader(inputStream, UTF_8));
        final int size = list.size();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8))) {
            if (size > 0) {
                writer.write(list.get(0));
            }
            for (int i = 1; i < size; i++) {
                writer.newLine();
                writer.write(list.get(i));
            }
            writer.flush();
        }
    }

}
