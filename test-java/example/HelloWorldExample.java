package example;

import java.io.PrintStream;
import java.util.Scanner;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

class HelloWorldExample {

    // An immutable runner that can be reused.
    private final Runner runner = new Runner();

    @Test
    void testCase() {

        runner.run(
            (Scanner scanner, PrintStream out) -> {
                // Hello World lambda.
                out.print("Hello World!");
            })
            .input()
            .expected("Hello World!")
            .assertSuccess();
    }

}
