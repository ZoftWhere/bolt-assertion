package app.zoftwhere.bolt;

import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import app.zoftwhere.bolt.deluge.DelugeBuilder;
import app.zoftwhere.bolt.deluge.DelugeData;
import app.zoftwhere.bolt.deluge.DelugeProgramType;
import app.zoftwhere.bolt.deluge.DelugeSetting;

import static app.zoftwhere.bolt.BoltTestHelper.array;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forInputStream;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forResource;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forSetting;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forStringArray;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;

class BoltDelugeBarrage {

    public static void main(String[] args) {
        BoltDelugeBarrage test = new BoltDelugeBarrage();
        long rx = 2 + 2 * test.encodingArray.length;
        long ax = test.argumentArray.length;
        long cx = test.charsetArray.length;
        long ex = 1 + test.errorArray.length;
        long dx = test.dataList.size();

        long expected = rx * 2 * (1 + ax + cx + ax * cx) * ex * dx;
        System.out.println("Tests expected : " + expected);

        var start = Instant.now();
        test.barrageTest();
        var finish = Instant.now();
        var duration = Duration.ofMillis(start.until(finish, ChronoUnit.MILLIS));

        System.out.println("Tests run      : " + test.count);
        System.out.println("Duration       : " + duration);
    }

    private final Charset[] encodingArray = {null, US_ASCII, UTF_8, UTF_16LE, UTF_16BE};

    private final Charset[] charsetArray = {null, US_ASCII, UTF_8, UTF_16LE, UTF_16BE};

    private final String[][] argumentArray = new String[][] {
        null,
        new String[] {null},
        new String[] {"\ufeffHelloWorld"},
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

    private final List<DelugeSetting> settingList = expansiveSetting();

    private final List<DelugeData> dataList = expansiveData();

    private int count = 0;

    private void barrageTest() {
        DelugeProgramType[] programTypes = DelugeProgramType.values();

        for (DelugeSetting setting : settingList) {
            for (DelugeProgramType programType : programTypes) {
                if (programType.isArgued() != setting.hasArgumentArray()) {
                    continue;
                }

                for (DelugeData data : dataList) {
                    DelugeBuilder.runTest(programType, setting, data);
                    count++;
                }
            }
        }
    }

    private List<DelugeData> expansiveData() {
        final String[] arraySingleNull = new String[] {null};
        List<DelugeData> list = new ArrayList<>();

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
        List<DelugeData> list = new ArrayList<>();
        list.add(forStringArray(data));
        for (Charset charset : charsetArray) {
            list.add(forStringArray(data, charset));
            list.add(forInputStream(data, charset, true));

            if (charset != null) {
                list.add(forInputStream(data, charset, false));
            }
        }
        return list;
    }

    private List<DelugeSetting> expansiveSetting() {
        List<DelugeSetting> list = new ArrayList<>();

        list.add(forSetting());

        for (Charset encoding : encodingArray) {
            list.add(forSetting(encoding, true));

            for (String[] argument : argumentArray) {
                list.add(forSetting(encoding, argument));

                for (Exception error : errorArray) {
                    list.add(forSetting(encoding, argument, error));

                    for (Charset charset : charsetArray) {
                        list.add(forSetting(encoding, argument, error, charset));
                    }
                }

                for (Charset charset : charsetArray) {
                    list.add(forSetting(encoding, argument, charset));
                }
            }

            for (Exception error : errorArray) {
                list.add(forSetting(encoding, error));

                for (Charset charset : charsetArray) {
                    list.add(forSetting(encoding, error, charset));
                }
            }

            for (Charset charset : charsetArray) {
                list.add(forSetting(encoding, charset));
            }
        }

        for (String[] argument : argumentArray) {
            list.add(forSetting(argument));

            for (Exception error : errorArray) {
                list.add(forSetting(argument, error));

                for (Charset charset : charsetArray) {
                    list.add(forSetting(argument, error, charset));
                }
            }

            for (Charset charset : charsetArray) {
                list.add(forSetting(argument, charset));
            }
        }

        for (Exception error : errorArray) {
            list.add(forSetting(error));

            for (Charset charset : charsetArray) {
                list.add(forSetting(error, charset));
            }
        }

        for (Charset charset : charsetArray) {
            list.add(forSetting(charset, false));
        }

        return list;
    }

}
