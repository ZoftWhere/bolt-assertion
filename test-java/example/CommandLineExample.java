package example;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * An example of how a command-line program can be tested by means of a proxy method.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 1.0.0
 */
public class CommandLineExample {

  public static void main(String[] args) throws Exception {
    try (Scanner scanner = new Scanner(System.in)) {
      try (PrintStream out = new PrintStream(System.getenv("OUTPUT_PATH"))) {
        run(args, scanner, out);
      }
    }
  }

  // Make a proxy method to decrease boilerplate, and simplify.
  @SuppressWarnings("unused")
  static void run(String[] arguments, Scanner scanner, PrintStream out) {
    String name = scanner.nextLine();
    out.printf("Hello %s!%n", name);
  }
}
