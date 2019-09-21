package example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import app.zoftwhere.bolt.runner.Runner;
import org.junit.jupiter.api.Test;

class BasicExample {

    // An immutable runner that can be reused.
    private final Runner runner = Runner.newRunner();

    @Test
    void testCase() {

        // Start with defining a application (must accept scanner and writer).
        runner.run(BasicExample::basic)
            .input()
            .expected("Hello World?")
            .assertResult();

        // Start with defining the application input (application must still accept scanner and writer).
        runner.input()
            .run(BasicExample::basic)
            .expected("Hello World?")
            .assertResult();

        // Run a simple lambda.
        runner.run((scanner, bufferedWriter) -> bufferedWriter.write("Hello World!"))
            .input()
            .expected("Hello World!")
            .assertResult();

        // Run with a program arguments.
        runner.run(BasicExample::main)
            .argument("a=1", "b=2")
            .input()
            .expected("Program arguments: [a=1, b=2]")
            .assertResult();

        // Run with a program arguments.
        runner.input()
            .argument("a=1", "b=2")
            .run(BasicExample::main)
            .expected("Program arguments: [a=1, b=2]")
            .assertResult();

        // Run with a difference method.
        runner.input()
            .run((scanner, bufferedWriter) -> {
                String result = runProcess();
                bufferedWriter.write(result);
            })
            .expected("Success!")
            .assertResult();
    }

    private static void basic(Scanner scanner, BufferedWriter writer) throws IOException {
        writer.write("Hello World?");
    }

    private static void main(String[] argumentArray, Scanner scanner, BufferedWriter writer) throws IOException {
        writer.write("Program arguments: " + Arrays.toString(argumentArray));
    }

    private static String runProcess() {
        // Run the process and get the exit code.
        try {
            Thread.sleep(100);
            return "Success!";
        }
        catch (InterruptedException e) {
            return "Interrupted";
        }
    }

}
