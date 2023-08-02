package example;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

/**
 * This class is the test class for {@link example.TriggerOnOffenceExample}.
 *
 * @author Osmund
 * @version 11.6.0
 * @since 5.0.0
 */
class TriggerOnOffenceExampleTest {

  @Test
  void testCase() {
    Runner.newRunner()
        .run((scanner, out) -> TriggerOnOffenceExample.run(out))
        .input()
        .expected(
            "Program output does not meet expectation.",
            "Output line number: 2",
            "Program output: Hello world!",
            "Expected output: Hello World!",
            "")
        .assertSuccess();
  }
}
