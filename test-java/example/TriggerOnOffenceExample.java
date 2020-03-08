package example;

import app.zoftwhere.bolt.Runner;

import static java.lang.System.out;

/**
 * This example shows how to handle failures or errors with the asserter consumer.
 * <p>
 * The program triggers an offence as the word "world" is expected to be with an upper case 'W'
 *
 * @since 5.0.0
 */
public class TriggerOnOffenceExample {

    public static void main(String[] args) {
        // Program output does not meet expectation.
        // Output line number: 2
        // Program output: Hello world!
        // Expected output: Hello World!

        Runner.newRunner()
            .run((scanner, bufferedWriter) -> {
                bufferedWriter.write("test:");
                bufferedWriter.newLine();
                bufferedWriter.write(helloWorld());
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
                    out.println("Program output does not meet expectation.");
                    out.println(String.format("Program outputted %d lines.", testResult.output().length));
                    out.println(String.format("Expected outputted is %d lines.", testResult.expected().length));
                }

                // Otherwise it must be a comparison flag.
                int index = testResult.offendingIndex();
                out.println("Program output does not meet expectation.");
                out.println(String.format("Output line number: %d", index + 1));
                out.println(String.format("Program output: %s", testResult.output()[index]));
                out.println(String.format("Expected output: %s", testResult.expected()[index]));
            });
    }

    private static String helloWorld() {
        return "Hello world!";
    }

}