package app.zoftwhere.bolt.runner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import app.zoftwhere.bolt.runner.Runner.RunnerOutput;
import app.zoftwhere.bolt.runner.Runner.RunnerProgram;
import app.zoftwhere.mutable.MutableValue;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.runner.Runner.newRunner;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RunnerTest {

    @Test
    void testScanner() {
        final MutableValue<String[]> input = new MutableValue<>();

        newRunner().input("one", "two", "three\r\nfour\nfive", "\n\r\n")
            .run((scanner, bufferedWriter) -> {
                List<String> list = new ArrayList<>();
                while (scanner.hasNext()) {
                    list.add(scanner.nextLine());
                }
                final int size = list.size();
                input.set(list.toArray(new String[size]));
                final String[] array = input.get();
                if (size > 0) {
                    bufferedWriter.write(array[0]);
                    for (int i = 1; i < size; i++) {
                        bufferedWriter.newLine();
                        bufferedWriter.write(array[i]);
                    }
                }
                bufferedWriter.flush();
            })
            .expected("one", "two", "three", "four", "five")
            .assertResult();
    }

    @Test
    void testInputStreamClose() {
        final Charset charset = StandardCharsets.UTF_16BE;
        final MutableValue<Boolean> closedFlag = new MutableValue<>(Boolean.FALSE);
        assertTrue(closedFlag.isPresent());
        assertFalse(closedFlag.get());

        final InputStream inputStream = new ByteArrayInputStream("–Great–".getBytes(charset)) {
            @Override
            public void close() throws IOException {
                closedFlag.set(Boolean.TRUE);
                super.close();
            }
        };

        newRunner().input(() -> inputStream)
            .runConsole((scanner, bufferedWriter) -> {})
            .expected();

        assertNotNull(closedFlag);
        assertTrue(closedFlag.isPresent());
        assertTrue(closedFlag.get());
    }

    @Test
    void testCallerFirst() {
        RunnerOutput runnerOutput = newRunner()
            .runConsole((scanner, bufferedWriter) -> {})
            .input();

        assertNotNull(runnerOutput.output());
        assertNull(runnerOutput.exception());
        runnerOutput.expected().assertResult();
        runnerOutput.expected("").assertResult();

        RunnerOutput resultBlank = newRunner()
            .runConsole((scanner, bufferedWriter) -> {})
            .input("");

        assertNotNull(resultBlank.output());
        assertNull(resultBlank.exception());
        resultBlank.expected().assertResult();
        resultBlank.expected("").assertResult();
    }

    @Test
    void testInputFirst() {
        RunnerOutput resultEmpty = newRunner()
            .input()
            .runConsole((scanner, bufferedWriter) -> {});

        assertNotNull(resultEmpty.output());
        assertNull(resultEmpty.exception());
        resultEmpty.expected().assertResult();
        resultEmpty.expected("").assertResult();

        RunnerOutput resultBlank = newRunner()
            .input("")
            .argument("")
            .runConsole((arguments, scanner, bufferedWriter) -> {});

        assertNotNull(resultBlank.output());
        assertNull(resultBlank.exception());
        resultBlank.expected().assertResult();
        resultBlank.expected("").assertResult();
    }

    @Test
    void testLoadingInput() {
        RunnerProgram program = newRunner()
            .run((scanner, writer) -> {
                while (scanner.hasNext()) {
                    writer.write(scanner.nextLine());
                    writer.newLine();
                }
            });

        program.loadInput("RunnerTest.txt", getClass())
            .expected("Hello World!", "1 ≤ A[i] ≤ 1014")
            .assertResult();

        program.loadInput("RunnerTest.txt", getClass(), UTF_8)
            .expected("Hello World!", "1 ≤ A[i] ≤ 1014")
            .assertResult();

        program.loadInput("RunnerTest.txt", getClass(), UTF_8, UTF_8)
            .expected("Hello World!", "1 ≤ A[i] ≤ 1014")
            .assertResult();
    }

    @Test
    void testLoadingExpectation() {
        RunnerOutput r = newRunner().run((scanner, writer) -> {
            writer.write("Hello World!\n");
            writer.write("1 ≤ A[i] ≤ 1014\n");
        }).input();

        r.loadExpectation("RunnerTest.txt", getClass())
            .assertResult();

        r.loadExpectation("RunnerTest.txt", getClass(), UTF_8)
            .assertResult();

        r.loadExpectation("RunnerTest.txt", getClass(), UTF_8)
            .assertResult();
    }

}
