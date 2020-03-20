package app.zoftwhere.bolt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingConsumer;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoltProgramOutputTest {

    @Test
    void testLoadResource() throws Throwable {
        var programOutput = new BoltProgramOutput(new String[] {""}, null);
        var names = new String[] {null, "notFound", "RunnerTest.txt"};
        var withClasses = new Class<?>[] {null, Runner.class, RunnerProgramOutput.class};
        var charsets = new Charset[] {null, StandardCharsets.UTF_8, StandardCharsets.US_ASCII};
        var errorMessageHolder = new BoltPlaceHolder<String>(null);

        final ThrowingConsumer<RunnerAsserter> check;
        check = asserter -> {
            var errorMessage = errorMessageHolder.get();

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
                    String failureReason = "bolt.runner.asserter.output.length.mismatch";
                    assertEquals(failureReason, result.message().orElse(null));
                });
            }
        };

        for (String name : names) {
            for (Class<?> withClass : withClasses) {
                var asserter = programOutput.loadExpectation(name, withClass);
                String errorMessage = name == null ? "bolt.runner.load.expectation.resource.name.null"
                    : withClass == null ? "bolt.runner.load.expectation.resource.class.null"
                    : withClass.getResource(name) == null ? "bolt.runner.load.expectation.resource.not.found"
                    : null;
                errorMessageHolder.set(errorMessage);
                check.accept(asserter);
            }
        }

        for (String name : names) {
            for (Class<?> withClass : withClasses) {
                for (Charset charset : charsets) {
                    var asserter = programOutput.loadExpectation(name, withClass, charset);
                    String errorMessage = charset == null ? "bolt.runner.load.expectation.charset.null"
                        : name == null ? "bolt.runner.load.expectation.resource.name.null"
                        : withClass == null ? "bolt.runner.load.expectation.resource.class.null"
                        : withClass.getResource(name) == null ? "bolt.runner.load.expectation.resource.not.found"
                        : null;
                    errorMessageHolder.set(errorMessage);
                    check.accept(asserter);
                }
            }
        }
    }

    @Test
    void testLoadResourceSkip() throws Throwable {
        var throwable = new NullPointerException("resource.load.skip");
        var programOutput = new BoltProgramOutput(new String[] {""}, throwable);
        var names = new String[] {null, "notFound", "RunnerTest.txt"};
        var withClasses = new Class<?>[] {null, Runner.class, RunnerProgramOutput.class};
        var charsets = new Charset[] {null, StandardCharsets.UTF_8, StandardCharsets.US_ASCII};

        ThrowingConsumer<RunnerAsserter> check = asserter -> {
            asserter.assertException();
            asserter.assertCheck(result -> {
                Exception exception = result.exception().orElse(null);
                assertNotNull(exception);
                assertClass(NullPointerException.class, exception);
                assertEquals("resource.load.skip", exception.getMessage());
                assertNull(exception.getCause());
            });
        };

        for (String name : names) {
            for (Class<?> withClass : withClasses) {
                var asserter = programOutput.loadExpectation(name, withClass);
                check.accept(asserter);
            }
        }

        for (String name : names) {
            for (Class<?> withClass : withClasses) {
                for (Charset charset : charsets) {
                    var asserter = programOutput.loadExpectation(name, withClass, charset);
                    check.accept(asserter);
                }
            }
        }
    }

}