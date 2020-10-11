package app.zoftwhere.bolt;

import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.zoftwhere.bolt.deluge.DelugeBuilder;
import app.zoftwhere.bolt.deluge.DelugeData;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.array;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forInputStream;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forResource;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forStringArray;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.programSetting;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * <p>Bolt Deluge Barrage.
 * </p>
 *
 * @author Osmund
 * @since 11.0.0
 */
class BoltDelugeBarrage {

    /**
     * <p>Java main method for quick IDE execution.</p>
     *
     * @param arguments input arguments
     * @since 11.0.0
     */
    @SuppressWarnings("WeakerAccess")
    public static void main(String[] arguments) {
        final var test = new BoltDelugeBarrage();
        final var rx = 2 + 2 * test.encodingArray.length;
        final var ax = test.argumentArray.length;
        final var cx = test.charsetArray.length;
        final var ex = 1 + test.errorArray.length;
        final var dx = test.dataList.size();

        final var expected = rx * 2 * (1 + ax + cx + ax * cx) * ex * dx;
        System.out.println("Tests expected : " + expected);

        final var start = Instant.now();
        final var count = test.main();
        final var finish = Instant.now();
        final var duration = Duration.ofMillis(start.until(finish, ChronoUnit.MILLIS));

        System.out.println("Tests run      : " + count);
        System.out.println("Duration       : " + duration);
    }

    private final Charset[] encodingArray = {null, US_ASCII, UTF_8, UTF_16LE, UTF_16BE, UTF_16};

    private final Charset[] charsetArray = {null, US_ASCII, UTF_8, UTF_16LE, UTF_16BE, UTF_16};

    private final String[][] argumentArray = {
        null,
        new String[] {null},
        new String[] {"\ufeff"},
        new String[] {"HelloWorld"},
        new String[] {"≤", null},
        new String[] {null, "≥"},
    };

    private final Exception[] errorArray = {
        new Exception("deluge.exception.test"),
        new Exception("deluge.exception.test", new Exception("deluge.exception.test.cause")),
        new RuntimeException("deluge.exception.test", null),
        new RuntimeException("deluge.exception.test", new Exception("deluge.exception.test.cause")),
    };

    private final List<DelugeData> dataList = expansiveData();

    @Test
    void runnerTest() {
        if (!System.getProperties().containsKey("bolt.runner.deluge.test")) {
            final var cause = new RunnerException("This exception is to manage long running tests.");
            throw new RunnerException("bolt.runner.deluge.test.flag.missing", cause);
        }
        if (!"barrage".equals(System.getProperties().getProperty("bolt.runner.deluge.test"))) {
            throw new RunnerException("bolt.runner.deluge.test.flag.mismatched");
        }

        new BoltDelugeBarrage().main();
    }

    private int main() {
        final var encodingList = Arrays.asList(encodingArray);
        final var argumentList = Arrays.asList(argumentArray);
        final var errorList = Arrays.asList(errorArray);
        final var charsetList = Arrays.asList(charsetArray);
        final var settingList = programSetting(argumentList, errorList, charsetList);

        return DelugeBuilder.runTest(encodingList, settingList, dataList);
    }

    private List<DelugeData> expansiveData() {
        final var arraySingleNull = new String[] {null};
        final var list = new ArrayList<DelugeData>();

        list.addAll(listForData(null));
        list.addAll(listForData(arraySingleNull));
        list.addAll(listForData(array()));
        list.addAll(listForData(array("ListForDataTest")));
        list.addAll(listForData(array("Hello World!\r", "\nUnicode(\ud801\udc10)", "")));

        list.add(forInputStream(new NullPointerException("InputStreamExceptionTest")));
        list.add(forInputStream(new IllegalArgumentException("InputStreamExceptionTest"), UTF_8));

        list.add(forResource("<null>", Runner.class));
        list.add(forResource("<null>", null));
        list.add(forResource(null, Runner.class));
        list.add(forResource(null, null));

        list.add(forResource("<null>", Runner.class, UTF_8));
        list.add(forResource("<null>", Runner.class, null));
        list.add(forResource("<null>", null, UTF_8));
        list.add(forResource("<null>", null, null));
        list.add(forResource(null, Runner.class, UTF_8));
        list.add(forResource(null, Runner.class, null));
        list.add(forResource(null, null, UTF_8));
        list.add(forResource(null, null, null));

        list.add(forResource("RunnerTest.txt", Runner.class));
        list.add(forResource("RunnerTest.txt", Runner.class, UTF_8));

        list.add(forResource("RunnerTestUTF16.txt", Runner.class, UTF_16BE));

        return list;
    }

    private List<DelugeData> listForData(String[] data) {
        final var list = new ArrayList<DelugeData>();
        list.add(forStringArray(data));
        for (final var charset : charsetArray) {
            list.add(forStringArray(data, charset));
            list.add(forInputStream(data, charset, true));

            if (charset != null) {
                list.add(forInputStream(data, charset, false));
            }
        }
        return list;
    }

}
