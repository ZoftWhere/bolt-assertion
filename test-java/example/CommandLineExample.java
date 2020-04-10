package example;

import java.io.PrintStream;
import java.util.Scanner;

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
