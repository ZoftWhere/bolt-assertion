package example;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

/**
 * This class is the test class for {@link example.ScanEveryLineExample}.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 2.0.0
 */
class ScanEveryLineExampleTest {

  private final Runner runner = new Runner();

  @Test
  void testBlank() {
    runner //
        .run(ScanEveryLineExample::program)
        .input(" ")
        .expected("[ ]")
        .assertSuccess();
  }

  @Test
  void testEmpty() {
    runner //
        .run(ScanEveryLineExample::program)
        .input("")
        .expected("[]")
        .assertSuccess();
  }

  @Test
  void testCase() {
    runner //
        .run(ScanEveryLineExample::program)
        .input("", "Get them all.", "")
        .expected("[]", "[Get them all.]", "[]")
        .assertSuccess();
  }

  @Test
  void testSeparator() {
    runner //
        .run(ScanEveryLineExample::program)
        .input("system\r" + "\r\n" + "\n" + "and\u2028" + "unicode\u2029" + "agnostic\u0085" + "\f")
        .expected("[system]", "[]", "[]", "[and]", "[unicode]", "[agnostic]", "[]", "[]")
        .assertSuccess();
  }

  @Test
  void testByteOrderMark() {
    runner //
        .run(ScanEveryLineExample::program)
        .input("\ufeff", "Retain leading Zero-Width-Space.", "Zero-Width-Space{\ufeff}")
        .expected("[\ufeff]", "[Retain leading Zero-Width-Space.]", "[Zero-Width-Space{\ufeff}]")
        .assertSuccess();
  }
}
