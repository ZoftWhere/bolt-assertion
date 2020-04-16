package app.zoftwhere.bolt;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.Runner.newRunner;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class RunnerTest {

    private final Runner runner = newRunner();

    @Test
    void testDeprecatedThrowable() {
        boolean caught = false;
        try {
            runner.input()
                .run((scanner, out) -> {
                    throw new Error("bolt.runner.throwable.deprecated");
                })
                .expected()
                .assertError();
            caught = true;
        }
        catch (Throwable ignore) {
        }

        if (caught) {
            fail("bolt.runner.test.error.throwable.deprecated");
        }
    }

    @Test
    void testPartialOutput() {
        var result = runner.input("1", "2", "3", "4.5")
            .run((scanner, out) -> {
                scanner.useDelimiter("\\R");
                while (scanner.hasNext()) {
                    out.printf("%d%n", Integer.parseInt(scanner.next()));
                }
            })
            .expected("1", "2", "3")
            .result();

        Exception error = result.error().orElse(null);
        assertNotNull(error);
        BoltTestHelper.assertClass(NumberFormatException.class, error);
    }

}
