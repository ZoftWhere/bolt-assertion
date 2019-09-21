package app.zoftwhere.bolt.runner.scope;

import java.io.ByteArrayInputStream;

import app.zoftwhere.bolt.runner.Runner;
import app.zoftwhere.bolt.runner.Runner.RunnerAsserter;
import app.zoftwhere.bolt.runner.Runner.RunnerInput;
import app.zoftwhere.bolt.runner.Runner.RunnerLoader;
import app.zoftwhere.bolt.runner.Runner.RunnerOutput;
import app.zoftwhere.bolt.runner.Runner.RunnerPreProgram;
import app.zoftwhere.bolt.runner.Runner.RunnerPreTest;
import app.zoftwhere.bolt.runner.Runner.RunnerProgram;
import app.zoftwhere.bolt.runner.Runner.RunnerTestResult;
import app.zoftwhere.bolt.runner.RunnerProxy;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.runner.Runner.newRunner;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerScopeTest {

    private final String boltExceptionName = RunnerProxy.getBoltExceptionName();

    @SuppressWarnings({"ThrowableNotThrown", "unused"})
    @Test
    void testProgramFirstScope() {
        Runner runner = newRunner();

        RunnerPreProgram r2a;
        RunnerPreProgram r2b = runner.run((strings, scanner, writer) -> {});

        RunnerProgram r3a = runner.run((scanner, writer) -> {});
        RunnerProgram r3b = r2b.argument();

        RunnerOutput r4a = r3a.input();
        RunnerOutput r4b = r3b.input();

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

        r6a.assertResult();
        r6b.assertResult();

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

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    void testInputFirstScope() {
        Runner runner = newRunner();

        RunnerInput r2a = runner.input();

        RunnerLoader r3a = r2a.argument();

        RunnerOutput r4a = r3a.run((strings, scanner, writer) -> {});

        RunnerOutput r4b = r2a.run((scanner, writer) -> {});

        r4a.output();
        r4b.output();

        r4a.exception();
        r4b.exception();

        RunnerPreTest r5a = r4a.comparator((o1, o2) -> 0);
        r5a.output();
        r5a.exception();

        RunnerAsserter r6a = r5a.expected();
        RunnerAsserter r6b = r4b.expected();
        r6a.assertResult();
        r6b.assertResult();
        r6a.assertCheck((result) -> {});
        try {
            r6a.assertException();
            fail("bolt.scope.test.expected.bolt.assertion.exception");
        }
        catch (Exception e) {
            String exceptionClassName = e.getClass().getName();
            if (!boltExceptionName.equals(exceptionClassName)) {
                fail("bolt.scope.test.expected.bolt.assertion.exception");
            }
        }
        try {
            r6b.assertException();
            fail("bolt.scope.test.expected.bolt.assertion.exception");
        }
        catch (Exception e) {
            String exceptionClassName = e.getClass().getName();
            if (!boltExceptionName.equals(exceptionClassName)) {
                fail("bolt.scope.test.expected.bolt.assertion.exception");
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
        Runner runner = newRunner();
        assertNotNull(runner.run((scanner, writer) -> {}));
        assertNotNull(runner.run((strings, scanner, writer) -> {}));
        assertNotNull(runner.run(UTF_8, (scanner, writer) -> {}));
        assertNotNull(runner.run(UTF_8, (strings, scanner, writer) -> {}));
        assertNotNull(runner.runConsole((inputStream, outputStream) -> {}));
        assertNotNull(runner.runConsole((strings, inputStream, outputStream) -> {}));

        RunnerPreProgram preProgram = runner.run((strings, scanner, writer) -> {});
        RunnerProgram program = preProgram.argument();
        assertNotNull(program.input());
        assertNotNull(program.input(() -> null));
        assertNotNull(program.input(() -> null, UTF_8));
        assertNotNull(program.input(() -> null, UTF_8, UTF_8));

        String resourceName1 = "1";
        String resourceName2 = "2";
        assertNotNull(runner.input());
        assertNotNull(runner.loadInput(resourceName1, runner.getClass()));
        assertNotNull(runner.loadInput(resourceName1, program.getClass(), UTF_8));
        assertNotNull(runner.loadInput(resourceName2, program.getClass(), UTF_8, UTF_8));

        RunnerInput input = runner.input();
        assertNotNull(input.run((scanner, writer) -> {}));
        assertNotNull(input.run(UTF_8, (scanner, writer) -> {}));

        RunnerLoader loader = input.argument();
        assertNotNull(loader.run((strings, scanner, writer) -> {}));
        assertNotNull(loader.run(UTF_8, (strings, scanner, writer) -> {}));
        assertNotNull(loader.runConsole((strings, inputStream, outputStream) -> {}));

        RunnerOutput output = program.input();
        assertNotNull(output);

        String expectationResource = "RunnerScopeTest.txt";
        RunnerPreTest preTest = program.input().comparator(null);
        assertNotNull(preTest.expected());
        assertNotNull(preTest.expected(() -> new ByteArrayInputStream(new byte[0])));
        assertNotNull(preTest.expected(() -> new ByteArrayInputStream(new byte[0]), UTF_8));
        preTest.loadExpectation(expectationResource, getClass());
        preTest.loadExpectation(expectationResource, getClass(), UTF_8);

        RunnerAsserter asserter = preTest.expected();
        asserter.assertResult();
        asserter.assertCheck(result -> {});
        try { asserter.assertException(); }
        catch (Exception ignored) { }

        RunnerTestResult result = asserter.result();
        assertNotNull(result.expected());
        assertNotNull(result.output());
        assertTrue(result.isSuccess());
        assertFalse(result.isFail());
        assertFalse(result.exception().isPresent());
        assertFalse(result.message().isPresent());
    }

}
