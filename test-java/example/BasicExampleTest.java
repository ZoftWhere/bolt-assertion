package example;

import app.zoftwhere.bolt.Runner;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

/**
 * A couple of basic examples of how the Bolt Assertion runner can be used in tests.
 *
 * @author Osmund
 * @version 11.4.0
 * @since 1.0.0
 */
class BasicExampleTest {

  // An immutable runner that can be reused.
  private final Runner runner = new Runner();

  private static void basic(Scanner scanner, PrintStream out) {
    out.print("Hello World?");
  }

  private static void run(String[] argumentArray, Scanner scanner, PrintStream out) {
    out.print("Program arguments:");
    for (final String line : argumentArray) {
      out.println();
      out.print(line);
    }
  }

  private static String runProcess() {
    // Run the process and get the exit code.
    try {
      Thread.sleep(100);
      return "Success!";
    } catch (InterruptedException e) {
      return "Interrupted";
    }
  }

  @Test
  void testCase() {

    // Start with defining a application (must accept scanner and print stream).
    runner.run(BasicExampleTest::basic).input().expected("Hello World?").assertSuccess();

    // Start with defining the application input (application must still accept scanner and print
    // stream).
    runner.input().run(BasicExampleTest::basic).expected("Hello World?").assertSuccess();

    // Run a simple lambda.
    runner
        .run((scanner, out) -> out.print("Hello World!"))
        .input()
        .expected("Hello World!")
        .assertSuccess();

    // Run a program with arguments.
    runner
        .run(BasicExampleTest::run)
        .argument("a=1", "b=2")
        .input()
        .expected("Program arguments:", "a=1", "b=2")
        .assertSuccess();

    // Run with arguments with input.
    runner
        .input()
        .argument("a=1", "b=2")
        .run(BasicExampleTest::run)
        .expected("Program arguments:", "a=1", "b=2")
        .assertSuccess();

    // Run with a difference method.
    runner
        .input()
        .run(
            (scanner, out) -> {
              String result = runProcess();
              out.print(result);
            })
        .expected("Success!")
        .assertSuccess();
  }
}
