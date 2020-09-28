package example;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * <p>An example of how a command-line program can be tested by means of a proxy method.
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
 * @since 1.0.0
 */
@SuppressWarnings({"WeakerAccess", "unused", "RedundantSuppression"})
public class CommandLineExample {

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            try (PrintStream out = new PrintStream(System.getenv("OUTPUT_PATH"))) {
                main(args, scanner, out);
            }
        }
    }

    // Make a proxy method to decrease boilerplate, and simplify.
    static void main(String[] arguments, Scanner scanner, PrintStream out) {
        String name = scanner.nextLine();
        out.printf("Hello %s!%n", name);
    }

}
