package example;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import app.zoftwhere.bolt.Runner;
import app.zoftwhere.function.ThrowingConsumer1;
import app.zoftwhere.function.ThrowingConsumer3;
import org.junit.jupiter.api.Test;

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
    private static ThrowingConsumer3<String[], InputStream, OutputStream> redirect(
        final ThrowingConsumer1<String[]> program)
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

}
