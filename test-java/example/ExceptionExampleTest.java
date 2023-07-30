package example;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

/**
 * This class is the test class for {@link example.ExceptionExample}.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 1.0.0
 */
class ExceptionExampleTest {

  // An immutable runner that can be reused.
  private final Runner runner = new Runner();

  @Test
  @SuppressWarnings("SimplifyOptionalCallChains")
  void testCase() {

    // Run with programs that should throw exceptions.
    runner
        .runConsole((inputStream, outputStream) -> ExceptionExample.programWithError())
        .input()
        .expected()
        .assertError();

    // Run with a custom assertion check.
    runner
        .runConsole((inputStream, outputStream) -> ExceptionExample.programWithError())
        .input()
        .expected()
        .assertCheck(
            (result) -> {
              if (result.isSuccess()) {
                throw new Exception("The program should have thrown an exception!");
              }
              if (!result.error().isPresent()) {
                throw new NullPointerException("Handle null pointers.");
              }
              if (!(result.error().get() instanceof IllegalStateException)) {
                String format = "Error: Expected IllegalStateException (found: %s)";
                String message = String.format(format, result.error().getClass());
                throw new IllegalStateException(message);
              }
            });
  }
}
