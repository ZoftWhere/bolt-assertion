package example;

import java.io.BufferedWriter;
import java.util.Scanner;

import app.zoftwhere.bolt.runner.Runner;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"CodeBlock2Expr", "WeakerAccess"})
public class HelloWorldExample {

    // An immutable runner that can be reused.
    private final Runner runner = new Runner();

    @Test
    void testCase() {

        // Hello World lambda.
        runner.run(
            (Scanner scanner, BufferedWriter writer) -> {
                writer.write("Hello World!");
            })
            .input()
            .expected("Hello World!")
            .assertSuccess();
    }

}
