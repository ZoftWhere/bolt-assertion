package example;

import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.Runner.newRunner;

/**
 * <p>This example shows how to handle failures or errors with the asserter consumer.
 * </p>
 * <p>The program triggers an offence as the word "world" is expected to be with an upper case 'W'
 * </p>
 */
@SuppressWarnings("WeakerAccess")
public class TriggerOnOffenceExample {

    public static void main(String[] args) {
        run(System.err);
    }

    @Test
    void testCase() {
        newRunner()
            .run((scanner, out) -> run(out))
            .input()
            .expected(
                "Program output does not meet expectation.",
                "Output line number: 2",
                "Program output: Hello world!",
                "Expected output: Hello World!",
                ""
            )
            .assertSuccess();
    }

    private static void run(PrintStream err) {
        newRunner()
            .run((scanner, out) -> {
                out.println("test:");
                out.print("Hello world!");
            })
            .input()
            .expected("test:", "Hello World!")
            .onOffence(testResult -> {
                // Check if error (throwable/exception was thrown)
                if (testResult.exception().isPresent()) {
                    throw testResult.exception().get();
                }

                // If offending index is -1, then it must be the output lengths.
                if (testResult.offendingIndex() == -1) {
                    err.println("Program output does not meet expectation.");
                    err.printf("Program outputted %d lines.%n", testResult.output().length);
                    err.printf("Expected outputted is %d lines.%n", testResult.expected().length);
                    return;
                }

                // Otherwise it must be a comparison flag.
                int index = testResult.offendingIndex();
                err.println("Program output does not meet expectation.");
                err.printf("Output line number: %d%n", index + 1);
                err.printf("Program output: %s%n", testResult.output()[index]);
                err.printf("Expected output: %s%n", testResult.expected()[index]);
            });
    }

}
