package example;

import java.util.Scanner;

/**
 * Example test class to test console program with input/output redirected.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 1.0.0
 */
public class ConsoleOutputExample {

  public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
      String name = scanner.nextLine();
      System.out.printf("Hello %s!%n", name);
    }
  }
}
