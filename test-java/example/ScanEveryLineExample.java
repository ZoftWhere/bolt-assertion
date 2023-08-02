package example;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Scan Every Line example.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 2.0.0
 */
public class ScanEveryLineExample {

  /**
   * Example for running a program with scanner against fixed/file/resource input.
   *
   * @param scanner program {@link java.util.Scanner}
   * @param out program {@link java.io.PrintStream}
   */
  public static void program(Scanner scanner, PrintStream out) {
    String line = firstLine(scanner);
    out.print("[" + line + "]");

    while (hasNextLine(scanner)) {
      line = nextLine(scanner);
      out.println();
      out.print("[" + line + "]");
    }
  }

  private static String firstLine(Scanner scanner) {
    // Check for empty first line.
    scanner.useDelimiter("");
    if (scanner.hasNext("\\R")) {
      scanner.useDelimiter("\\R");
      return "";
    }

    // Check for empty input.
    scanner.useDelimiter("\\R");
    if (!scanner.hasNext()) {
      return "";
    }

    return scanner.next();
  }

  private static boolean hasNextLine(Scanner scanner) {
    return scanner.hasNext() || scanner.hasNextLine();
  }

  private static String nextLine(Scanner scanner) {
    if (scanner.hasNext()) {
      return scanner.next();
    }

    // Check for trailing form-feed character.
    scanner.skip("\f?");
    if (scanner.hasNextLine()) {
      return scanner.nextLine();
    }
    return "";
  }
}
