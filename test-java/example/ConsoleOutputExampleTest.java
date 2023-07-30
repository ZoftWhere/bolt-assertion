package example;

import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

/**
 * This class is the test class for {@link example.ConsoleOutputExample}.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 1.0.0
 */
class ConsoleOutputExampleTest {

  private final Runner runner = new Runner();

  /** Not Thread Safe * */
  private static RunConsoleArgued redirect(final ThrowingConsumer<String[]> program) {
    return (arguments, inputStream, outputStream) -> {
      var systemIn = System.in;
      var systemOutput = System.out;
      var systemError = System.err;

      System.setIn(inputStream);
      System.setOut(new PrintStream(outputStream));
      System.setErr(new PrintStream(outputStream));

      try {
        program.accept(arguments);
      } finally {
        System.setIn(systemIn);
        System.setOut(systemOutput);
        System.setErr(systemError);
      }
    };
  }

  @Test
  void testCase() {
    // Not Thread Safe!
    runner
        .runConsole(redirect(ConsoleOutputExample::main))
        .argument()
        .input("World")
        .expected("Hello World!", "")
        .assertSuccess();
  }

  private interface ThrowingConsumer<T> {

    void accept(T value) throws Exception;
  }
}
