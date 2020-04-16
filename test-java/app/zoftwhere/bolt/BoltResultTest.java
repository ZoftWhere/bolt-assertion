package app.zoftwhere.bolt;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class BoltResultTest {

    private final String[] emptyArray = new String[] { };
    private final String[] blankArray = new String[] {""};
    private final String[] nullArray = new String[] {null};
    private final Duration instant = Duration.ZERO;

    @Test
    void testForSuccessState() {
        var inputArray = new String[][] {null, nullArray, emptyArray, blankArray};
        for (String[] outputStringArray : inputArray) {
            for (String[] expectedStringArray : inputArray) {
                try {
                    var result = new BoltResult(outputStringArray, expectedStringArray, instant);

                    if (outputStringArray == null || expectedStringArray == null) {
                        fail("null.pointer.exception.expected");
                    }

                    assertTrue(result.isSuccess());
                    assertFalse(result.isFailure());
                    assertFalse(result.isError());

                    String[] output = result.output();
                    String[] expected = result.expected();
                    int offendingIndex = result.offendingIndex();
                    String message = result.message().orElse(null);
                    Exception error = result.error().orElse(null);

                    assertEquals(outputStringArray.length, output.length);
                    assertEquals(expectedStringArray.length, expected.length);
                    assertArrayEquals(outputStringArray, output);
                    assertArrayEquals(expectedStringArray, expected);
                    assertEquals(instant, result.executionDuration());
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