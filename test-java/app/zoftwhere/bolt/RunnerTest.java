package app.zoftwhere.bolt;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Comparator;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltReader.readList;
import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static app.zoftwhere.bolt.Runner.newRunner;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerTest {

    private final Runner runner = newRunner();

    @Test
    void testRunProgram() {
        runner.run((scanner, out) -> {})
            .input("")
            .expected("")
            .assertSuccess();

        runner.run(US_ASCII, (scanner, out) -> {})
            .input("")
            .expected("")
            .assertSuccess();

        runner.run(((arguments, scanner, out) -> {}))
            .argument("")
            .input("")
            .expected("")
            .assertSuccess();

        runner.run(US_ASCII, ((arguments, scanner, out) -> {}))
            .argument("")
            .input("")
            .expected("")
            .assertSuccess();
    }

    @Test
    void testStandardThrowableCause() {
        final var s = "Ensure Throwable to Exception";
        final var e = runner //
            .run((scanner, out) -> {
                throw new Throwable(s, new RuntimeException("ignore"));
            })
            .input("")
            .expected("")
            .result()
            .exception()
            .orElse(null);

        assertNotNull(e);
        assertClass(RunnerException.class, e);
        assertEquals("bolt.runner.throwable.as.cause", e.getMessage());

        assertNotNull(e.getCause());
        assertClass(Throwable.class, e.getCause());
        assertEquals(s, e.getCause().getMessage());
    }

    @Test
    void testStandardException() {
        final var s = "Ensure RuntimeException";
        final var e = runner //
            .run((scanner, out) -> {
                throw new RuntimeException(s, null);
            })
            .input("")
            .expected("")
            .result()
            .exception()
            .orElse(null);

        assertNotNull(e);
        assertClass(RuntimeException.class, e);
        assertEquals(s, e.getMessage());
        assertNull(e.getCause());
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
            assertClass(RunnerException.class, throwable);
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
            assertClass(RunnerException.class, throwable);
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
            assertClass(RunnerException.class, throwable);
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
            assertClass(RunnerException.class, throwable);
        }

        try {
            runner.runConsole(RunnerTest::echoConsole)
                .input("")
                .expected("")
                .assertFailure();
            fail("bolt.runner.test.error.exception.expected");
        }
        catch (Throwable throwable) {
            assertClass(RunnerException.class, throwable);
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
            assertClass(RunnerException.class, throwable);
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
            assertClass(RunnerException.class, throwable);
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
            assertClass(RunnerException.class, throwable);
            assertEquals("bolt.runner.assert.check", throwable.getMessage());

            assertNotNull(throwable.getCause());
            assertClass(RuntimeException.class, throwable.getCause());
            assertEquals("rethrow", throwable.getCause().getMessage());

            assertNotNull(throwable.getCause().getCause());
            assertClass(Exception.class, throwable.getCause().getCause());
            assertEquals("cause", throwable.getCause().getCause().getMessage());
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
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.success.found", e.getMessage());
        }

        try {
            asserter.assertException();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.success.found", e.getMessage());
        }

        asserter.onOffence(testResult -> fail("bolt.runner.test.success.not.offence"));

        var result = asserter.result();
        assertNull(result.message().orElse(null));
        assertNull(result.exception().orElse(null));
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
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
        }

        try {
            asserter.assertFailure();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
        }

        try {
            asserter.onOffence(result -> {
                throw result.exception().orElse(new NullPointerException(""));
            });
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.on.offence", e.getMessage());
            assertNotNull(e.getCause());
            assertClass(Exception.class, e.getCause());
            assertEquals("thrown", e.getCause().getMessage());
        }

        asserter.assertException();

        var result = asserter.result();
        var exception = result.exception().orElse(null);
        assertEquals(-1, result.offendingIndex());
        assertNotNull(exception);
        assertClass(Exception.class, exception);
        assertEquals("thrown", exception.getMessage());
    }

    private static void echoConsole(InputStream inputStream, OutputStream outputStream) {
        final var list = readList(() -> new BoltReader(inputStream, UTF_8));
        final int size = list.size();
        try (PrintStream out = new PrintStream(outputStream, false, UTF_8)) {
            if (size > 0) {
                out.print(list.get(0));
            }
            for (int i = 1; i < size; i++) {
                out.println();
                out.print(list.get(i));
            }
            out.flush();
        }
    }

}
