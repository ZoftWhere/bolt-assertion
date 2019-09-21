package example;

import app.zoftwhere.bolt.runner.Runner;
import org.junit.jupiter.api.Test;

class CommandLineExampleTest {

    private final Runner runner = Runner.newRunner();

    @Test
    void testCase() {
        runner.run(CommandLineExample::main)
            .argument()
            .input("World")
            .expected("Hello World!")
            .assertResult();
    }

}
