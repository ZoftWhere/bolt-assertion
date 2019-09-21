package example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

@SuppressWarnings("WeakerAccess")
public class CommandLineExample {

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            try (FileWriter fileWriter = new FileWriter(System.getenv("OUTPUT_PATH"))){
                try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                    main(args, scanner, bufferedWriter);
                }
            }
        }
    }

    // Make a proxy method to decrease boilerplate, and simplify.
    static void main(String[] arguments, Scanner scanner, BufferedWriter bufferedWriter) throws IOException {
        String name = scanner.nextLine();
        bufferedWriter.write(String.format("Hello %s!", name));
        bufferedWriter.newLine();
    }

}
