package app.zoftwhere.bolt.scope;

import java.io.ByteArrayInputStream;

import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.RunnerException;
import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerPreTest;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerProgramResult;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.Runner.newRunner;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerScopeTest {

    private final String runnerException = RunnerException.class.getName();

    private final Runner runner = newRunner();

    @Test
    @SuppressWarnings("unused")
    void testProgramFirstScope() {

        RunnerPreProgram r2a;
        RunnerPreProgram r2b = runner.run((strings, scanner, writer) -> {});

        RunnerProgram r3a = runner.run((scanner, writer) -> {});
        RunnerProgram r3b = r2b.argument();

        RunnerProgramOutput r4a = r3a.input();
        RunnerProgramOutput r4b = r3b.input();

        r4a.output();
        r4b.output();

        r4a.exception();
        r4b.exception();

        RunnerPreTest r5a;
        RunnerPreTest r5b = r4b.comparator((o1, o2) -> 0);

        r5b.output();
        r5b.exception();

        RunnerAsserter r6a = r4a.expected();
        RunnerAsserter r6b = r5b.expected();

        r6a.assertSuccess();
        r6b.assertSuccess();

        r6a.assertCheck((result) -> {});
        r6b.assertCheck((result) -> {});

        try {
            r6a.assertException();
            fail();
        }
        catch (Exception ignore) {}

        try {
            r6b.assertException();
            fail();
        }
        catch (Exception ignore) {}
    }

    @Test
    void testInputFirstScope() {
        RunnerProgramInput r2a = runner.input();

        RunnerLoader r3a = r2a.argument();

        RunnerProgramOutput r4a = r3a.run((strings, scanner, writer) -> {});

        RunnerProgramOutput r4b = r2a.run((scanner, writer) -> {});

        r4a.output();
        r4b.output();

        r4a.exception();
        r4b.exception();

        RunnerPreTest r5a = r4a.comparator((o1, o2) -> 0);
        r5a.output();
        r5a.exception();

        RunnerAsserter r6a = r5a.expected();
        RunnerAsserter r6b = r4b.expected();
        r6a.assertSuccess();
        r6b.assertSuccess();
        r6a.assertCheck((result) -> {});
        try {
            r6a.assertException();
            fail("bolt.runner.scope.test.expected.bolt.assertion.exception");
        }
        catch (Exception e) {
            String exceptionClassName = e.getClass().getName();
            if (!runnerException.equals(exceptionClassName)) {
                fail("bolt.runner.scope.test.expected.bolt.assertion.exception");
            }
        }
        try {
            r6b.assertException();
            fail("bolt.runner.scope.test.expected.bolt.assertion.exception");
        }
        catch (Exception e) {
            String exceptionClassName = e.getClass().getName();
            if (!runnerException.equals(exceptionClassName)) {
                fail("bolt.runner.scope.test.expected.bolt.assertion.exception");
            }
        }
        r6b.assertCheck((result) -> {});
    }

    @Test
    void todoRealTestCase() {
        RunnerProxy proxy = new RunnerProxy();
        proxy.run((scanner, writer) -> writer.write(""));
    }

    @Test
    void testScope() {
        assertNotNull(runner.run((scanner, writer) -> {}));
        assertNotNull(runner.run((strings, scanner, writer) -> {}));
        assertNotNull(runner.runConsole((inputStream, outputStream) -> {}));
        assertNotNull(runner.runConsole((strings, inputStream, outputStream) -> {}));
        assertNotNull(runner.runConsole(UTF_8, (inputStream, outputStream) -> {}));
        assertNotNull(runner.runConsole(UTF_8, (strings, inputStream, outputStream) -> {}));

        RunnerPreProgram preProgram = runner.run((strings, scanner, writer) -> {});
        RunnerProgram program = preProgram.argument();
        assertNotNull(program.input());
        assertNotNull(program.input(() -> null));
        assertNotNull(program.input(() -> null, UTF_8));

        String resourceName1 = "1";
        String resourceName2 = "2";
        assertNotNull(runner.input());
        assertNotNull(runner.loadInput(resourceName1, runner.getClass()));
        assertNotNull(runner.loadInput(resourceName2, program.getClass(), UTF_8));

        RunnerProgramInput input = runner.input();
        assertNotNull(input.run((scanner, writer) -> {}));
        assertNotNull(input.runConsole((inputStream, outputStream) -> {}));
        assertNotNull(input.runConsole(UTF_8, (inputStream, outputStream) -> {}));

        RunnerLoader loader = input.argument();
        assertNotNull(loader.run((strings, scanner, writer) -> {}));
        assertNotNull(loader.runConsole((strings, inputStream, outputStream) -> {}));
        assertNotNull(loader.runConsole(UTF_8, (strings, inputStream, outputStream) -> {}));

        RunnerProgramOutput output = program.input();
        assertNotNull(output);

        String expectationResource = "RunnerScopeTest.txt";
        RunnerPreTest preTest = output.comparator(null);
        assertNotNull(preTest.expected());
        assertNotNull(preTest.expected(() -> new ByteArrayInputStream(new byte[0])));
        assertNotNull(preTest.expected(() -> new ByteArrayInputStream(new byte[0]), UTF_8));
        preTest.loadExpectation(expectationResource, Runner.class);
        preTest.loadExpectation(expectationResource, Runner.class, UTF_8);

        RunnerAsserter asserter = preTest.expected();
        asserter.assertSuccess();
        asserter.assertCheck(result -> {});
        try { asserter.assertException(); }
        catch (Exception ignored) { }

        RunnerProgramResult result = asserter.result();
        assertNotNull(result.expected());
        assertNotNull(result.output());
        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertFalse(result.message().isPresent());
        assertFalse(result.isException());
        assertFalse(result.exception().isPresent());
    }

}
