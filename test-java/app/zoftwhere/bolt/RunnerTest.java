package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Comparator;

import app.zoftwhere.bolt.api.RunnerOutput;
import app.zoftwhere.mutable.MutableValue;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.Runner.newRunner;
import static app.zoftwhere.bolt.RunnerReader.readList;
import static java.nio.charset.StandardCharsets.US_ASCII;
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
    void testRunProgram() {
        runner.run((scanner, bufferedWriter) -> {})
            .input("")
            .expected("")
            .assertSuccess();

        runner.run(US_ASCII, (scanner, bufferedWriter) -> {})
            .input("")
            .expected("")
            .assertSuccess();

        runner.run(((strings, scanner, bufferedWriter) -> {}))
            .argument("")
            .input("")
            .expected("")
            .assertSuccess();

        runner.run(US_ASCII, ((strings, scanner, bufferedWriter) -> {}))
            .argument("")
            .input("")
            .expected("")
            .assertSuccess();
    }

    @Test
    void testOptimised() {
        runner.input("optimised 1")
            .run(((scanner, bufferedWriter) -> bufferedWriter.write(scanner.nextLine())))
            .expected("optimised 1")
            .assertSuccess();

        runner.input("optimised 2")
            .run(UTF_16BE, ((scanner, bufferedWriter) -> bufferedWriter.write(scanner.nextLine())))
            .expected("optimised 2")
            .assertSuccess();

        runner.input("optimised 3")
            .argument("")
            .run(((strings, scanner, bufferedWriter) -> bufferedWriter.write(scanner.nextLine())))
            .expected("optimised 3")
            .assertSuccess();

        runner.input("optimised 4")
            .argument("")
            .run(UTF_16BE, ((strings, scanner, bufferedWriter) -> bufferedWriter.write(scanner.nextLine())))
            .expected("optimised 4")
            .assertSuccess();
    }

    @Test
    void testExecuteRunFirstNullInput() {
        final var asserter = runner
            .run((scanner, bufferedWriter) -> {})
            .input(() -> null)
            .expected("");

        asserter.assertException();
        final var result = asserter.result();
        final var exception = result.exception().orElse(null);
        assertTrue(exception instanceof NullPointerException);
        assertEquals("bolt.runner.load.input.input.stream.null", exception.getMessage());
    }

    @Test
    void testExecuteInputFirstNullInput() {
        final var asserter = runner
            .input(() -> null)
            .run((scanner, bufferedWriter) -> {})
            .expected("");

        asserter.assertException();
        final var result = asserter.result();
        final var exception = result.exception().orElse(null);
        assertTrue(exception instanceof NullPointerException);
        assertEquals("bolt.runner.load.input.input.stream.null", exception.getMessage());
    }

    @Test
    void testExecuteRunFirstNullExpectation() {
        try {
            runner //
                .run((scanner, bufferedWriter) -> {})
                .input("")
                .expected(() -> null);
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertNotNull(e.getMessage());
            assertEquals("bolt.runner.load.expectation.error", e.getMessage());
            assertEquals("bolt.runner.load.expectation.input.stream.null", e.getCause().getMessage());
        }
    }

    @Test
    void testExecuteInputFirstNullExpectation() {
        try {
            runner //
                .input("")
                .run((scanner, bufferedWriter) -> {})
                .expected(() -> null);
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertNotNull(e.getMessage());
            assertEquals("bolt.runner.load.expectation.error", e.getMessage());
            assertEquals("bolt.runner.load.expectation.input.stream.null", e.getCause().getMessage());
        }
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
    void testAssertSuccessAntagonist() {

        // BoltAssertionException when checking success against failure.
        try {
            runner //
                .runConsole(RunnerTest::echoConsole)
                .input("", "a")
                .expected("")
                .assertSuccess();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof RunnerException);
            // Lengths to not match. Expected 1, found 2.
            assertNotNull(throwable.getMessage());
            assertNull(throwable.getCause());
        }

        // BoltAssertionException when checking success against failure (with comparator).
        try {
            runner //
                .runConsole(RunnerTest::echoConsole)
                .input("a")
                .comparator(String::compareToIgnoreCase)
                .expected("z")
                .assertSuccess();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof RunnerException);
            // Line 1: Expected "z". Found "a"
            assertNotNull(throwable.getMessage());
            assertNull(throwable.getCause());
        }

        // BoltAssertionException when checking success against exception.
        final var successAntagonist = "success.exception";
        try {
            runner //
                .runConsole((inputStream, outputStream) -> { throw new Exception(successAntagonist); })
                .input("")
                .expected("")
                .assertSuccess();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof RunnerException);
            assertEquals("bolt.runner.asserter.error.found", throwable.getMessage());
            assertNull(throwable.getCause());
        }
    }

    @Test
    void testAssertFailure() {
        runner //
            .runConsole(RunnerTest::echoConsole)
            .input("a")
            .expected("z")
            .assertFailure();

        runner //
            .runConsole(RunnerTest::echoConsole)
            .input("a")
            .comparator(Comparator.naturalOrder())
            .expected("z")
            .assertFailure();

        try {
            runner //
                .runConsole((inputStream, outputStream) -> {
                    throw new Throwable("");
                })
                .input()
                .comparator(null)
                .expected()
                .assertFailure();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof RunnerException);
        }

        try {
            runner.runConsole(RunnerTest::echoConsole)
                .input("")
                .expected("")
                .assertFailure();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertTrue(throwable instanceof RunnerException);
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
            assertTrue(throwable instanceof RunnerException);
            assertEquals("bolt.runner.asserter.success.found", throwable.getMessage());
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
            assertTrue(throwable instanceof RunnerException);
            assertEquals("bolt.runner.asserter.output.length.mismatch", throwable.getMessage());
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
            assertTrue(throwable instanceof RunnerException);
            assertEquals("rethrow", throwable.getMessage());
            assertEquals("cause", throwable.getCause().getMessage());
        }
    }

    @Test
    void testRunTestSuccess() {
        var asserter = runner //
            .runConsole((inputStream, outputStream) -> {})
            .input("")
            .expected("");

        asserter.assertSuccess();

        try {
            asserter.assertFailure();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("bolt.runner.asserter.success.found", e.getMessage());
        }

        try {
            asserter.assertException();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("bolt.runner.asserter.success.found", e.getMessage());
        }

        asserter.onOffence(testResult -> fail("bolt.runner.test.success.not.offence"));

        var result = asserter.result();
        assertNull(result.message().orElse(null));
        assertNull(result.exception().orElse(null));
    }

    @Test
    void testRunTestFailureExpectedLength() {
        var asserter = runner //
            .runConsole((inputStream, outputStream) -> {})
            .input("")
            .expected("", "");

        try {
            asserter.assertSuccess();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getMessage());
        }

        asserter.assertFailure();

        try {
            asserter.assertException();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getMessage());
        }

        try {
            asserter.onOffence(result -> {
                throw new RunnerException(result.message().orElse("null.message"));
            });
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getMessage());
        }

        var result = asserter.result();
        assertEquals(-1, result.offendingIndex());
        assertNull(result.exception().orElse(null));
        assertEquals("bolt.runner.asserter.output.length.mismatch", result.message().orElse(null));
    }

    @Test
    void testRunTestFailureComparisonFailure() {
        var asserter = runner //
            .runConsole((inputStream, outputStream) -> {})
            .input("")
            .expected("mismatch");

        try {
            asserter.assertSuccess();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getMessage());
        }

        asserter.assertFailure();

        try {
            asserter.assertException();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getMessage());
        }

        try {
            asserter.onOffence(result -> {
                throw new RunnerException(result.message().orElse(null), null);
            });
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getMessage());
        }

        var result = asserter.result();
        assertEquals(0, result.offendingIndex());
        assertNull(result.exception().orElse(null));
        assertEquals("bolt.runner.asserter.output.data.mismatch", result.message().orElse(null));
    }

    @Test
    void testRunThrowException() {
        var asserter = runner //
            .runConsole((inputStream, outputStream) -> { throw new Exception("thrown"); })
            .input("")
            .expected("");

        try {
            asserter.assertSuccess();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
        }

        try {
            asserter.assertFailure();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
        }

        try {
            asserter.onOffence(result -> {
                throw result.exception().orElse(new NullPointerException(""));
            });
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof RunnerException);
            assertEquals("thrown", e.getMessage());
        }

        asserter.assertException();

        var result = asserter.result();
        var exception = result.exception().orElse(null);
        assertEquals(-1, result.offendingIndex());
        assertNotNull(exception);
        assertTrue(exception instanceof Exception);
        assertEquals("thrown", exception.getMessage());
    }

    @Test
    void testOnOffenceFallThrough() {
        runner //
            .run((scanner, bufferedWriter) -> {})
            .input("bolt.runner.on.offence.coverage")
            .expected("", "")
            .onOffence(testResult -> {});
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
