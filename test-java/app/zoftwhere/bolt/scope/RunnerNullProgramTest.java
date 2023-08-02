package app.zoftwhere.bolt.scope;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.RunnerException;
import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface;
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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Comparator;
import org.junit.jupiter.api.Test;

class RunnerNullProgramTest {

  private final RunnerInterface runner = new Runner();

  private final Charset encoding = UTF_8;

  private final String[] emptyArray = new String[] {};

  private final String[] blankArray = new String[] {""};

  private final Comparator<String> comparator = Comparator.nullsFirst(Comparator.naturalOrder());

  @Test
  void testProgramFirst() {
    testProgramFirst(runner);
  }

  @Test
  void testInputFirst() {
    testInputFirst(runner);
  }

  private void testProgramFirst(RunnerProvideProgram runner) {
    testProgramInput(runner.run((RunStandard) null));
    testProgramInput(runner.run(encoding, (RunStandard) null));
    testProgramInput(runner.runConsole((RunConsole) null));
    testProgramInput(runner.runConsole(encoding, (RunConsole) null));

    testProgramArgument(runner.run((RunStandardArgued) null));
    testProgramArgument(runner.run(encoding, (RunStandardArgued) null));
    testProgramArgument(runner.runConsole((RunConsoleArgued) null));
    testProgramArgument(runner.runConsole(encoding, (RunConsoleArgued) null));
  }

  private void testProgramArgument(RunnerPreProgram preProgram) {
    testProgramInput(preProgram.argument((String) null));
    testProgramInput(preProgram.argument((String[]) null));
    testProgramInput(preProgram.argument());
    testProgramInput(preProgram.argument(emptyArray));
    testProgramInput(preProgram.argument(blankArray));
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
    testProgramThree(programInput.argument((String) null));
    testProgramThree(programInput.argument((String[]) null));
    testProgramThree(programInput.argument());
    testProgramThree(programInput.argument(emptyArray));
    testProgramThree(programInput.argument(blankArray));

    testProgramTwo(programInput.run(null));
    testProgramTwo(programInput.run(encoding, null));
    testProgramTwo(programInput.runConsole(null));
    testProgramTwo(programInput.runConsole(encoding, null));
  }

  private void testProgramTwo(RunnerProgramOutput programOutput) {
    testOptionalComparator(programOutput);
  }

  private void testProgramThree(RunnerLoader loader) {
    testOptionalComparator(loader.run(null));
    testOptionalComparator(loader.run(encoding, null));
    testOptionalComparator(loader.runConsole(null));
    testOptionalComparator(loader.runConsole(encoding, null));
  }

  private void testOptionalComparator(RunnerProgramOutput programOutput) {
    testRunnerOutput(programOutput);
    testRunnerOutput(programOutput.comparator(comparator));
  }

  private void testRunnerOutput(RunnerPreTest preTest) {
    final var output = preTest.output();
    final var duration = preTest.executionDuration();
    final var error = preTest.error().orElse(null);
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
    testAsserter(preTest.expected(this::blankStream, encoding));
    testAsserter(preTest.loadExpectation("RunnerBlankScopeTest.txt", Runner.class));
    testAsserter(preTest.loadExpectation("RunnerBlankScopeTest.txt", Runner.class, encoding));
  }

  private void testAsserter(RunnerAsserter asserter) {
    try {
      asserter.assertSuccess();
      fail("exception.expected");
    } catch (Exception e) {
      assertClass(RunnerException.class, e);
      assertEquals("bolt.runner.asserter.error.found", e.getMessage());
    }

    try {
      asserter.assertFailure();
      fail("exception.expected");
    } catch (Exception e) {
      assertClass(RunnerException.class, e);
      assertEquals("bolt.runner.asserter.error.found", e.getMessage());
    }

    asserter.assertError();

    asserter.assertCheck(this::testResult);
    asserter.onOffence(
        result -> {
          final var error = result.error().orElse(null);
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
