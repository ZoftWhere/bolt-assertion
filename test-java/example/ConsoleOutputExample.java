package example;

import java.util.Scanner;

@SuppressWarnings({"WeakerAccess", "RedundantSuppression"})
public class ConsoleOutputExample {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String name = scanner.nextLine();
            System.out.printf("Hello %s!%n", name);
        }
    }

}
