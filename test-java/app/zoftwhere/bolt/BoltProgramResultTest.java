package app.zoftwhere.bolt;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

class BoltProgramResultTest {

    @Test
    void testEmptyExpectation() {
        var blank = new String[] {""};
        var exception = new Exception("");
        try {
            new BoltProgramResult(blank, blank, exception);
            fail("bolt.program.output.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertNotNull(e.getMessage());
            assertEquals("bolt.runner.expected.expectation.length.zero", e.getMessage());
            assertNull(e.getCause());
        }
    }

}