package app.zoftwhere.bolt;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.array;
import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class BoltAsserterTest {

    @Test
    void testConstructorNullResult() {
        try {
            new BoltAsserter(null);
            fail("bolt.asserter.null.constructor.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertNotNull(e.getMessage());
            assertEquals("bolt.runner.asserter.result.null", e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    void testResultSuccess() {
        final var blank = new String[] {""};
        final var result = new BoltProgramResult(blank, blank);
        final var asserter = new BoltAsserter(result);

        asserter.assertSuccess();

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertFalse(result.isException());

        assertNull(result.message().orElse(null));
        assertNull(result.exception().orElse(null));

        try {
            asserter.assertFailure();
            fail("bolt.runner.asserter.success.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertNotNull(e.getMessage());
            assertEquals("bolt.runner.asserter.success.found", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            asserter.assertException();
            fail("bolt.runner.asserter.success.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertNotNull(e.getMessage());
            assertEquals("bolt.runner.asserter.success.found", e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    void testResultFailure() {
        final var blank = new String[] {""};
        final var customMessage = "bolt.asserter.custom.message";
        final var result = new BoltProgramResult(blank, blank, -1, customMessage);
        final var asserter = new BoltAsserter(result);

        asserter.assertFailure();

        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
        assertFalse(result.isException());

        assertNotNull(result.message().orElse(null));
        assertNull(result.exception().orElse(null));

        try {
            asserter.assertSuccess();
            fail("bolt.runner.asserter.failure.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertNotNull(e.getMessage());
            assertEquals(customMessage, e.getMessage());
            assertNull(e.getCause());
        }

        try {
            asserter.assertException();
            fail("bolt.runner.asserter.success.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertNotNull(e.getMessage());
            assertEquals(customMessage, e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    void testResultException() {
        final var blank = new String[] {""};
        final var empty = new String[0];
        final var errorMessage = "Throwable?";
        final var exception = new Exception(errorMessage, null);
        final var result = new BoltProgramResult(blank, empty, exception);
        final var asserter = new BoltAsserter(result);

        asserter.assertException();

        assertFalse(result.isSuccess());
        assertFalse(result.isFailure());
        assertTrue(result.isException());

        assertNull(result.message().orElse(null));

        Exception foundException = result.exception().orElse(null);
        assertNotNull(foundException);
        assertClass(exception.getClass(), foundException);
        assertEquals("Throwable?", foundException.getMessage());

        try {
            asserter.assertSuccess();
            fail("bolt.runner.asserter.error.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertNotNull(e.getMessage());
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            asserter.assertFailure();
            fail("bolt.runner.asserter.error.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertNotNull(e.getMessage());
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    void testRunTestFailureExpectedLength() {
        var outputLines = array("");
        var expectedLines = array("", "");
        final var programOutput = new BoltProgramOutput(outputLines, null);
        final var asserter = programOutput.expected(expectedLines);

        try {
            asserter.assertSuccess();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getMessage());
        }

        asserter.assertFailure();

        try {
            asserter.assertException();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getMessage());
        }

        try {
            asserter.onOffence(result -> {
                throw new RunnerException(result.message().orElse("null.message"));
            });
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getMessage());
        }

        var result = asserter.result();
        assertEquals(-1, result.offendingIndex());
        assertNull(result.exception().orElse(null));
        assertEquals("bolt.runner.asserter.output.length.mismatch", result.message().orElse(null));
    }

    @Test
    void testRunTestFailureComparisonFailure() {
        var outputLines = array("");
        var expectedLines = array("mismatch");
        final var programOutput = new BoltProgramOutput(outputLines, null);
        final var asserter = programOutput.expected(expectedLines);

        try {
            asserter.assertSuccess();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getMessage());
        }

        asserter.assertFailure();

        try {
            asserter.assertException();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getMessage());
        }

        try {
            asserter.onOffence(result -> {
                throw new RunnerException(result.message().orElse(null), null);
            });
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getMessage());
        }

        var result = asserter.result();
        assertEquals(0, result.offendingIndex());
        assertNull(result.exception().orElse(null));
        assertEquals("bolt.runner.asserter.output.data.mismatch", result.message().orElse(null));
    }

    @Test
    void testOnOffenceFallThrough() {
        final var blank = new String[] {""};
        final var empty = new String[0];
        final var result = new BoltProgramResult(blank, empty);
        final var asserter = new BoltAsserter(result);

        asserter.onOffence(testResult -> {});
    }

}
