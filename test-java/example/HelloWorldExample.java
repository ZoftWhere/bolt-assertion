package example;

import java.io.PrintStream;
import java.util.Scanner;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"CodeBlock2Expr", "WeakerAccess"})
public class HelloWorldExample {

    // An immutable runner that can be reused.
    private final Runner runner = new Runner();

    @Test
    void testCase() {

        // Hello World lambda.
        runner.run(
            (Scanner scanner, PrintStream out) -> {
                out.print("Hello World!");
            })
            .input()
            .expected("Hello World!")
            .assertSuccess();
    }

}
