package app.zoftwhere.bolt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RunnerAsserterTest extends Runner {

    @Test
    void testResultSuccess() {
        var blank = new String[] {""};
        final var result = new RunnerTestResult(blank, blank);
        final var asserter = new RunnerAsserter(result);

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
        final var result = new RunnerTestResult(blank, blank, "");
        final var asserter = new RunnerAsserter(result);

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
        final var result = new RunnerTestResult(blank, blank, new Exception("Throwable?"));
        final var asserter = new RunnerAsserter(result);

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
