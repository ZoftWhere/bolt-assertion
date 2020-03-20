package app.zoftwhere.bolt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoltProgramOutputTest {

    @Test
    void testLoadResourceNull() {
        String[] output = new String[] {""};
        String[] names = {null, "notFound", "RunnerTest.txt"};
        Class<?>[] withClasses = {null, Runner.class, RunnerProgramOutput.class};
        Charset[] charsets = new Charset[] {null, StandardCharsets.UTF_8, StandardCharsets.US_ASCII};

        for (String name : names) {
            for (Class<?> withClass : withClasses) {
                var programOutput = new BoltProgramOutput(output, null);
                var asserter = programOutput.loadExpectation(name, withClass);
                final String errorMessage = name == null ? "bolt.runner.load.expectation.resource.name.null"
                    : withClass == null ? "bolt.runner.load.expectation.resource.class.null"
                    : withClass.getResource(name) == null ? "bolt.runner.load.expectation.resource.not.found"
                    : null;
                assertAsserterFailure(asserter, errorMessage);
            }
        }

        for (String name : names) {
            for (Class<?> withClass : withClasses) {
                for (Charset charset : charsets) {
                    var programOutput = new BoltProgramOutput(output, null);
                    var asserter = programOutput.loadExpectation(name, withClass, charset);
                    final String errorMessage = charset == null ? "bolt.runner.load.expectation.charset.null"
                        : name == null ? "bolt.runner.load.expectation.resource.name.null"
                        : withClass == null ? "bolt.runner.load.expectation.resource.class.null"
                        : withClass.getResource(name) == null ? "bolt.runner.load.expectation.resource.not.found"
                        : null;
                    assertAsserterFailure(asserter, errorMessage);
                }
            }
        }
    }

    private void assertAsserterFailure(RunnerAsserter asserter, String errorMessage) {
        if (errorMessage != null) {
            asserter.assertCheck(result -> {
                assertTrue(result.isException());
                Exception exception = result.exception().orElse(null);
                assertNotNull(exception);
                assertClass(RunnerException.class, exception);
                assertEquals(errorMessage, exception.getMessage());
                assertNull(exception.getCause());
            });
        }
        else {
            asserter.assertFailure();
            asserter.assertCheck(result -> {
                assertFalse(result.isException());
                assertTrue(result.isFailure());
            });
        }
    }

}