package app.zoftwhere.bolt.scope;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.zoftwhere.bolt.BoltPlaceHolder;
import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerProvideInput;
import app.zoftwhere.bolt.api.RunnerProvideProgram;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.jupiter.api.Test;

class RunnerInputStreamTest {

  private final RunnerInterface runner = new Runner();

  private final Charset encoding = UTF_8;

  private final String[] emptyArray = new String[] {};

  private final String[] blankArray = new String[] {""};

  private final BoltPlaceHolder<Boolean> openedFlag = new BoltPlaceHolder<>(false);

  private final BoltPlaceHolder<Boolean> closedFlag = new BoltPlaceHolder<>(false);

  private final InputStreamSupplier supplier =
      () -> {
        openedFlag.set(true);

        return new ByteArrayInputStream(new byte[0]) {
          @Override
          public void close() throws IOException {
            closedFlag.set(true);
            super.close();
          }
        };
      };

  @Test
  void testProgramFirst() {
    testProgramFirst(runner);
  }

  @Test
  void testInputFirst() {
    testInputFirst(runner);
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
    testProgramInput(preProgram.argument((String) null));
    testProgramInput(preProgram.argument((String[]) null));
    testProgramInput(preProgram.argument());
    testProgramInput(preProgram.argument(emptyArray));
    testProgramInput(preProgram.argument(blankArray));
  }

  private void testProgramInput(RunnerProgram program) {
    testFlag(program.input(supplier));
    testFlag(program.input(supplier, encoding));
  }

  private void testInputFirst(RunnerProvideInput runner) {
    testOptionalArgument(runner.input(supplier));
    testOptionalArgument(runner.input(supplier, encoding));
  }

  private void testOptionalArgument(RunnerProgramInput programInput) {
    testProgramThree(programInput.argument((String) null));
    testProgramThree(programInput.argument((String[]) null));
    testProgramThree(programInput.argument());
    testProgramThree(programInput.argument(emptyArray));
    testProgramThree(programInput.argument(blankArray));

    testFlag(programInput.run((scanner, out) -> {}));
    testFlag(programInput.run(encoding, (scanner, out) -> {}));
    testFlag(programInput.runConsole((inputStream, outputStream) -> {}));
    testFlag(programInput.runConsole(encoding, (inputStream, outputStream) -> {}));
  }

  private void testProgramThree(RunnerLoader loader) {
    testFlag(loader.run((arguments, scanner, out) -> {}));
    testFlag(loader.run(encoding, (arguments, scanner, out) -> {}));
    testFlag(loader.runConsole((arguments, inputStream, outputStream) -> {}));
    testFlag(loader.runConsole(encoding, (arguments, inputStream, outputStream) -> {}));
  }

  private void testFlag(RunnerProgramOutput programOutput) {
    assertNotNull(programOutput);
    assertNull((programOutput.error().orElse(null)));
    assertTrue(openedFlag.get());
    assertTrue(closedFlag.get());
    openedFlag.set(false);
    closedFlag.set(false);
  }
}
