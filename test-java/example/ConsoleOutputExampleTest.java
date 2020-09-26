package example;

import java.io.PrintStream;

import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import org.junit.jupiter.api.Test;

/**
 * <p>Example test class with input/output redirected for console program.
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
 * @since 1.0.0
 */
@SuppressWarnings({"RedundantThrows", "RedundantSuppression"})
class ConsoleOutputExampleTest {

    private final Runner runner = new Runner();

    @Test
    void testCase() {
        // Not Thread Safe!
        runner.runConsole(redirect(ConsoleOutputExample::main))
            .argument()
            .input("World")
            .expected("Hello World!", "")
            .assertSuccess();
    }

    /** Not Thread Safe **/
    private static RunConsoleArgued redirect(
        final ThrowingConsumer<String[]> program)
    {
        return (arguments, inputStream, outputStream) -> {
            var systemIn = System.in;
            var systemOutput = System.out;
            var systemError = System.err;

            System.setIn(inputStream);
            System.setOut(new PrintStream(outputStream));
            System.setErr(new PrintStream(outputStream));

            try {
                program.accept(arguments);
            }
            finally {
                System.setIn(systemIn);
                System.setOut(systemOutput);
                System.setErr(systemError);
            }
        };
    }

    private interface ThrowingConsumer<T> {

        void accept(T value) throws Exception;
    }

}
