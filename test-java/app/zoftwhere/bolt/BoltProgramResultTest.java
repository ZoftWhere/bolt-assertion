package app.zoftwhere.bolt;

import org.junit.jupiter.api.Test;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class BoltProgramResultTest {

    private final String[] emptyArray = new String[] { };
    private final String[] blankArray = new String[] {""};
    private final String[] nullArray = new String[] {null};

    @Test
    void testForSuccessState() {
        String[][] inputArray = new String[][] {null, nullArray, emptyArray, blankArray};
        for (String[] outputStringArray : inputArray) {
            for (String[] expectedStringArray : inputArray) {
                try {
                    var result = new BoltResult(outputStringArray, expectedStringArray);

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
        String[][] inputArray = new String[][] {null, nullArray, emptyArray, blankArray};
        String[] messageArray = new String[] {null, "", "message"};
        int[] indexArray = new int[] {-3, -2, -1, 0, 1, 2};

        for (String[] pOutput : inputArray) {
            for (String[] pExpected : inputArray) {
                for (int pIndex : indexArray) {
                    for (String pMessage : messageArray) {
                        try {
                            var result = new BoltResult(pOutput, pExpected, pIndex, pMessage);

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
        String[][] inputArray = new String[][] {null, nullArray, emptyArray, blankArray};
        Exception[] exceptionArray = new Exception[] {null, new Exception("")};
        for (String[] pOutput : inputArray) {
            for (String[] pExpected : inputArray) {
                for (Exception pException : exceptionArray) {
                    try {
                        var result = new BoltResult(pOutput, pExpected, pException);
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

}