package app.zoftwhere.bolt.scope;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Comparator;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerBlankScopeTest {

    @Test
    void testRunner() {
        final Runner runner = new Runner();
        testProgramFirst(runner);
        testInputFirst(runner);
    }

    @Test
    void testProxy() {
        final RunnerProxy proxy = new RunnerProxy();
        testProgramFirst(proxy);
        testInputFirst(proxy);
    }

    private void testProgramFirst(Runner runner) {
        runner.run((scanner, bufferedWriter) -> {});

        testProgramArgument(runner.run((strings, scanner, writer) -> {}));
        testProgramArgument(runner.runConsole((strings, inputStream, outputStream) -> {}));
        testProgramArgument(runner.runConsole(UTF_8, (strings, inputStream, outputStream) -> {}));
    }

    private void testProgramArgument(Runner.RunnerPreProgram preProgram) {
        testProgramInput(preProgram.argument(""));
    }

    private void testProgramInput(Runner.RunnerProgram program) {
        testOptionalComparator(program.input(""));
        testOptionalComparator(program.input(() -> new ByteArrayInputStream(new byte[0])));
        testOptionalComparator(program.input(() -> new ByteArrayInputStream(new byte[0]), UTF_8));
        testOptionalComparator(program.loadInput("RunnerBlankScopeTest.txt", Runner.class));
        testOptionalComparator(program.loadInput("RunnerBlankScopeTest.txt", Runner.class, UTF_8));
    }

    private void testInputFirst(Runner runner) {
        testOptionalArgument(runner.input(""));
        testOptionalArgument(runner.input(() -> new ByteArrayInputStream(new byte[0])));
        testOptionalArgument(runner.input(() -> new ByteArrayInputStream(new byte[0]), UTF_8));
        testOptionalArgument(runner.loadInput("RunnerBlankScopeTest.txt", Runner.class));
        testOptionalArgument(runner.loadInput("RunnerBlankScopeTest.txt", Runner.class, UTF_8));
    }

    private void testOptionalArgument(Runner.RunnerInput next) {
        testProgramThree(next.argument(""));

        testProgramTwo(next.run((scanner, bufferedWriter) -> {}));
        testProgramTwo(next.runConsole((inputStream, outputStream) -> {}));
        testProgramTwo(next.runConsole(UTF_8, (inputStream, outputStream) -> {}));
    }

    private void testProgramTwo(Runner.RunnerOutput output) {
        testOptionalComparator(output);
    }

    private void testProgramThree(Runner.RunnerLoader loader) {
        testOptionalComparator(loader.run((strings, scanner, bufferedWriter) -> {}));
        testOptionalComparator(loader.runConsole((strings, inputStream, outputStream) -> {}));
        testOptionalComparator(loader.runConsole(UTF_8, (strings, inputStream, outputStream) -> {}));
    }

    private void testOptionalComparator(Runner.RunnerOutput output) {
        testRunnerOutput(output);
        testRunnerOutput(output.comparator(Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    private void testRunnerOutput(Runner.RunnerOutputCommon outputCommon) {
        testAsserter(outputCommon.expected(""));
        testAsserter(outputCommon.expected(this::blankStream));
        testAsserter(outputCommon.expected(this::blankStream, UTF_8));
        testAsserter(outputCommon.loadExpectation("RunnerBlankScopeTest.txt", Runner.class));
        testAsserter(outputCommon.loadExpectation("RunnerBlankScopeTest.txt", Runner.class, UTF_8));
    }

    private void testAsserter(Runner.RunnerAsserter asserter) {
        asserter.assertSuccess();
        try {
            asserter.assertFailure();
            fail("exception.expected");
        }
        catch (Exception ignore) {
        }
        try {
            asserter.assertException();
            fail("exception.expected");
        }
        catch (Exception ignore) {
        }

        asserter.assertCheck(this::testResult);
    }

    private void testResult(Runner.RunnerTestResult testResult) {
        assertTrue(testResult.isSuccess());
        assertFalse(testResult.isFailure());
        assertFalse(testResult.message().isPresent());
        assertFalse(testResult.isException());
        assertFalse(testResult.exception().isPresent());

        assertNotNull(testResult.expected());
        assertNotNull(testResult.output());
    }

    private InputStream blankStream() {
        return new ByteArrayInputStream(new byte[0]);
    }

}
