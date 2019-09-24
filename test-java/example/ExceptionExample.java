package example;

import app.zoftwhere.bolt.runner.Runner;
import org.junit.jupiter.api.Test;

class ExceptionExample {

    // An immutable runner that can be reused.
    private final Runner runner = new Runner();

    @Test
    void testCase() {

        // Run with programs that should throw exceptions.
        runner.runConsole((scanner, writer) -> programWithError())
            .input()
            .expected()
            .assertException();

        // Run with a custom assertion check.
        runner.runConsole((scanner, writer) -> programWithError())
            .input()
            .expected()
            .assertCheck((testResult) -> {
                if (testResult.isSuccess()) {
                    throw new Throwable("The program should have thrown an exception!");
                }
                //noinspection SimplifyOptionalCallChains
                if (!testResult.exception().isPresent()) {
                    throw new NullPointerException("Handle null pointers.");
                }
                if (!(testResult.exception().get() instanceof IllegalStateException)) {
                    String format = "Error: Expected IllegalStateException (found: %s)";
                    String message = String.format(format, testResult.exception().getClass());
                    throw new IllegalStateException(message);
                }
            });
    }

    private static void programWithError() throws IllegalStateException {
        throw new IllegalStateException("Ex@mple Err0r Pr0gr@m!");
    }

}
