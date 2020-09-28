package example;

import java.io.PrintStream;
import java.util.Scanner;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

/**
 * <p>A simple "Hello world." example.
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
 * @since 1.0.0
 */
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
