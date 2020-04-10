package example;

import java.io.PrintStream;
import java.util.Scanner;

@SuppressWarnings({"WeakerAccess", "unused", "RedundantSuppression"})
public class CommandLineExample {

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            try (PrintStream printStream = new PrintStream(System.getenv("OUTPUT_PATH"))) {
                main(args, scanner, printStream);
            }
        }
    }

    // Make a proxy method to decrease boilerplate, and simplify.
    static void main(String[] arguments, Scanner scanner, PrintStream printStream) {
        String name = scanner.nextLine();
        printStream.println(String.format("Hello %s!", name));
    }

}
