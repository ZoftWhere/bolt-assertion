package app.zoftwhere.bolt.runner.scope;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import app.zoftwhere.bolt.runner.RunnerSplitter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RunnerSplitterScopeTest {

    private final RunnerSplitter splitter = new RunnerSplitter();

    @Test
    void testScope() {
        List<String> list;

        assertNotNull(splitter);

        list = splitter.getList("");
        assertNotNull(list);
        assertEquals(1, list.size());

        list = splitter.getList(new ByteArrayInputStream(new byte[0]), StandardCharsets.UTF_8);
        assertNotNull(list);
        assertEquals(1, list.size());

        list = splitter.getList(new Scanner(""));
        assertNotNull(list);
        assertEquals(1, list.size());
    }

}