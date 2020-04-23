package app.zoftwhere.bolt.scope;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Comparator;

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

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerProxyTest {

    private final RunnerInterface proxy = new RunnerProxy();

    private final Charset encoding = UTF_8;

    private final String[] emptyArray = new String[] { };

    private final String[] blankArray = new String[] {""};

    private final Comparator<String> comparator = Comparator.nullsFirst(Comparator.naturalOrder());

    @Test
    void testProgramFirst() {
        testProgramFirst(proxy);
    }

    @Test
    void testInputFirst() {
        testInputFirst(proxy);
    }

    private void testProgramFirst(RunnerProvideProgram runner) {
        testProgramInput(runner.run((scanner, out) -> {}));
        testProgramInput(runner.run(encoding, (scanner, out) -> {}));
        testProgramInput(runner.runConsole((inputStream, outputStream) -> {}));
        testProgramInput(runner.runConsole(encoding, (inputStream, outputStream) -> {}));

        testProgramArgument(runner.run((arguments, scanner, out) -> {}));
        testProgramArgument(runner.run(encoding, (arguments, scanner, out) -> {}));
        testProgramArgument(runner.runConsole((arguments, inputStream, outputStream) -> {}));
        testProgramArgument(runner.runConsole(encoding, (arguments, inputStream, outputStream) -> {}));
    }

    private void testProgramArgument(RunnerPreProgram preProgram) {
        testProgramInput(preProgram.argument(""));
    }

    private void testProgramInput(RunnerProgram program) {
        testOptionalComparator(program.input());
        testOptionalComparator(program.input(""));
        testOptionalComparator(program.input(emptyArray));
        testOptionalComparator(program.input(blankArray));
        testOptionalComparator(program.input(encoding));
        testOptionalComparator(program.input(encoding, ""));
        testOptionalComparator(program.input(encoding, emptyArray));
        testOptionalComparator(program.input(encoding, blankArray));
        testOptionalComparator(program.input(() -> new ByteArrayInputStream(new byte[0])));
        testOptionalComparator(program.input(() -> new ByteArrayInputStream(new byte[0]), encoding));
        testOptionalComparator(program.loadInput("RunnerBlankScopeTest.txt", Runner.class));
        testOptionalComparator(program.loadInput("RunnerBlankScopeTest.txt", Runner.class, encoding));
    }

    private void testInputFirst(RunnerProvideInput runner) {
        testOptionalArgument(runner.input());
        testOptionalArgument(runner.input(""));
        testOptionalArgument(runner.input(emptyArray));
        testOptionalArgument(runner.input(blankArray));
        testOptionalArgument(runner.input(encoding));
        testOptionalArgument(runner.input(encoding, ""));
        testOptionalArgument(runner.input(encoding, emptyArray));
        testOptionalArgument(runner.input(encoding, blankArray));
        testOptionalArgument(runner.input(() -> new ByteArrayInputStream(new byte[0])));
        testOptionalArgument(runner.input(() -> new ByteArrayInputStream(new byte[0]), encoding));
        testOptionalArgument(runner.loadInput("RunnerBlankScopeTest.txt", Runner.class));
        testOptionalArgument(runner.loadInput("RunnerBlankScopeTest.txt", Runner.class, encoding));
    }

    private void testOptionalArgument(RunnerProgramInput programInput) {
        testProgramThree(programInput.argument());
        testProgramThree(programInput.argument(""));
        testProgramThree(programInput.argument(emptyArray));
        testProgramThree(programInput.argument(blankArray));

        testProgramTwo(programInput.run((scanner, out) -> {}));
        testProgramTwo(programInput.run(encoding, (scanner, out) -> {}));
        testProgramTwo(programInput.runConsole((inputStream, outputStream) -> {}));
        testProgramTwo(programInput.runConsole(encoding, (inputStream, outputStream) -> {}));
    }

    private void testProgramTwo(RunnerProgramOutput programOutput) {
        testOptionalComparator(programOutput);
    }

    private void testProgramThree(RunnerLoader loader) {
        testOptionalComparator(loader.run((arguments, scanner, out) -> {}));
        testOptionalComparator(loader.run(encoding, (arguments, scanner, out) -> {}));
        testOptionalComparator(loader.runConsole((arguments, inputStream, outputStream) -> {}));
        testOptionalComparator(loader.runConsole(encoding, (arguments, inputStream, outputStream) -> {}));
    }

    private void testOptionalComparator(RunnerProgramOutput programOutput) {
        testRunnerOutput(programOutput);
        testRunnerOutput(programOutput.comparator(comparator));
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
        testAsserter(preTest.expected(this::blankStream, encoding));
        testAsserter(preTest.loadExpectation("RunnerBlankScopeTest.txt", Runner.class));
        testAsserter(preTest.loadExpectation("RunnerBlankScopeTest.txt", Runner.class, encoding));
    }

    private void testAsserter(RunnerAsserter asserter) {
        asserter.assertSuccess();
        try {
            asserter.assertFailure();
            fail("exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
        }
        try {
            asserter.assertError();
            fail("exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
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
