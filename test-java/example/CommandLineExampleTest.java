package example;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

/**
 * <p>This class is the test class for the CommandLineExample.
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
 * @since 1.0.0
 */
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
