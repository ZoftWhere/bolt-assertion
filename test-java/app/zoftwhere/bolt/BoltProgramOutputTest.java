package app.zoftwhere.bolt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Comparator;
import java.util.function.Consumer;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerResult;
import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoltProgramOutputTest {

    private final Charset ENCODING = Runner.DEFAULT_ENCODING;

    private final Duration instant = Duration.ZERO;

    @Test
    void testLoadNullComparator() {
        final var output = new BoltProgramOutput(ENCODING, new String[] {""}, instant, null);
        final var test = output.comparator(null);
        assertClass(BoltProgramOutput.class, test);
        final var exception = test.error().orElse(null);
        assertNotNull(exception);
        assertClass(RunnerException.class, exception);
        final var errorReason = "bolt.runner.expectation.comparator.null";
        assertEquals(errorReason, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testComparatorMatch() {
        final var output = new String[] {"one", "two"};
        final var result = new BoltProgramOutput(ENCODING, output, instant, null)
            .comparator(Comparator.nullsFirst(Comparator.naturalOrder()))
            .expected(output)
            .result();
        final var message = result.message().orElse(null);
        final var exception = result.error().orElse(null);
        assertNull(message);
        assertNull(exception);
    }

    @Test
    void testComparatorLengthMismatch() {
        final var output = new String[] {"one", "two"};
        final var result = new BoltProgramOutput(ENCODING, output, instant, null)
            .comparator(Comparator.nullsFirst(Comparator.naturalOrder()))
            .expected("")
            .result();
        final var message = result.message().orElse(null);
        final var exception = result.error().orElse(null);
        assertEquals("bolt.runner.asserter.output.length.mismatch", message);
        assertNull(exception);
    }

    @Test
    void testComparatorDataMismatch() {
        final var output = new String[] {"one", "two"};
        final var result = new BoltProgramOutput(ENCODING, output, instant, null)
            .comparator(Comparator.nullsFirst(Comparator.naturalOrder()))
            .expected("", "")
            .result();
        final var message = result.message().orElse(null);
        final var exception = result.error().orElse(null);
        assertEquals("bolt.runner.asserter.output.data.mismatch", message);
        assertNull(exception);
    }

    @Test
    void testLoadComparatorSkip() {
        final var exception = new NullPointerException("comparator.load.skip");
        final var output = new BoltProgramOutput(ENCODING, new String[] {""}, instant, exception).comparator(null);

        Consumer<RunnerProgramOutput> check = programOutput -> {
            final var error = programOutput.error().orElse(null);
            assertNotNull(error);
            assertClass(NullPointerException.class, error);
            assertEquals("comparator.load.skip", error.getMessage());
            assertNull(error.getCause());
        };

        check.accept((RunnerProgramOutput) output);
    }

    /** Test loading empty, blank, and null expectation. */
    @Test
    void testSpaceLoad() {
        Consumer<RunnerAsserter> check = asserter -> {
            final var result = asserter.result();
            final var output = result.output();
            final var expected = result.expected();
            final var error = result.error().orElse(null);

            assertNull(error);
            assertEquals(1, expected.length);
            assertEquals("", expected[0]);
            assertNotNull(output);
            assertEquals(1, output.length);
            assertEquals("", output[0]);
        };

        final var programOutput = new BoltProgramOutput(ENCODING, new String[] {""}, instant, null);
        final var emptyArray = new String[] { };
        final var blankArray = new String[] {""};

        check.accept(programOutput.expected((String[]) null));
        check.accept(programOutput.expected());
        check.accept(programOutput.expected(""));
        check.accept(programOutput.expected(emptyArray));
        check.accept(programOutput.expected(blankArray));
    }

    @Test
    void testLoad() {
        final var output = new BoltProgramOutput(ENCODING, new String[] {""}, instant, null);
        final var errorClass = new BoltPlaceHolder<Class<?>>(RunnerException.class);
        final var errorMessage = new BoltPlaceHolder<String>(null);
        final var expectationLength = new BoltPlaceHolder<Integer>(null);
        Consumer<RunnerAsserter> check = asserter -> {
            asserter.assertError();
            asserter.assertCheck(result -> {
                final var expected = result.expected();
                assertNotNull(expected);
                assertEquals(expectationLength.get().intValue(), expected.length);
                final var error = result.error().orElse(null);
                assertNotNull(error);
                assertClass(errorClass.get(), error);
                assertEquals(errorMessage.get(), error.getMessage());
                assertNull(error.getCause());
                expectationLength.set(null);
            });
        };

        errorMessage.set("bolt.runner.variable.argument.expected.has.null");
        final var nullArray = new String[] {null};
        expectationLength.set(1);
        check.accept(output.expected(nullArray));

        errorMessage.set("bolt.runner.variable.argument.expected.has.null");
        expectationLength.set(3);
        check.accept(output.expected("", null, ""));

        errorMessage.set("bolt.runner.load.expectation.supplier.null");
        expectationLength.set(0);
        check.accept(output.expected((InputStreamSupplier) null));

        errorMessage.set("bolt.runner.load.expectation.charset.null");
        expectationLength.set(0);
        check.accept(output.expected(null, null));

        errorMessage.set("bolt.runner.load.expectation.supplier.null");
        expectationLength.set(0);
        check.accept(output.expected(null, UTF_8));

        errorMessage.set("bolt.runner.load.expectation.stream.null");
        expectationLength.set(0);
        check.accept(output.expected(() -> null));

        errorMessage.set("bolt.runner.load.expectation.charset.null");
        expectationLength.set(0);
        check.accept(output.expected(() -> null, null));

        errorMessage.set("bolt.runner.load.expectation.stream.null");
        expectationLength.set(0);
        check.accept(output.expected(() -> null, UTF_8));
    }

    @Test
    void testLoadSkip() {
        final var errorMessage = "expectation.load.skip";
        final var exception = new NullPointerException(errorMessage);
        final var output = new BoltProgramOutput(ENCODING, new String[] {""}, instant, exception).comparator(null);
        final var expectationLength = new BoltPlaceHolder<Integer>(null);

        Consumer<RunnerAsserter> check = asserter -> {
            asserter.assertError();
            asserter.assertCheck(result -> {
                final var expected = result.expected();
                final var length = expectationLength.get();
                assertNotNull(expected);
                assertNotNull(length);
                assertEquals(length.intValue(), expected.length);
                final var error = result.error().orElse(null);
                assertNotNull(error);
                assertClass(exception.getClass(), error);
                assertEquals(errorMessage, error.getMessage());
                assertNull(error.getCause());
                expectationLength.set(null);
            });
        };

        expectationLength.set(3);
        check.accept(output.expected("", null, ""));

        expectationLength.set(0);
        check.accept(output.expected((InputStreamSupplier) null));

        expectationLength.set(0);
        check.accept(output.expected(null, null));

        expectationLength.set(0);
        check.accept(output.expected(null, UTF_8));

        expectationLength.set(0);
        check.accept(output.expected(() -> null));

        expectationLength.set(0);
        check.accept(output.expected(() -> null, null));

        expectationLength.set(0);
        check.accept(output.expected(() -> null, UTF_8));
    }

    @Test
    void testLoadResource() {
        final var output = new BoltProgramOutput(UTF_8, new String[] {""}, instant, null);
        final var names = new String[] {null, "notFound", "RunnerTest.txt"};
        final var withClasses = new Class<?>[] {null, Runner.class, RunnerProgramOutput.class};
        final var charsets = new Charset[] {null, UTF_8, US_ASCII};
        final var errorMessageHolder = new BoltPlaceHolder<String>(null);

        final Consumer<RunnerAsserter> check;
        check = asserter -> {
            final var errorMessage = errorMessageHolder.get();

            if (errorMessage != null) {
                asserter.assertCheck(result -> {
                    assertNotNull(result.expected());
                    assertEquals(0, result.expected().length);
                    final var error = result.error().orElse(null);
                    assertNotNull(error);
                    assertClass(RunnerException.class, error);
                    assertEquals(errorMessage, error.getMessage());
                    assertNull(error.getCause());
                });
            }
            else {
                asserter.assertFailure();
                asserter.assertCheck(result -> {
                    assertNotNull(result.expected());
                    assertTrue(result.expected().length > 0);
                    assertFalse(result.isError());
                    assertTrue(result.isFailure());
                    final var failureReason = "bolt.runner.asserter.output.length.mismatch";
                    assertEquals(failureReason, result.message().orElse(null));
                });
            }
        };

        for (var name : names) {
            for (var withClass : withClasses) {
                final var asserter = output.loadExpectation(name, withClass);
                final var errorMessage = name == null ? "bolt.runner.load.expectation.resource.name.null"
                    : withClass == null ? "bolt.runner.load.expectation.resource.class.null"
                    : withClass.getResource(name) == null ? "bolt.runner.load.expectation.resource.not.found"
                    : null;
                errorMessageHolder.set(errorMessage);
                check.accept(asserter);
            }
        }

        for (var name : names) {
            for (var withClass : withClasses) {
                for (var charset : charsets) {
                    final var asserter = output.loadExpectation(name, withClass, charset);
                    final var errorMessage = charset == null ? "bolt.runner.load.expectation.charset.null"
                        : name == null ? "bolt.runner.load.expectation.resource.name.null"
                        : withClass == null ? "bolt.runner.load.expectation.resource.class.null"
                        : withClass.getResource(name) == null ? "bolt.runner.load.expectation.resource.not.found"
                        : null;
                    errorMessageHolder.set(errorMessage);
                    check.accept(asserter);
                }
            }
        }
    }

    @Test
    void testLoadResourceSkip() {
        final var output = new String[] {"testLoadResourceSkip", ""};
        final var exception = new NullPointerException("resource.load.skip");
        final var programOutput = new BoltProgramOutput(ENCODING, output, instant, exception);
        final var names = new String[] {null, "notFound", "RunnerTest.txt"};
        final var withClasses = new Class<?>[] {null, Runner.class, RunnerProgramOutput.class};
        final var charsets = new Charset[] {null, UTF_8, US_ASCII};

        Consumer<RunnerAsserter> check = asserter -> {
            asserter.assertError();
            asserter.assertCheck(result -> {
                assertArrayEquals(output, result.output());
                final var expectation = result.expected();
                assertNotNull(expectation);
                assertEquals(0, expectation.length);
                final var error = result.error().orElse(null);
                assertNotNull(error);
                assertClass(error.getClass(), error);
                assertEquals(error.getMessage(), error.getMessage());
                assertNull(error.getCause());
            });
        };

        for (var name : names) {
            for (var withClass : withClasses) {
                final var asserter = programOutput.loadExpectation(name, withClass);
                check.accept(asserter);
            }
        }

        for (var name : names) {
            for (var withClass : withClasses) {
                for (var charset : charsets) {
                    final var asserter = programOutput.loadExpectation(name, withClass, charset);
                    check.accept(asserter);
                }
            }
        }
    }

    @Test
    void testLoadInputStream() {
        final var output = new String[] {"program", "output", ""};
        final var programOutput = new BoltProgramOutput(ENCODING, output, instant, null);
        final var encodingArray = new Charset[] {null, UTF_8, US_ASCII, UTF_16BE, UTF_16LE};
        final var expectation = new String[] {"expectation", "test"};
        Consumer<RunnerResult> check = (RunnerResult result) -> {
            assertArrayEquals(output, result.output());
            assertArrayEquals(expectation, result.expected());
        };

        for (var encoding : encodingArray) {
            RunnerResult result;
            if (encoding == null) {
                final var supplier = forStringArray(expectation, UTF_8);
                result = programOutput.expected(supplier).result();
            }
            else {
                final var supplier = forStringArray(expectation, encoding);
                result = programOutput.expected(supplier, encoding).result();
            }
            check.accept(result);
        }
    }

    @Test
    void testOutputArrayCopy() {
        // The constructor caller takes responsibility for input arrays.
        final var output = new String[] {"index0", "index1", "index2"};
        final var programOutput = new BoltProgramOutput(ENCODING, output, instant, null);
        output[0] = "changed0";
        final var copy1 = programOutput.output();
        copy1[1] = "changed1";
        output[2] = "changed2";
        final var copy2 = programOutput.output();
        assertArrayEquals(output, new String[] {"changed0", "index1", "changed2"});
        assertArrayEquals(copy1, new String[] {"changed0", "changed1", "index2"});
        assertArrayEquals(copy2, new String[] {"changed0", "index1", "changed2"});
    }

    private InputStreamSupplier forStringArray(String[] input, Charset charset) {
        return () -> {
            try (final var output = new ByteArrayOutputStream()) {
                try (final var writer = new OutputStreamWriter(output, charset)) {
                    writer.append(input[0]);
                    final var s = input.length;
                    for (var i = 1; i < s; i++) {
                        writer.append(System.lineSeparator());
                        writer.append(input[i]);
                    }
                    writer.flush();
                }
                return new ByteArrayInputStream(output.toByteArray());
            }
        };
    }

}
