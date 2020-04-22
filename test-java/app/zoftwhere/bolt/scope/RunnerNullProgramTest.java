package app.zoftwhere.bolt.scope;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Comparator;

import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.RunnerException;
import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
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

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static app.zoftwhere.bolt.Runner.newRunner;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerNullProgramTest {

    private final Runner runner = newRunner();

    private final String[] emptyArray = new String[] { };

    private final String[] blankArray = new String[] {""};

    @Test
    void testProvideProgram() {
        testProgramFirst(runner);
    }

    @Test
    void testProvideInput() {
        testInputFirst(runner);
    }

    private void testProgramFirst(RunnerProvideProgram runner) {
        testProgramInput(runner.run((RunStandard) null));
        testProgramInput(runner.run(UTF_8, (RunStandard) null));
        testProgramInput(runner.runConsole((RunConsole) null));
        testProgramInput(runner.runConsole(UTF_8, (RunConsole) null));

        testProgramArgument(runner.run((RunStandardArgued) null));
        testProgramArgument(runner.run(UTF_8, (RunStandardArgued) null));
        testProgramArgument(runner.runConsole((RunConsoleArgued) null));
        testProgramArgument(runner.runConsole(UTF_8, (RunConsoleArgued) null));
    }

    private void testProgramArgument(RunnerPreProgram preProgram) {
        testProgramInput(preProgram.argument(""));
    }

    private void testProgramInput(RunnerProgram program) {
        testOptionalComparator(program.input());
        testOptionalComparator(program.input(""));
        testOptionalComparator(program.input(emptyArray));
        testOptionalComparator(program.input(blankArray));
        testOptionalComparator(program.input(UTF_8));
        testOptionalComparator(program.input(UTF_8, ""));
        testOptionalComparator(program.input(UTF_8, emptyArray));
        testOptionalComparator(program.input(UTF_8, blankArray));
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
        testOptionalArgument(runner.input(UTF_8));
        testOptionalArgument(runner.input(UTF_8, ""));
        testOptionalArgument(runner.input(UTF_8, emptyArray));
        testOptionalArgument(runner.input(UTF_8, blankArray));
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

        testProgramTwo(programInput.run(null));
        testProgramTwo(programInput.run(UTF_8, null));
        testProgramTwo(programInput.runConsole(null));
        testProgramTwo(programInput.runConsole(UTF_8, null));
    }

    private void testProgramTwo(RunnerProgramOutput programOutput) {
        testOptionalComparator(programOutput);
    }

    private void testProgramThree(RunnerLoader loader) {
        testOptionalComparator(loader.run(null));
        testOptionalComparator(loader.run(UTF_8, null));
        testOptionalComparator(loader.runConsole(null));
        testOptionalComparator(loader.runConsole(UTF_8, null));
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
        assertNotNull(error);
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
        asserter.assertError();
        try {
            asserter.assertSuccess();
            fail("exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
        }
        try {
            asserter.assertFailure();
            fail("exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
        }

        asserter.assertCheck(this::testResult);
        asserter.onOffence(result -> {
            Exception error = result.error().orElse(null);
            assertNotNull(error);
            assertNull(error.getCause());
            assertClass(RunnerException.class, error);
        });
    }

    private void testResult(RunnerResult result) {
        assertFalse(result.isSuccess());
        assertFalse(result.isFailure());
        assertFalse(result.message().isPresent());
        assertTrue(result.isError());
        assertTrue(result.error().isPresent());

        assertNotNull(result.output());
        assertNotNull(result.expected());
        assertNotNull(result.executionDuration());
    }

    private InputStream blankStream() {
        return new ByteArrayInputStream(new byte[0]);
    }

}
