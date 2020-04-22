package app.zoftwhere.bolt.deluge;

import app.zoftwhere.bolt.Runner;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DelugeMockTest {

    @Test
    void testTransCodeInputUTF8() {
        DelugeProgramType type = DelugeProgramType.INPUT_CONSOLE;
        DelugeSetting setting = DelugeSetting.from(UTF_8, true);
        DelugeData data = DelugeData.forStringArray(new String[] {"\ufeffTestInputArray"}, UTF_16);
        DelugeProgramOutput programOutput = DelugeMock.from(type, setting, data).buildExpectedOutput();
        String[] actual = programOutput.output();
        String[] expected = new String[] {
            "Argument: <null>",
            "Line: \"TestInputArray\""
        };
        assertNull(programOutput.error());
        assertArrayEquals(expected, actual);
    }

    @Test
    void testTransCodeResourceUTF8() {
        DelugeProgramType type = DelugeProgramType.INPUT_CONSOLE;
        DelugeSetting setting = DelugeSetting.from(US_ASCII, true);
        DelugeData data = DelugeData.forResource("RunnerTest.txt", Runner.class);
        DelugeProgramOutput programOutput = DelugeMock.from(type, setting, data).buildExpectedOutput();
        String[] actual = programOutput.output();
        String[] expected = new String[] {
            "Argument: <null>",
            "Line: \"Hello World!\"",
            "Line: \"1 ??? A[i] ??? 1014\"",
            "Line: \"\""
        };
        assertNull(programOutput.error());
        assertArrayEquals(expected, actual);
    }

    @Test
    void testTransCodeResourceUTF16() {
        DelugeProgramType type = DelugeProgramType.INPUT_CONSOLE;
        DelugeSetting setting = DelugeSetting.from(US_ASCII, UTF_8);
        DelugeData data = DelugeData.forResource("RunnerTestUTF16.txt", Runner.class, UTF_16);
        DelugeProgramOutput programOutput = DelugeMock.from(type, setting, data).buildExpectedOutput();
        String[] actual = programOutput.output();
        String[] expected = new String[] {
            "Argument: <null>",
            "Line: \"1\"",
            "Line: \"2\"",
            "Line: \"3\"",
            "Line: \"4\"",
            "Line: \"5\"",
            "Line: \"6\"",
            "Line: \"7\"",
            "Line: \"8\""
        };
        assertNull(programOutput.error());
        assertArrayEquals(expected, actual);
    }

}