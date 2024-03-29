package app.zoftwhere.bolt;

import static app.zoftwhere.bolt.deluge.DelugeBuilder.programSetting;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;

import app.zoftwhere.bolt.deluge.DelugeBuilder;
import app.zoftwhere.bolt.deluge.DelugeData;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * Bolt Deluge Basic Test.
 *
 * @author Osmund
 * @since 11.0.0
 */
class BoltDelugeBasicTest {

  /**
   * Java main method for quick IDE execution.
   *
   * @param arguments input arguments
   * @since 11.0.0
   */
  @SuppressWarnings("WeakerAccess")
  public static void main(String[] arguments) {
    final var test = new BoltDelugeBasicTest();
    final var rx = 2 + 2 * test.encodingArray.length;
    final var ax = test.argumentArray.length;
    final var cx = test.charsetArray.length;
    final var ex = 1 + test.errorArray.length;
    final var dx = test.data.length;

    final var expected = rx * 2 * (1 + ax + cx + ax * cx) * ex * dx;
    System.out.println("Tests expected : " + expected);

    final var start = Instant.now();
    final var count = test.run();
    final var finish = Instant.now();
    final var duration = Duration.ofMillis(start.until(finish, ChronoUnit.MILLIS));

    System.out.println("Tests run      : " + count);
    System.out.println("Duration       : " + duration);
  }

  private final Charset[] encodingArray = {US_ASCII, UTF_16LE};

  private final Charset[] charsetArray = {
    null, US_ASCII, UTF_16LE,
  };

  private final String[][] argumentArray = {
    new String[] {null, "µ", null},
  };

  private final Exception[] errorArray = {
    new RuntimeException("test.nested.cause", new NullPointerException("test.cause")),
  };

  private final DelugeData[] data = {
    DelugeBuilder.forStringArray(null),
    DelugeBuilder.forStringArray(null, null),
    DelugeBuilder.forStringArray(new String[] {null}),
    DelugeBuilder.forStringArray(new String[] {"Hello"}),
    DelugeBuilder.forStringArray(new String[] {"Hello"}, null),
    DelugeBuilder.forStringArray(new String[] {"Hello"}, ISO_8859_1),
    DelugeBuilder.forStringArray(new String[] {null}, UTF_8),
    DelugeBuilder.forStringArray(new String[] {"Hello"}, UTF_8),
    DelugeBuilder.forStringArray(null, UTF_16),
    DelugeBuilder.forStringArray(new String[] {null}, UTF_16),
    DelugeBuilder.forStringArray(new String[] {"Hello"}, UTF_16),
    //
    DelugeBuilder.forInputStream(null, null, false),
    DelugeBuilder.forInputStream(null, null, true),
    DelugeBuilder.forInputStream(new String[] {null}, US_ASCII, true),
    DelugeBuilder.forInputStream(new String[] {"1\r", "\n2", "3", "4"}, US_ASCII, false),
    DelugeBuilder.forInputStream(new String[] {"1\r", "\n2", "3", "4"}, US_ASCII, true),
    //
    DelugeBuilder.forInputStream(new RuntimeException("TestInputError")),
    DelugeBuilder.forInputStream(new Exception("TestInputError", new Exception("TestInputCause"))),
    //
    DelugeBuilder.forResource("RunnerTest.txt", Runner.class, UTF_8),
    DelugeBuilder.forResource("RunnerTestUTF16.txt", Runner.class, UTF_16BE),
    DelugeBuilder.forResource("<none>", Runner.class),
    DelugeBuilder.forResource("<null>", null),
    DelugeBuilder.forResource(null, Runner.class),
    DelugeBuilder.forResource(null, null),
    DelugeBuilder.forResource("<null>", Runner.class, null),
    DelugeBuilder.forResource("<null>", null, US_ASCII),
    DelugeBuilder.forResource("<null>", null, null),
    DelugeBuilder.forResource(null, Runner.class, US_ASCII),
    DelugeBuilder.forResource(null, Runner.class, null),
    DelugeBuilder.forResource(null, null, US_ASCII),
    DelugeBuilder.forResource(null, null, null),
  };

  @Test
  void runnerTest() {
    run();
  }

  private int run() {
    final var encodingList = Arrays.asList(encodingArray);
    final var argumentList = Arrays.asList(argumentArray);
    final var errorList = Arrays.asList(errorArray);
    final var charsetList = Arrays.asList(charsetArray);
    final var settingList = programSetting(argumentList, errorList, charsetList);
    final var dataList = Arrays.asList(data);

    return DelugeBuilder.runTest(encodingList, settingList, dataList);
  }
}
