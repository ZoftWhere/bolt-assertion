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
            assertEquals("bolt.runner.asserter.result.null", e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    void testSuccessState() {
        final var blank = new String[] {""};
        final var result = new BoltResult(blank, blank);
        final var asserter = new BoltAsserter(result);

        asserter.assertSuccess();

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertFalse(result.isError());

        assertNull(result.message().orElse(null));
        assertNull(result.error().orElse(null));

        try {
            asserter.assertFailure();
            fail("bolt.runner.asserter.success.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.success.found", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            asserter.assertError();
            fail("bolt.runner.asserter.success.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.success.found", e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    void testFailureState() {
        final var blank = new String[] {""};
        final var customMessage = "bolt.asserter.custom.message";
        final var result = new BoltResult(blank, blank, -1, customMessage);
        final var asserter = new BoltAsserter(result);

        asserter.assertFailure();

        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
        assertFalse(result.isError());

        assertNotNull(result.message().orElse(null));
        assertNull(result.error().orElse(null));

        try {
            asserter.assertSuccess();
            fail("bolt.runner.asserter.failure.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals(customMessage, e.getMessage());
            assertNull(e.getCause());
        }

        try {
            asserter.assertError();
            fail("bolt.runner.asserter.success.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals(customMessage, e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    void testErrorState() {
        final var blank = new String[] {""};
        final var empty = new String[0];
        final var errorMessage = "Throwable?";
        final var exception = new Exception(errorMessage, null);
        final var result = new BoltResult(blank, empty, exception);
        final var asserter = new BoltAsserter(result);

        asserter.assertError();

        assertFalse(result.isSuccess());
        assertFalse(result.isFailure());
        assertTrue(result.isError());

        assertNull(result.message().orElse(null));

        Exception error = result.error().orElse(null);
        assertNotNull(error);
        assertClass(exception.getClass(), error);
        assertEquals("Throwable?", error.getMessage());

        try {
            asserter.assertSuccess();
            fail("bolt.runner.asserter.error.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            asserter.assertFailure();
            fail("bolt.runner.asserter.error.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
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
            asserter.assertError();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getMessage());
        }

        try {
            asserter.onOffence(result -> {
                throw new IllegalStateException(result.message().orElse("null.message"));
            });
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.on.offence", e.getMessage());
            assertNotNull(e.getCause());
            assertClass(IllegalStateException.class, e.getCause());
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getCause().getMessage());
        }

        var result = asserter.result();
        assertEquals(-1, result.offendingIndex());
        assertNull(result.error().orElse(null));
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
            asserter.assertError();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getMessage());
        }

        try {
            asserter.onOffence(result -> {
                throw new IllegalStateException(result.message().orElse(null), null);
            });
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.on.offence", e.getMessage());
            assertNotNull(e.getCause());
            assertClass(IllegalStateException.class, e.getCause());
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getCause().getMessage());
        }

        var result = asserter.result();
        assertEquals(0, result.offendingIndex());
        assertNull(result.error().orElse(null));
        assertEquals("bolt.runner.asserter.output.data.mismatch", result.message().orElse(null));
    }

    @Test
    void testOnOffenceFallThrough() {
        final var one = new String[] {"one"};
        final var two = new String[] {"two"};
        final var offendingIndex = 0;
        final var message = "mismatch";
        final var result = new BoltResult(one, two, offendingIndex, message);
        final var asserter = new BoltAsserter(result);

        asserter.onOffence(runnerResult -> {});
    }

}
