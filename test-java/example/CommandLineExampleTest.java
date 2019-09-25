package example;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

class CommandLineExampleTest {

    private final Runner runner = new Runner();

    @Test
    void testCase() {
        runner.run(CommandLineExample::main)
            .argument()
            .input("World")
            .expected("Hello World!", "")
            .assertSuccess();
    }

}
