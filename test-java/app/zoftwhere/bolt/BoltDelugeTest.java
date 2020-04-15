package app.zoftwhere.bolt;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import app.zoftwhere.bolt.deluge.DelugeBuilder;
import app.zoftwhere.bolt.deluge.DelugeException;
import app.zoftwhere.bolt.deluge.DelugeProgramType;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.array;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;

class BoltDelugeTest {

    @Test
    void barrageTest() {
        DelugeProgramType[] programTypes = DelugeProgramType.values();

        for (DelugeBuilder builder : expansiveSettings()) {
            for (DelugeProgramType programType : programTypes) {
                if (programType.isArgued() != builder.hasArgumentArray()) {
                    continue;
                }

                for (DelugeBuilder unit : expansiveData(builder.forProgram(programType))) {
                    if (!unit.hasProgramType() || !unit.hasSettings() || !unit.hasData()) {
                        throw new DelugeException("deluge.builder.failed");
                    }

                    unit.runTest();
                }
            }
        }
    }

    private List<DelugeBuilder> expansiveData(DelugeBuilder builder) {
        final var arraySingleNull = new String[] {null};
        List<DelugeBuilder> list = new ArrayList<>();

        list.addAll(listForData(builder, null));
        list.addAll(listForData(builder, arraySingleNull));
        list.addAll(listForData(builder, array()));
        list.addAll(listForData(builder, array("ListForDataTest")));
        list.addAll(listForData(builder, array("Hello World!\r", "\nUnicode(\ud801\udc10)", "")));

        list.addAll(listForData(builder, array("<", null)));
        list.addAll(listForData(builder, array(null, ">")));

        list.add(builder.forInputStream(new NullPointerException("InputStreamExceptionTest")));
        list.add(builder.forInputStream(new NullPointerException("InputStreamExceptionTest"), UTF_8));

        list.add(builder.forResource("<null>", Runner.class, array()));
        list.add(builder.forResource("<null>", null, array()));
        list.add(builder.forResource(null, Runner.class, array()));
        list.add(builder.forResource(null, null, array()));

        list.add(builder.forResource("<null>", Runner.class, array(), UTF_8));
        list.add(builder.forResource("<null>", null, array(), UTF_8));
        list.add(builder.forResource(null, Runner.class, array(), UTF_8));
        list.add(builder.forResource(null, null, array(), UTF_8));
        list.add(builder.forResource("RunnerTest.txt", null, array("")));
        list.add(builder.forResource("RunnerTest.txt", null, array(""), null));
        list.add(builder.forResource("RunnerTest.txt", null, array(""), ISO_8859_1));

        list.add(builder.forResource("RunnerBlankScopeTest.txt", Runner.class, array()));
        list.add(builder.forResource("RunnerBlankScopeTest.txt", Runner.class, array(), US_ASCII));

        String[] helloWorld = array("Hello World!", "1 ≤ A[i] ≤ 1014", "");
        list.add(builder.forResource("RunnerTest.txt", Runner.class, helloWorld));
        list.add(builder.forResource("RunnerTest.txt", Runner.class, helloWorld, UTF_8));

        String[] oneToEight = {"1", "2", "3", "4", "5", "6", "7", "8"};
        list.add(builder.forResource("RunnerTestUTF16.txt", Runner.class, oneToEight, UTF_16BE));

        return list;
    }

    private List<DelugeBuilder> listForData(DelugeBuilder builder, String[] data) {
        List<DelugeBuilder> list = new ArrayList<>();
        Charset[] encodingArray = {null, UTF_8, UTF_16LE, UTF_16BE};
        list.add(builder.forStringArray(data));
        list.add(builder.forInputStream(data));
        for (Charset encoding : encodingArray) {
            list.add(builder.forInputStream(data, encoding));
        }
        return list;
    }

    private List<DelugeBuilder> expansiveSettings() {
        List<DelugeBuilder> list = new ArrayList<>();
        Charset[] encodingArray = {null, UTF_8, UTF_16LE, UTF_16BE};
        Exception[] exceptionArray = {
            new Exception("deluge.exception.test"),
            new Exception("deluge.exception.test", null),
            new Exception("deluge.exception.test", new Exception((String) null)),
            new Exception("deluge.exception.test", new Exception("deluge.exception.cause.exception")),
            new RuntimeException("deluge.runtime.exception.with.null.cause", null),
        };
        list.addAll(listForArgument(encodingArray, exceptionArray, (String[]) null));
        list.addAll(listForArgument(encodingArray, exceptionArray, "arg1", "arg2"));
        list.addAll(listForArgument(encodingArray, exceptionArray, null, "arg2", ""));
        return list;
    }

    private List<DelugeBuilder> listForArgument(Charset[] charsets, Exception[] errors, String... arguments) {
        List<DelugeBuilder> list = new ArrayList<>();
        DelugeBuilder builder = new DelugeBuilder();

        // Add with no charset, no throwable.
        list.add(builder.withSettings(arguments));

        // Add with charset, no throwable.
        for (Charset charset : charsets) {
            list.add(builder.withSettings(arguments, charset));
        }

        // Add with no charset, throwing.
        for (Exception error : errors) {
            if (error != null) {
                list.add(builder.withSettings(error));
            }
        }

        // Add with no charset, argument, and throwing.
        for (Exception error : errors) {
            list.add(builder.withSettings(arguments, error));
        }

        // Add with charset, throwing.
        for (Charset charset : charsets) {
            if (charset != null) {
                for (Exception error : errors) {
                    list.add(builder.withSettings(error, charset));
                    list.add(builder.withSettings(arguments, error, charset));
                }
            }
        }

        return list;
    }

}
