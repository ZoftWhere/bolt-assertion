package app.zoftwhere.bolt.scope;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import app.zoftwhere.bolt.nio.LineSplitter;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LineSplitterScopeTest {

    @Test
    void testScope() throws IOException {
        List<String> list;

        list = new LineSplitter("").list();
        assertNotNull(list);
        assertEquals(1, list.size());

        list = new LineSplitter(new ByteArrayInputStream(new byte[0]), UTF_8).list();
        assertNotNull(list);
        assertEquals(1, list.size());

        list = new LineSplitter(new byte[0], UTF_8).list();
        assertNotNull(list);
        assertEquals(1, list.size());

        list = new LineSplitter(new Scanner("")).list();
        assertNotNull(list);
        assertEquals(1, list.size());
    }

}