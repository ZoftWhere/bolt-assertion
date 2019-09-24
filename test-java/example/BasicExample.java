package example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

import app.zoftwhere.bolt.runner.Runner;
import org.junit.jupiter.api.Test;

class BasicExample {

    // An immutable runner that can be reused.
    private final Runner runner = new Runner();

    @Test
    void testCase() {

        // Start with defining a application (must accept scanner and writer).
        runner.run(BasicExample::basic)
            .input()
            .expected("Hello World?")
            .assertSuccess();

        // Start with defining the application input (application must still accept scanner and writer).
        runner.input()
            .run(BasicExample::basic)
            .expected("Hello World?")
            .assertSuccess();

        // Run a simple lambda.
        runner.run((scanner, bufferedWriter) -> bufferedWriter.write("Hello World!"))
            .input()
            .expected("Hello World!")
            .assertSuccess();

        // Run a program with arguments.
        runner.run(BasicExample::main)
            .argument("a=1", "b=2")
            .input()
            .expected("Program arguments:", "a=1", "b=2")
            .assertSuccess();

        // Run with arguments with input.
        runner.input()
            .argument("a=1", "b=2")
            .run(BasicExample::main)
            .expected("Program arguments:", "a=1", "b=2")
            .assertSuccess();

        // Run with a difference method.
        runner.input()
            .run((scanner, bufferedWriter) -> {
                String result = runProcess();
                bufferedWriter.write(result);
            })
            .expected("Success!")
            .assertSuccess();
    }

    private static void basic(Scanner scanner, BufferedWriter writer)
    throws IOException
    {
        writer.write("Hello World?");
    }

    private static void main(String[] argumentArray, Scanner scanner, BufferedWriter writer)
    throws IOException
    {
        writer.write("Program arguments:");
        for (String line : argumentArray) {
            writer.newLine();
            writer.write(line);
        }
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
