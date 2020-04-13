package app.zoftwhere.bolt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerAsserter;
import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerProgramResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingConsumer;

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

    @Test
    void testLoadComparator() {
        var output = new BoltProgramOutput(new String[] {""}, null);
        var test = output.comparator(null);
        assertClass(BoltProgramOutput.class, test);
        var exception = test.exception().orElse(null);
        assertNotNull(exception);
        assertClass(RunnerException.class, exception);
        var errorReason = "bolt.runner.expectation.comparator.null";
        assertEquals(errorReason, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testLoadComparatorSkip() throws Throwable {
        var throwable = new NullPointerException("comparator.load.skip");
        var output = new BoltProgramOutput(new String[] {""}, throwable).comparator(null);

        ThrowingConsumer<RunnerProgramOutput> check = programOutput -> {
            Exception exception = programOutput.exception().orElse(null);
            assertNotNull(exception);
            assertClass(NullPointerException.class, exception);
            assertEquals("comparator.load.skip", exception.getMessage());
            assertNull(exception.getCause());
        };

        check.accept((RunnerProgramOutput) output);
    }

    /** Test loading empty, blank, and null expectation. */
    @Test
    void testSpaceLoad() throws Throwable {
        ThrowingConsumer<RunnerAsserter> check = asserter -> {
            RunnerProgramResult result = asserter.result();
            Exception exception = result.exception().orElse(null);
            String[] output = result.output();
            String[] expected = result.expected();

            assertEquals(1, expected.length);
            assertEquals("", expected[0]);
            assertNotNull(output);
            assertEquals(1, output.length);
            assertEquals("", output[0]);
        };

        var programOutput = new BoltProgramOutput(new String[] {""}, null);
        var emptyArray = new String[] { };
        var blankArray = new String[] {""};

        check.accept(programOutput.expected((String[]) null));
        check.accept(programOutput.expected());
        check.accept(programOutput.expected(""));
        check.accept(programOutput.expected(emptyArray));
        check.accept(programOutput.expected(blankArray));
    }

    @Test
    void testLoad() throws Throwable {
        var output = new BoltProgramOutput(new String[] {""}, null);
        var errorClass = new BoltPlaceHolder<Class<?>>(RunnerException.class);
        var errorMessage = new BoltPlaceHolder<String>(null);
        var expectationLength = new BoltPlaceHolder<Integer>(null);
        ThrowingConsumer<RunnerAsserter> check = asserter -> {
            asserter.assertException();
            asserter.assertCheck(result -> {
                String[] expected = result.expected();
                assertNotNull(expected);
                assertEquals(expectationLength.get().intValue(), expected.length);
                Exception exception = result.exception().orElse(null);
                assertNotNull(exception);
                assertClass(errorClass.get(), exception);
                assertEquals(errorMessage.get(), exception.getMessage());
                assertNull(exception.getCause());
                expectationLength.set(null);
            });
        };

        errorMessage.set("bolt.runner.variable.array.expected.has.null");
        var nullArray = new String[] {null};
        expectationLength.set(1);
        check.accept(output.expected(nullArray));

        errorMessage.set("bolt.runner.variable.array.expected.has.null");
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
    void testLoadSkip() throws Throwable {
        var errorMessage = "expectation.load.skip";
        var throwable = new NullPointerException(errorMessage);
        var output = new BoltProgramOutput(new String[] {""}, throwable).comparator(null);
        var expectationLength = new BoltPlaceHolder<Integer>(null);

        ThrowingConsumer<RunnerAsserter> check = asserter -> {
            asserter.assertException();
            asserter.assertCheck(result -> {
                String[] expected = result.expected();
                Integer length = expectationLength.get();
                assertNotNull(expected);
                assertNotNull(length);
                assertEquals(length.intValue(), expected.length);
                Exception exception = result.exception().orElse(null);
                assertNotNull(exception);
                assertClass(throwable.getClass(), exception);
                assertEquals(errorMessage, exception.getMessage());
                assertNull(exception.getCause());
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
    void testLoadResource() throws Throwable {
        var output = new BoltProgramOutput(new String[] {""}, null);
        var names = new String[] {null, "notFound", "RunnerTest.txt"};
        var withClasses = new Class<?>[] {null, Runner.class, RunnerProgramOutput.class};
        var charsets = new Charset[] {null, UTF_8, US_ASCII};
        var errorMessageHolder = new BoltPlaceHolder<String>(null);

        final ThrowingConsumer<RunnerAsserter> check;
        check = asserter -> {
            var errorMessage = errorMessageHolder.get();

            if (errorMessage != null) {
                asserter.assertCheck(result -> {
                    assertNotNull(result.expected());
                    assertEquals(0, result.expected().length);
                    Exception exception = result.exception().orElse(null);
                    assertNotNull(exception);
                    assertClass(RunnerException.class, exception);
                    assertEquals(errorMessage, exception.getMessage());
                    assertNull(exception.getCause());
                });
            }
            else {
                asserter.assertFailure();
                asserter.assertCheck(result -> {
                    assertNotNull(result.expected());
                    assertTrue(result.expected().length > 0);
                    assertFalse(result.isException());
                    assertTrue(result.isFailure());
                    String failureReason = "bolt.runner.asserter.output.length.mismatch";
                    assertEquals(failureReason, result.message().orElse(null));
                });
            }
        };

        for (String name : names) {
            for (Class<?> withClass : withClasses) {
                var asserter = output.loadExpectation(name, withClass);
                String errorMessage = name == null ? "bolt.runner.load.expectation.resource.name.null"
                    : withClass == null ? "bolt.runner.load.expectation.resource.class.null"
                    : withClass.getResource(name) == null ? "bolt.runner.load.expectation.resource.not.found"
                    : null;
                errorMessageHolder.set(errorMessage);
                check.accept(asserter);
            }
        }

        for (String name : names) {
            for (Class<?> withClass : withClasses) {
                for (Charset charset : charsets) {
                    var asserter = output.loadExpectation(name, withClass, charset);
                    String errorMessage = charset == null ? "bolt.runner.load.expectation.charset.null"
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
    void testLoadResourceSkip() throws Throwable {
        var output = new String[] {"testLoadResourceSkip", ""};
        var error = new NullPointerException("resource.load.skip");
        var programOutput = new BoltProgramOutput(output, error);
        var names = new String[] {null, "notFound", "RunnerTest.txt"};
        var withClasses = new Class<?>[] {null, Runner.class, RunnerProgramOutput.class};
        var charsets = new Charset[] {null, UTF_8, US_ASCII};

        ThrowingConsumer<RunnerAsserter> check = asserter -> {
            asserter.assertException();
            asserter.assertCheck(result -> {
                assertArrayEquals(output, result.output());
                var expectation = result.expected();
                assertNotNull(expectation);
                assertEquals(0, expectation.length);
                Exception exception = result.exception().orElse(null);
                assertNotNull(exception);
                assertClass(error.getClass(), exception);
                assertEquals(error.getMessage(), exception.getMessage());
                assertNull(exception.getCause());
            });
        };

        for (String name : names) {
            for (Class<?> withClass : withClasses) {
                var asserter = programOutput.loadExpectation(name, withClass);
                check.accept(asserter);
            }
        }

        for (String name : names) {
            for (Class<?> withClass : withClasses) {
                for (Charset charset : charsets) {
                    var asserter = programOutput.loadExpectation(name, withClass, charset);
                    check.accept(asserter);
                }
            }
        }
    }

    @Test
    void testLoadInputStream() throws Throwable {
        final var output = new String[] {"program", "output", ""};
        final var programOutput = new BoltProgramOutput(output, null);
        final var encodingArray = new Charset[] {null, UTF_8, US_ASCII, UTF_16BE, UTF_16LE};
        final var expectation = new String[] {"expectation", "test"};
        ThrowingConsumer<RunnerProgramResult> check = (RunnerProgramResult result) -> {
            assertArrayEquals(output, result.output());
            assertArrayEquals(expectation, result.expected());
        };

        for (var encoding : encodingArray) {
            RunnerProgramResult result;
            if (encoding == null) {
                InputStreamSupplier supplier = forStringArray(expectation, UTF_8);
                result = programOutput.expected(supplier).result();
            }
            else {
                InputStreamSupplier supplier = forStringArray(expectation, encoding);
                result = programOutput.expected(supplier, encoding).result();
            }
            check.accept(result);
        }
    }

    private InputStreamSupplier forStringArray(String[] input, Charset charset) {
        return () -> {
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                try (OutputStreamWriter writer = new OutputStreamWriter(output, charset)) {
                    writer.append(input[0]);
                    for (int i = 1, s = input.length; i < s; i++) {
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