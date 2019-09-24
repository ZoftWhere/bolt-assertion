package app.zoftwhere.bolt.runner;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RunnerSplitterTest {

    private final RunnerSplitter splitter = new RunnerSplitter();

    @Test
    void testInputSplitting() {
        testThis("empty", "");
        testThis("blank", " ");
        testThis("new1", "", "");
        testThis("new2", "", "", "");
        testThis("new3", "", "", "", "");
    }

    private void testThis(String test, String... array) {
        final int size = array.length;
        StringBuilder builder = new StringBuilder();
        if (size > 0) {
            builder.append(array[0]);
        }
        for (int i = 1; i < size; i++) {
            builder.append("\n").append(array[i]);
        }
        String input = builder.toString();
        final var list = splitter.getList(new ByteArrayInputStream(input.getBytes(UTF_8)), UTF_8);

        if (array.length != list.size()) {
            assertEquals(array.length, list.size(), test + " [" + Arrays.toString(list.toArray()) + "]");
        }
        for (int i = 0; i < size; i++) {
            assertEquals(array[i], list.get(i), test);
        }
    }

}
