package app.zoftwhere.bolt.scope;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Comparator;

import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerPreTest;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerProgramResult;
import app.zoftwhere.bolt.api.RunnerProvideInput;
import app.zoftwhere.bolt.api.RunnerProvideProgram;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerBlankScopeTest {

    @Test
    void testRunner() {
        final RunnerInterface runner = new Runner();
        testProgramFirst(runner);
        testInputFirst(runner);
    }

    @Test
    void testProxy() {
        final RunnerInterface proxy = new RunnerProxy();
        testProgramFirst(proxy);
        testInputFirst(proxy);
    }

    private void testProgramFirst(RunnerProvideProgram runner) {
        testProgramInput(runner.run(((scanner, bufferedWriter) -> {})));
        testProgramInput(runner.run(UTF_8, ((scanner, bufferedWriter) -> {})));
        testProgramInput(runner.runConsole(((inputStream, outputStream) -> {})));
        testProgramInput(runner.runConsole(UTF_8, ((inputStream, outputStream) -> {})));

        testProgramArgument(runner.run((strings, scanner, writer) -> {}));
        testProgramArgument(runner.run(UTF_8, (strings, scanner, writer) -> {}));
        testProgramArgument(runner.runConsole((strings, inputStream, outputStream) -> {}));
        testProgramArgument(runner.runConsole(UTF_8, (strings, inputStream, outputStream) -> {}));
    }

    private void testProgramArgument(RunnerPreProgram preProgram) {
        testProgramInput(preProgram.argument(""));
    }

    private void testProgramInput(RunnerProgram program) {
        testOptionalComparator(program.input(""));
        testOptionalComparator(program.input(() -> new ByteArrayInputStream(new byte[0])));
        testOptionalComparator(program.input(() -> new ByteArrayInputStream(new byte[0]), UTF_8));
        testOptionalComparator(program.loadInput("RunnerBlankScopeTest.txt", Runner.class));
        testOptionalComparator(program.loadInput("RunnerBlankScopeTest.txt", Runner.class, UTF_8));
    }

    private void testInputFirst(RunnerProvideInput runner) {
        testOptionalArgument(runner.input(""));
        testOptionalArgument(runner.input(() -> new ByteArrayInputStream(new byte[0])));
        testOptionalArgument(runner.input(() -> new ByteArrayInputStream(new byte[0]), UTF_8));
        testOptionalArgument(runner.loadInput("RunnerBlankScopeTest.txt", Runner.class));
        testOptionalArgument(runner.loadInput("RunnerBlankScopeTest.txt", Runner.class, UTF_8));
    }

    private void testOptionalArgument(RunnerProgramInput next) {
        testProgramThree(next.argument(""));

        testProgramTwo(next.run((scanner, bufferedWriter) -> {}));
        testProgramTwo(next.run(UTF_8, (scanner, bufferedWriter) -> {}));
        testProgramTwo(next.runConsole((inputStream, outputStream) -> {}));
        testProgramTwo(next.runConsole(UTF_8, (inputStream, outputStream) -> {}));
    }

    private void testProgramTwo(RunnerProgramOutput output) {
        testOptionalComparator(output);
    }

    private void testProgramThree(RunnerLoader loader) {
        testOptionalComparator(loader.run((strings, scanner, bufferedWriter) -> {}));
        testOptionalComparator(loader.run(UTF_8, (strings, scanner, bufferedWriter) -> {}));
        testOptionalComparator(loader.runConsole((strings, inputStream, outputStream) -> {}));
        testOptionalComparator(loader.runConsole(UTF_8, (strings, inputStream, outputStream) -> {}));
    }

    private void testOptionalComparator(RunnerProgramOutput output) {
        testRunnerOutput(output);
        testRunnerOutput(output.comparator(Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    private void testRunnerOutput(RunnerPreTest preTest) {
        testAsserter(preTest.expected(""));
        testAsserter(preTest.expected(this::blankStream));
        testAsserter(preTest.expected(this::blankStream, UTF_8));
        testAsserter(preTest.loadExpectation("RunnerBlankScopeTest.txt", Runner.class));
        testAsserter(preTest.loadExpectation("RunnerBlankScopeTest.txt", Runner.class, UTF_8));
    }

    private void testAsserter(RunnerAsserter asserter) {
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

    private void testResult(RunnerProgramResult testResult) {
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
