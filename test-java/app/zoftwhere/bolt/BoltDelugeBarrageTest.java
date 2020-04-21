package app.zoftwhere.bolt;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import app.zoftwhere.bolt.deluge.DelugeBuilder;
import app.zoftwhere.bolt.deluge.DelugeData;
import app.zoftwhere.bolt.deluge.DelugeProgramType;
import app.zoftwhere.bolt.deluge.DelugeSetting;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.array;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forInputStream;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forResource;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forSetting;
import static app.zoftwhere.bolt.deluge.DelugeBuilder.forStringArray;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;

class BoltDelugeBarrageTest {

    private final Charset[] encodingArray = {null, US_ASCII, UTF_8, UTF_16};

    private final Charset[] charsetArray = {null, US_ASCII, UTF_8, UTF_16LE, UTF_16BE};

    private final String[][] argumentArray = new String[][] {
        null,
        new String[] {null},
        new String[] {"≤", null},
        new String[] {null, "≥"},
    };

    private final Exception[] errorArray = {
        new Exception("deluge.exception.test"),
        new Exception("deluge.exception.test", new Exception("deluge.exception.test.cause")),
        new RuntimeException("deluge.exception.test", null),
        new RuntimeException("deluge.exception.test", new Exception("deluge.exception.test.cause")),
    };

    @Test
    void barrageTest() {
        DelugeProgramType[] programTypes = DelugeProgramType.values();

        List<DelugeSetting> settingList = expansiveSetting();
        List<DelugeData> dataList = expansiveData();

        for (DelugeSetting setting : settingList) {
            for (DelugeProgramType programType : programTypes) {
                if (programType.isArgued() != setting.hasArgumentArray()) {
                    continue;
                }

                for (DelugeData data : dataList) {
                    DelugeBuilder.runTest(programType, setting, data);
                }
            }
        }
    }

    private List<DelugeData> expansiveData() {
        final var arraySingleNull = new String[] {null};
        List<DelugeData> list = new ArrayList<>();

        list.addAll(listForData(null));
        list.addAll(listForData(arraySingleNull));
        list.addAll(listForData(array()));
        list.addAll(listForData(array("ListForDataTest")));
        list.addAll(listForData(array("Hello World!\r", "\nUnicode(\ud801\udc10)", "")));

        list.addAll(listForData(array("<", null)));
        list.addAll(listForData(array(null, ">")));

        list.add(forInputStream(new NullPointerException("InputStreamExceptionTest")));
        list.add(forInputStream(new NullPointerException("InputStreamExceptionTest"), UTF_8));

        list.add(forResource("<null>", Runner.class));
        list.add(forResource("<null>", null));
        list.add(forResource(null, Runner.class));
        list.add(forResource(null, null));

        list.add(forResource("<null>", Runner.class, UTF_8));
        list.add(forResource("<null>", null, UTF_8));
        list.add(forResource(null, Runner.class, UTF_8));
        list.add(forResource(null, null, UTF_8));
        list.add(forResource("RunnerTest.txt", null));
        list.add(forResource("RunnerTest.txt", null, null));
        list.add(forResource("RunnerTest.txt", null, ISO_8859_1));

        list.add(forResource("RunnerBlankScopeTest.txt", Runner.class));
        list.add(forResource("RunnerBlankScopeTest.txt", Runner.class, US_ASCII));

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
