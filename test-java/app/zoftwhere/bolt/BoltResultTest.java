package app.zoftwhere.bolt;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.array;
import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class BoltResultTest {

    private final String[] emptyArray = new String[] { };
    private final String[] blankArray = new String[] {""};
    private final String[] nullArray = new String[] {null};
    private final Duration instant = Duration.ZERO;

    @Test
    void testSuccessState() {
        final var blank = new String[] {""};
        final var fire = new BoltResult(blank, blank, instant);

        fire.assertSuccess();

        assertTrue(fire.isSuccess());
        assertFalse(fire.isFailure());
        assertFalse(fire.isError());

        assertNull(fire.message().orElse(null));
        assertNull(fire.error().orElse(null));

        try {
            fire.assertFailure();
            fail("bolt.runner.asserter.success.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.success.found", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            fire.assertError();
            fail("bolt.runner.asserter.success.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.success.found", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            fire.assertCheck(runnerResult -> {
                assertTrue(runnerResult.isSuccess());
                assertFalse(runnerResult.isFailure());
                assertFalse(runnerResult.isError());
                assertNull(runnerResult.message().orElse(null));
                assertNull(runnerResult.error().orElse(null));
                throw new Exception("bolt.asserter.assert.check.test");
            });
        } catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.assert.check", e.getMessage());
            assertClass(Exception.class, e.getCause());
            assertEquals("bolt.asserter.assert.check.test", e.getCause().getMessage());
        }

        fire.onOffence(runnerResult -> {
            throw new RunnerException("bolt.runner.asserter.fall.through.expected");
        });
    }

    @Test
    void testFailureState() {
        final var blank = new String[] {""};
        final var customMessage = "bolt.asserter.custom.message";
        final var fire = new BoltResult(blank, blank, instant, -1, customMessage);

        fire.assertFailure();

        assertFalse(fire.isSuccess());
        assertTrue(fire.isFailure());
        assertFalse(fire.isError());

        assertNotNull(fire.message().orElse(null));
        assertNull(fire.error().orElse(null));

        try {
            fire.assertSuccess();
            fail("bolt.runner.asserter.failure.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals(customMessage, e.getMessage());
            assertNull(e.getCause());
        }

        try {
            fire.assertError();
            fail("bolt.runner.asserter.success.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals(customMessage, e.getMessage());
            assertNull(e.getCause());
        }

        try {
            fire.assertCheck(runnerResult -> {
                assertFalse(runnerResult.isSuccess());
                assertTrue(runnerResult.isFailure());
                assertFalse(runnerResult.isError());
                assertNotNull(runnerResult.message().orElse(null));
                assertNull(runnerResult.error().orElse(null));
                throw new Exception("bolt.asserter.assert.check.test");
            });
        } catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.assert.check", e.getMessage());
            assertClass(Exception.class, e.getCause());
            assertEquals("bolt.asserter.assert.check.test", e.getCause().getMessage());
        }

        try {
            fire.onOffence(runnerResult -> {
                throw new Exception("bolt.asserter.on.offence.test");
            });
        } catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.on.offence", e.getMessage());
            assertClass(Exception.class, e.getCause());
            assertEquals("bolt.asserter.on.offence.test", e.getCause().getMessage());
        }
    }

    @Test
    void testErrorState() {
        final var blank = new String[] {""};
        final var empty = new String[0];
        final var errorMessage = "Throwable?";
        final var exception = new Exception(errorMessage, null);
        final var fire = new BoltResult(blank, empty, instant, exception);

        fire.assertError();

        assertFalse(fire.isSuccess());
        assertFalse(fire.isFailure());
        assertTrue(fire.isError());

        assertNull(fire.message().orElse(null));

        Exception error = fire.error().orElse(null);
        assertNotNull(error);
        assertClass(exception.getClass(), error);
        assertEquals("Throwable?", error.getMessage());

        try {
            fire.assertSuccess();
            fail("bolt.runner.asserter.error.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            fire.assertFailure();
            fail("bolt.runner.asserter.error.found.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.error.found", e.getMessage());
            assertNull(e.getCause());
        }

        try {
            fire.assertCheck(runnerResult -> {
                assertFalse(runnerResult.isSuccess());
                assertFalse(runnerResult.isFailure());
                assertTrue(runnerResult.isError());
                assertNull(runnerResult.message().orElse(null));
                assertNotNull(runnerResult.error().orElse(null));
                throw new Exception("bolt.asserter.assert.check.test");
            });
        } catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.assert.check", e.getMessage());
            assertClass(Exception.class, e.getCause());
            assertEquals("bolt.asserter.assert.check.test", e.getCause().getMessage());
        }

        try {
            fire.onOffence(runnerResult -> {
                throw new Exception("bolt.asserter.on.offence.test");
            });
        } catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.on.offence", e.getMessage());
            assertClass(Exception.class, e.getCause());
            assertEquals("bolt.asserter.on.offence.test", e.getCause().getMessage());
        }
    }

    @Test
    void testRunTestFailureExpectedLength() {
        var outputLines = array("");
        var expectedLines = array("", "");
        final var programOutput = new BoltProgramOutput(outputLines, instant, null);
        final var fire = programOutput.expected(expectedLines);

        try {
            fire.assertSuccess();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getMessage());
        }

        fire.assertFailure();

        try {
            fire.assertError();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getMessage());
        }

        try {
            fire.onOffence(result -> {
                throw new IllegalStateException(result.message().orElse("null.message"));
            });
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.on.offence", e.getMessage());
            assertNotNull(e.getCause());
            assertClass(IllegalStateException.class, e.getCause());
            assertEquals("bolt.runner.asserter.output.length.mismatch", e.getCause().getMessage());
        }

        var result = fire.result();
        assertEquals(-1, result.offendingIndex());
        assertNull(result.error().orElse(null));
        assertEquals("bolt.runner.asserter.output.length.mismatch", result.message().orElse(null));
    }

    @Test
    void testRunTestFailureComparisonFailure() {
        var outputLines = array("");
        var expectedLines = array("mismatch");
        final var programOutput = new BoltProgramOutput(outputLines, instant, null);
        final var fire = programOutput.expected(expectedLines);

        try {
            fire.assertSuccess();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getMessage());
        }

        fire.assertFailure();

        try {
            fire.assertError();
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getMessage());
        }

        try {
            fire.onOffence(result -> {
                throw new IllegalStateException(result.message().orElse(null), null);
            });
            fail("bolt.runner.test.exception.expected");
        }
        catch (Exception e) {
            assertClass(RunnerException.class, e);
            assertEquals("bolt.runner.on.offence", e.getMessage());
            assertNotNull(e.getCause());
            assertClass(IllegalStateException.class, e.getCause());
            assertEquals("bolt.runner.asserter.output.data.mismatch", e.getCause().getMessage());
        }

        var result = fire.result();
        assertEquals(0, result.offendingIndex());
        assertNull(result.error().orElse(null));
        assertEquals("bolt.runner.asserter.output.data.mismatch", result.message().orElse(null));
    }

    @Test
    void testOnOffenceFallThrough() {
        final var one = new String[] {"one"};
        final var two = new String[] {"two"};
        final var offendingIndex = 0;
        final var message = "mismatch";
        final var fire = new BoltResult(one, two, instant, offendingIndex, message);

        fire.onOffence(runnerResult -> {});
    }


    @Test
    void testForSuccessState() {
        var inputArray = new String[][] {null, nullArray, emptyArray, blankArray};
        for (String[] outputStringArray : inputArray) {
            for (String[] expectedStringArray : inputArray) {
                try {
                    var fire = new BoltResult(outputStringArray, expectedStringArray, instant);

                    if (outputStringArray == null || expectedStringArray == null) {
                        fail("null.pointer.exception.expected");
                    }

                    assertTrue(fire.isSuccess());
                    assertFalse(fire.isFailure());
                    assertFalse(fire.isError());

                    String[] output = fire.output();
                    String[] expected = fire.expected();
                    int offendingIndex = fire.offendingIndex();
                    String message = fire.message().orElse(null);
                    Exception error = fire.error().orElse(null);

                    assertEquals(outputStringArray.length, output.length);
                    assertEquals(expectedStringArray.length, expected.length);
                    assertArrayEquals(outputStringArray, output);
                    assertArrayEquals(expectedStringArray, expected);
                    assertEquals(instant, fire.executionDuration());
                    assertEquals(-1, offendingIndex);
                    assertNull(message);
                    assertNull(error);
                }
                catch (Exception e) {
                    assertClass(NullPointerException.class, e);
                    assertNull(e.getMessage());
                }
            }
        }
    }

    @Test
    void testForFailureState() {
        var inputArray = new String[][] {null, nullArray, emptyArray, blankArray};
        var messageArray = new String[] {null, "", "message"};
        var indexArray = new int[] {-3, -2, -1, 0, 1, 2};

        for (String[] pOutput : inputArray) {
            for (String[] pExpected : inputArray) {
                for (int pIndex : indexArray) {
                    for (String pMessage : messageArray) {
                        try {
                            var result = new BoltResult(pOutput, pExpected, instant, pIndex, pMessage);

                            if (pOutput == null || pExpected == null || pMessage == null) {
                                fail("null.pointer.exception.expected");
                            }

                            assertFalse(result.isSuccess());
                            assertTrue(result.isFailure());
                            assertFalse(result.isError());

                            String[] output = result.output();
                            String[] expected = result.expected();
                            int offendingIndex = result.offendingIndex();
                            String message = result.message().orElse(null);
                            Exception error = result.error().orElse(null);

                            assertEquals(pOutput.length, output.length);
                            assertEquals(pExpected.length, expected.length);
                            assertArrayEquals(pOutput, output);
                            assertArrayEquals(pExpected, expected);
                            assertEquals(instant, result.executionDuration());
                            assertEquals(Math.max(pIndex, -1), offendingIndex);
                            assertEquals(pMessage, message);
                            assertNull(error);
                        }
                        catch (Exception e) {
                            assertClass(NullPointerException.class, e);
                            assertNull(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    @Test
    void testForErrorState() {
        var inputArray = new String[][] {null, nullArray, emptyArray, blankArray};
        var exceptionArray = new Exception[] {null, new Exception("")};
        for (String[] pOutput : inputArray) {
            for (String[] pExpected : inputArray) {
                for (Exception pException : exceptionArray) {
                    try {
                        var result = new BoltResult(pOutput, pExpected, instant, pException);
                        if (pOutput == null || pExpected == null || pException == null) {
                            fail("null.pointer.exception.expected");
                        }

                        assertFalse(result.isSuccess());
                        assertFalse(result.isFailure());
                        assertTrue(result.isError());

                        String[] output = result.output();
                        String[] expected = result.expected();
                        String message = result.message().orElse(null);
                        int offendingIndex = result.offendingIndex();
                        Exception error = result.error().orElse(null);

                        assertEquals(pOutput.length, output.length);
                        assertEquals(pExpected.length, expected.length);
                        assertArrayEquals(pOutput, output);
                        assertArrayEquals(pExpected, expected);
                        assertEquals(instant, result.executionDuration());
                        assertEquals(-1, offendingIndex);
                        assertNull(message);
                        assertClass(pException.getClass(), error);
                    }
                    catch (Exception e) {
                        assertClass(NullPointerException.class, e);
                        assertNull(e.getMessage());
                    }
                }
            }
        }
    }

    @Test
    void testSuccessOutputArrayCopy() {
        // The constructor caller takes responsibility for input arrays.
        final var output = new String[] {"index0", "index1", "index2"};
        final var expected = new String[] {""};
        final var result = new BoltResult(output, expected, Duration.ZERO);
        output[0] = "changed0";
        final var copy1 = result.output();
        copy1[1] = "changed1";
        output[2] = "changed2";
        final var copy2 = result.output();
        assertArrayEquals(output, new String[] {"changed0", "index1", "changed2"});
        assertArrayEquals(copy1, new String[] {"changed0", "changed1", "index2"});
        assertArrayEquals(copy2, new String[] {"changed0", "index1", "changed2"});
    }

    @Test
    void testFailureOutputArrayCopy() {
        // The constructor caller takes responsibility for input arrays.
        final var output = new String[] {"index0", "index1", "index2"};
        final var expected = new String[] {""};
        final var result = new BoltResult(output, expected, Duration.ZERO, -1, "");
        output[0] = "changed0";
        final var copy1 = result.output();
        copy1[1] = "changed1";
        output[2] = "changed2";
        final var copy2 = result.output();
        assertArrayEquals(output, new String[] {"changed0", "index1", "changed2"});
        assertArrayEquals(copy1, new String[] {"changed0", "changed1", "index2"});
        assertArrayEquals(copy2, new String[] {"changed0", "index1", "changed2"});
    }

    @Test
    void testErrorOutputArrayCopy() {
        // The constructor caller takes responsibility for input arrays.
        final var output = new String[] {"index0", "index1", "index2"};
        final var expected = new String[] {""};
        final var result = new BoltResult(output, expected, Duration.ZERO, new Exception());
        output[0] = "changed0";
        final var copy1 = result.output();
        copy1[1] = "changed1";
        output[2] = "changed2";
        final var copy2 = result.output();
        assertArrayEquals(output, new String[] {"changed0", "index1", "changed2"});
        assertArrayEquals(copy1, new String[] {"changed0", "changed1", "index2"});
        assertArrayEquals(copy2, new String[] {"changed0", "index1", "changed2"});
    }

    @Test
    void testSuccessExpectedArrayCopy() {
        // The constructor caller takes responsibility for input arrays.
        final var output = new String[] {""};
        final var expected = new String[] {"index0", "index1", "index2"};
        final var result = new BoltResult(output, expected, Duration.ZERO);
        expected[0] = "changed0";
        final var copy1 = result.expected();
        copy1[1] = "changed1";
        expected[2] = "changed2";
        final var copy2 = result.expected();
        assertArrayEquals(expected, new String[] {"changed0", "index1", "changed2"});
        assertArrayEquals(copy1, new String[] {"changed0", "changed1", "index2"});
        assertArrayEquals(copy2, new String[] {"changed0", "index1", "changed2"});
    }

    @Test
    void testFailureExpectedArrayCopy() {
        // The constructor caller takes responsibility for input arrays.
        final var output = new String[] {""};
        final var expected = new String[] {"index0", "index1", "index2"};
        final var result = new BoltResult(output, expected, Duration.ZERO, -1, "");
        expected[0] = "changed0";
        final var copy1 = result.expected();
        copy1[1] = "changed1";
        expected[2] = "changed2";
        final var copy2 = result.expected();
        assertArrayEquals(expected, new String[] {"changed0", "index1", "changed2"});
        assertArrayEquals(copy1, new String[] {"changed0", "changed1", "index2"});
        assertArrayEquals(copy2, new String[] {"changed0", "index1", "changed2"});
    }

    @Test
    void testErrorExpectedArrayCopy() {
        // The constructor caller takes responsibility for input arrays.
        final var output = new String[] {""};
        final var expected = new String[] {"index0", "index1", "index2"};
        final var result = new BoltResult(output, expected, Duration.ZERO, new Exception());
        expected[0] = "changed0";
        final var copy1 = result.expected();
        copy1[1] = "changed1";
        expected[2] = "changed2";
        final var copy2 = result.expected();
        assertArrayEquals(expected, new String[] {"changed0", "index1", "changed2"});
        assertArrayEquals(copy1, new String[] {"changed0", "changed1", "index2"});
        assertArrayEquals(copy2, new String[] {"changed0", "index1", "changed2"});
    }

}