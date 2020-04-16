package app.zoftwhere.bolt.scope;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Comparator;

import app.zoftwhere.bolt.BoltTestHelper;
import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.RunnerException;
import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerPreTest;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerProvideInput;
import app.zoftwhere.bolt.api.RunnerProvideProgram;
import app.zoftwhere.bolt.api.RunnerResult;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerBlankScopeTest {

    private final String[] emptyArray = new String[] { };

    private final String[] blankArray = new String[] {""};

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
        testProgramInput(runner.run((scanner, out) -> {}));
        testProgramInput(runner.run(UTF_8, ((scanner, out) -> {})));
        testProgramInput(runner.runConsole(((inputStream, outputStream) -> {})));
        testProgramInput(runner.runConsole(UTF_8, ((inputStream, outputStream) -> {})));

        testProgramArgument(runner.run((arguments, scanner, out) -> {}));
        testProgramArgument(runner.run(UTF_8, (arguments, scanner, out) -> {}));
        testProgramArgument(runner.runConsole((arguments, inputStream, outputStream) -> {}));
        testProgramArgument(runner.runConsole(UTF_8, (arguments, inputStream, outputStream) -> {}));
    }

    private void testProgramArgument(RunnerPreProgram preProgram) {
        testProgramInput(preProgram.argument(""));
    }

    private void testProgramInput(RunnerProgram program) {
        testOptionalComparator(program.input());
        testOptionalComparator(program.input(""));
        testOptionalComparator(program.input(emptyArray));
        testOptionalComparator(program.input(blankArray));
        testOptionalComparator(program.input(() -> new ByteArrayInputStream(new byte[0])));
        testOptionalComparator(program.input(() -> new ByteArrayInputStream(new byte[0]), UTF_8));
        testOptionalComparator(program.loadInput("RunnerBlankScopeTest.txt", Runner.class));
        testOptionalComparator(program.loadInput("RunnerBlankScopeTest.txt", Runner.class, UTF_8));
    }

    private void testInputFirst(RunnerProvideInput runner) {
        testOptionalArgument(runner.input());
        testOptionalArgument(runner.input(""));
        testOptionalArgument(runner.input(emptyArray));
        testOptionalArgument(runner.input(blankArray));
        testOptionalArgument(runner.input(() -> new ByteArrayInputStream(new byte[0])));
        testOptionalArgument(runner.input(() -> new ByteArrayInputStream(new byte[0]), UTF_8));
        testOptionalArgument(runner.loadInput("RunnerBlankScopeTest.txt", Runner.class));
        testOptionalArgument(runner.loadInput("RunnerBlankScopeTest.txt", Runner.class, UTF_8));
    }

    private void testOptionalArgument(RunnerProgramInput programInput) {
        testProgramThree(programInput.argument());
        testProgramThree(programInput.argument(""));
        testProgramThree(programInput.argument(emptyArray));
        testProgramThree(programInput.argument(blankArray));

        testProgramTwo(programInput.run((scanner, out) -> {}));
        testProgramTwo(programInput.run(UTF_8, (scanner, out) -> {}));
        testProgramTwo(programInput.runConsole((inputStream, outputStream) -> {}));
        testProgramTwo(programInput.runConsole(UTF_8, (inputStream, outputStream) -> {}));
    }

    private void testProgramTwo(RunnerProgramOutput programOutput) {
        testOptionalComparator(programOutput);
    }

    private void testProgramThree(RunnerLoader loader) {
        testOptionalComparator(loader.run((arguments, scanner, out) -> {}));
        testOptionalComparator(loader.run(UTF_8, (arguments, scanner, out) -> {}));
        testOptionalComparator(loader.runConsole((arguments, inputStream, outputStream) -> {}));
        testOptionalComparator(loader.runConsole(UTF_8, (arguments, inputStream, outputStream) -> {}));
    }

    private void testOptionalComparator(RunnerProgramOutput programOutput) {
        programOutput.executionDuration();
        testRunnerOutput(programOutput);
        testRunnerOutput(programOutput.comparator(Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    private void testRunnerOutput(RunnerPreTest preTest) {
        String[] output = preTest.output();
        Duration duration = preTest.executionDuration();
        Exception error = preTest.error().orElse(null);
        assertNotNull(output);
        assertNotNull(duration);
        assertNull(error);
        assertEquals(1, output.length);
        assertEquals("", output[0]);

        testAsserter(preTest.expected());
        testAsserter(preTest.expected(""));
        testAsserter(preTest.expected(emptyArray));
        testAsserter(preTest.expected(blankArray));
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
        catch (Exception e) {
            BoltTestHelper.assertClass(RunnerException.class, e);
        }
        try {
            asserter.assertError();
            fail("exception.expected");
        }
        catch (Exception e) {
            BoltTestHelper.assertClass(RunnerException.class, e);
        }

        asserter.assertCheck(this::testResult);
    }

    private void testResult(RunnerResult result) {
        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertFalse(result.message().isPresent());
        assertFalse(result.isError());
        assertFalse(result.error().isPresent());

        assertNotNull(result.output());
        assertNotNull(result.expected());
        assertNotNull(result.executionDuration());
    }

    private InputStream blankStream() {
        return new ByteArrayInputStream(new byte[0]);
    }

}
