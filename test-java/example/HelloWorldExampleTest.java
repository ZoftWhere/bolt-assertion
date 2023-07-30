package example;

import app.zoftwhere.bolt.Runner;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

/**
 * A simple "Hello world." example.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 1.0.0
 */
class HelloWorldExampleTest {

  // An immutable runner that can be reused.
  private final Runner runner = new Runner();

  @Test
  void testCase() {
    runner
        .run((Scanner scanner, PrintStream out) -> out.print("Hello World!"))
        .input()
        .expected("Hello World!")
        .assertSuccess();
  }
}
