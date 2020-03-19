package app.zoftwhere.bolt;

import org.junit.jupiter.api.Test;

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
        var blank = new String[] {""};
        final var result = new BoltProgramResult(blank, blank);
        final var asserter = new BoltAsserter(result);

        asserter.assertSuccess();

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertFalse(result.isException());

        assertNull(result.message().orElse(null));
        assertNull(result.exception().orElse(null));
    }

    @Test
    void testResultFailure() {
        var blank = new String[] {""};
        final var result = new BoltProgramResult(blank, blank, -1, "");
        final var asserter = new BoltAsserter(result);

        asserter.assertFailure();

        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
        assertFalse(result.isException());

        assertNotNull(result.message().orElse(null));
        assertNull(result.exception().orElse(null));
    }

    @Test
    void testResultException() {
        var blank = new String[] {""};
        final var result = new BoltProgramResult(blank, blank, new Exception("Throwable?"));
        final var asserter = new BoltAsserter(result);

        asserter.assertException();

        assertFalse(result.isSuccess());
        assertFalse(result.isFailure());
        assertTrue(result.isException());

        assertNull(result.message().orElse(null));

        Exception e = result.exception().orElse(null);
        assertNotNull(e);
        assertEquals("Throwable?", e.getMessage());
        assertEquals(Exception.class.getName(), e.getClass().getName());
    }

}
