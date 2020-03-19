package app.zoftwhere.bolt;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import app.zoftwhere.bolt.deluge.DelugeControl;
import app.zoftwhere.bolt.deluge.DelugeData;
import app.zoftwhere.bolt.deluge.DelugeProgram;
import app.zoftwhere.bolt.deluge.DelugeProgram.ProgramType;
import app.zoftwhere.bolt.deluge.DelugeSettings;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.deluge.DelugeControl.from;
import static app.zoftwhere.bolt.deluge.DelugeData.forResource;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class BoltDelugeTest {

    @Test
    void barrageTest() {
        ProgramType[] programTypes = ProgramType.values();

        List<DelugeData> dataList = expansiveData();
        List<DelugeSettings> settingsList = expansiveSettings();
        for (DelugeSettings settings : settingsList) {
            for (ProgramType programType : programTypes) {
                if (DelugeProgram.hasArgument(programType)) {
                    if (!settings.hasArgumentArray()) {
                        continue;
                    }
                }
                else {
                    if (settings.hasArgumentArray()) {
                        continue;
                    }
                }

                for (DelugeData data : dataList) {
                    DelugeControl control = from(programType, data, settings);
                    control.runTest();
                }
            }
        }
    }

    @Test
    void testDelugeDataFlagging() {
        DelugeData data = DelugeData.forInputStream(BoltTestHelper.array("1", "2", "3"));
        assertNotNull(data.stream());
        assertFalse(data.isOpened());
        assertFalse(data.isClosed());
        try (InputStream inputStream = data.stream().get()) {
            assertNotNull(inputStream);
            assertTrue(data.isOpened());
            assertFalse(data.isClosed());
        }
        catch (Throwable e) {
            fail(e);
        }
        assertTrue(data.isOpened());
        assertTrue(data.isOpened());
    }

    private List<DelugeData> expansiveData() {
        List<DelugeData> list = new ArrayList<>();

        list.addAll(listForData(null));
        list.addAll(listForData(BoltTestHelper.array()));
        list.addAll(listForData(BoltTestHelper.array("Test")));
        list.addAll(listForData(BoltTestHelper.array("Hello World!\n\r\n", "Unicode(\ud801\udc10)", "")));

        list.addAll(listForData(BoltTestHelper.array("<", null)));
        list.addAll(listForData(BoltTestHelper.array(null, ">")));

        list.add(DelugeData.forResource("<null>", Runner.class, BoltTestHelper.array()));
        list.add(DelugeData.forResource("<null>", null, BoltTestHelper.array()));
        list.add(DelugeData.forResource(null, Runner.class, BoltTestHelper.array()));
        list.add(DelugeData.forResource(null, null, BoltTestHelper.array()));

        list.add(DelugeData.forResource("<null>", Runner.class, BoltTestHelper.array(), UTF_8));
        list.add(DelugeData.forResource("<null>", null, BoltTestHelper.array(), UTF_8));
        list.add(DelugeData.forResource(null, Runner.class, BoltTestHelper.array(), UTF_8));
        list.add(DelugeData.forResource(null, null, BoltTestHelper.array(), UTF_8));

        list.add(DelugeData.forResource("RunnerBlankScopeTest.txt", Runner.class, BoltTestHelper.array()));
        list.add(DelugeData.forResource("RunnerBlankScopeTest.txt", Runner.class, BoltTestHelper.array(), null));
        list.add(DelugeData.forResource("RunnerBlankScopeTest.txt", Runner.class, BoltTestHelper.array(), US_ASCII));
        list.add(DelugeData.forResource("RunnerTest.txt", Runner.class, BoltTestHelper.array("Hello World!", "1 ≤ A[i] ≤ 1014", "")));
        list.add(
            DelugeData.forResource("RunnerTest.txt", Runner.class, BoltTestHelper.array("Hello World!", "1 ≤ A[i] ≤ 1014", ""), UTF_8));
        list.add(DelugeData.forResource("RunnerTest.txt", null, BoltTestHelper.array("")));
        list.add(DelugeData.forResource("RunnerTest.txt", null, BoltTestHelper.array(""), ISO_8859_1));

        String[] oneToEight = {"1", "2", "3", "4", "5", "6", "7", "8"};
        list.add(DelugeData.forResource("RunnerTestUTF16.txt", Runner.class, oneToEight, UTF_16BE));

        return list;
    }

    private List<DelugeData> listForData(String[] data) {
        List<DelugeData> list = new ArrayList<>();
        Charset[] encodingArray = {null, UTF_8, UTF_16LE, UTF_16BE};
        list.add(DelugeData.forStringArray(data));
        list.add(DelugeData.forInputStream(data));
        for (Charset encoding : encodingArray) {
            list.add(DelugeData.forInputStream(data, encoding));
        }
        return list;
    }

    private List<DelugeSettings> expansiveSettings() {
        List<DelugeSettings> list = new ArrayList<>();
        Charset[] encodingArray = {null, UTF_8, UTF_16LE, UTF_16BE};
        Throwable[] throwableArray = {
            new Exception("deluge.exception.test"),
            new Exception("deluge.exception.test", null),
            new Exception("deluge.exception.test", new Exception((String) null)),
            new Exception("deluge.exception.test", new Exception("deluge.exception.cause.exception")),
            new Throwable("deluge.throwable.with.null.cause", null),
        };
        list.addAll(listForArgument(encodingArray, throwableArray, (String[]) null));
        list.addAll(listForArgument(encodingArray, throwableArray, "arg1", "arg2"));
        list.addAll(listForArgument(encodingArray, throwableArray, null, "arg2", ""));
        return list;
    }

    private List<DelugeSettings> listForArgument(Charset[] charsets, Throwable[] throwables, String... arguments) {
        List<DelugeSettings> list = new ArrayList<>();

        // Add with no charset, no throwable.
        list.add(DelugeSettings.from(arguments));

        // Add with charset, no throwable.
        for (Charset charset : charsets) {
            list.add(DelugeSettings.from(arguments, charset));
        }

        // Add with no charset, throwing.
        for (Throwable throwable : throwables) {
            if (throwable != null) {
                list.add(DelugeSettings.from(throwable));
            }
        }

        // Add with no charset, argument, and throwing.
        for (Throwable throwable : throwables) {
            list.add(DelugeSettings.from(arguments, throwable));
        }

        // Add with charset, throwing.
        for (Charset charset : charsets) {
            if (charset != null) {
                for (Throwable throwable : throwables) {
                    list.add(DelugeSettings.from(throwable, charset));
                    list.add(DelugeSettings.from(arguments, throwable, charset));
                }
            }
        }

        return list;
    }

}
