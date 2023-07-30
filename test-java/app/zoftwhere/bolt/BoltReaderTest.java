package app.zoftwhere.bolt;

import static app.zoftwhere.bolt.BoltTestHelper.assertClass;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class BoltReaderTest {

  @Test
  void readLine() throws IOException {
    final var string = "";
    final var reader = forString(string, UTF_16);
    assertEquals("", reader.readLine());
    assertFalse(reader.hasNext());
  }

  @Test
  void testByteOrderMark1() throws IOException {
    final var string = "UTF-16\ufeff";
    final var data = string.getBytes(UTF_16);
    final var stream = new ByteArrayInputStream(data);
    try (final var reader = new BoltReader(stream, UTF_16)) {
      assertEquals("UTF-16\ufeff", reader.readLine());
    }
  }

  @Test
  void testByteOrderMark2() throws IOException {
    final var string = "\ufeffUTF-16\ufeff";
    try (final var reader = forString(string, UTF_16BE)) {
      assertEquals("\ufeffUTF-16\ufeff", reader.readLine());
    }
  }

  @Test
  void testByteOrderMark3() throws IOException {
    final var string = "\ufeffUTF-16\ufeff";
    try (final var reader = forString(string, UTF_16LE)) {
      assertEquals("\ufeffUTF-16\ufeff", reader.readLine());
    }
  }

  @Test
  void testByteOrderMark4() throws IOException {
    final var string = "\ufeffUTF-16\ufeff";
    final var data = string.getBytes(UTF_16);
    try (final var reader = new BoltReader(data, UTF_16)) {
      assertEquals("\ufeffUTF-16\ufeff", reader.readLine());
    }
  }

  @Test
  void testByteOrderMarkArray1() {
    final var string = "\ufeffUTF-16\ufeff";
    final var array = BoltReader.readArray(() -> forString(string, UTF_16));
    final var expected = new String[] {"\ufeffUTF-16\ufeff"};
    assertArrayEquals(expected, array);
  }

  @Test
  void testByteOrderMarkArray2() {
    final var string = "\ufeffUTF-16\ufeff";
    final var array = BoltReader.readArray(() -> forString(string, UTF_16BE));
    final var expected = new String[] {"\ufeffUTF-16\ufeff"};
    assertArrayEquals(expected, array);
  }

  @Test
  void testByteOrderMarkArray3() {
    final var string = "\ufeffUTF-16\ufeff";
    final var array = BoltReader.readArray(() -> forString(string, UTF_16LE));
    final var expected = new String[] {"\ufeffUTF-16\ufeff"};
    assertArrayEquals(expected, array);
  }

  @Test
  void testByteOrderMarkArray4() {
    final var string = "\ufeff\ufeffUTF-16\ufeff";
    final var byteArray = string.getBytes(UTF_16BE);
    final var array = BoltReader.readArray(() -> new BoltReader(byteArray, UTF_16));
    final var expected = new String[] {"\ufeffUTF-16\ufeff"};
    assertArrayEquals(expected, array);
  }

  @Test
  void testByteOrderMarkArray5() {
    final var string = "\ufeff\ufeffUTF-16\ufeff";
    final var byteArray = string.getBytes(UTF_16LE);
    final var array = BoltReader.readArray(() -> new BoltReader(byteArray, UTF_16));
    final var expected = new String[] {"\ufeffUTF-16\ufeff"};
    assertArrayEquals(expected, array);
  }

  @Test
  void testNullPointer() {
    try {
      new BoltReader((byte[]) null, UTF_8);
    } catch (Exception e) {
      assertClass(RunnerException.class, e);
      assertEquals(e.getMessage(), "bolt.runner.reader.data.null");
    }
  }

  @Test
  void testNullCharset() {
    try {
      new BoltReader(new byte[0], null);
    } catch (Exception e) {
      assertClass(RunnerException.class, e);
      assertEquals(e.getMessage(), "bolt.runner.reader.charset.null");
    }

    try {
      new BoltReader(new ByteArrayInputStream(new byte[0]), null);
    } catch (Exception e) {
      assertClass(RunnerException.class, e);
      assertEquals(e.getMessage(), "bolt.runner.reader.charset.null");
    }
  }

  @Test
  void testNullInputStream() {
    try {
      new BoltReader((InputStream) null, UTF_8);
    } catch (Exception e) {
      assertClass(RunnerException.class, e);
      assertEquals(e.getMessage(), "bolt.runner.reader.input.stream.null");
    }
  }

  @Test
  void testInputStreamAutoClose() {
    final var inputStream =
        new ByteArrayInputStream("\r\r\r".getBytes()) {
          @Override
          public void close() throws IOException {
            super.close();
            throw new IOException();
          }
        };
    try {
      try (final var reader = new BoltReader(inputStream, UTF_8)) {
        reader.readLine();
      }

      fail("bolt.runner.reader.close.exception.expected");
    } catch (IOException ignore) {
    }
  }

  @Test
  void testReaderAutoCloseSkip() {
    final var stream = new ByteArrayInputStream(new byte[0]);
    final var closeFlag = new BoltPlaceHolder<>(false);
    final var reader =
        new InputStreamReader(stream) {
          @Override
          public void close() throws IOException {
            super.close();
            closeFlag.set(true);
          }
        };

    try (final var boltReader = new BoltReader(reader)) {
      boltReader.next();
    } catch (IOException e) {
      fail(e);
    }
    assertFalse(closeFlag.get());

    try {
      reader.close();
    } catch (Exception e) {
      fail(e);
    }
    assertTrue(closeFlag.get());
  }

  @Test
  void testHasNextFail() throws IOException {
    final var stream = new ByteArrayInputStream("".getBytes());
    final var runner = new BoltReader(stream, UTF_8);
    runner.readLine();
    assertFalse(runner.hasNext());
  }

  @Test
  void testNextFail() {
    final var stream = new ByteArrayInputStream("".getBytes());

    final var reader =
        new BoltReader(stream, UTF_8) {
          @Override
          String readLine() throws IOException {
            throw new IOException();
          }
        };

    try {
      reader.next();
      fail("UncheckedIOException expected.");
    } catch (RuntimeException e) {
      assertClass(UncheckedIOException.class, e);
    }
  }

  @Test
  void testReadArrayFail() {
    final var stream = new ByteArrayInputStream(new byte[0]);
    final var reader =
        new InputStreamReader(stream) {
          @Override
          public boolean ready() throws IOException {
            throw new IOException("Fake IO Exception.");
          }
        };

    try {
      BoltReader.readArray(() -> new BoltReader(reader));
      fail();
    } catch (Exception e) {
      assertClass(RunnerException.class, e);
      assertEquals("bolt.runner.reader.read.array", e.getMessage());
      assertClass(UncheckedIOException.class, e.getCause());
      assertEquals("java.io.IOException: Fake IO Exception.", e.getCause().getMessage());
    }

    try {
      reader.close();
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  void testReadListFail() {
    final var stream = new ByteArrayInputStream(new byte[0]);
    final var reader =
        new InputStreamReader(stream) {
          @Override
          public boolean ready() throws IOException {
            throw new IOException("Fake IO Exception.");
          }
        };

    try {
      BoltReader.readList(() -> new BoltReader(reader));
      fail();
    } catch (Exception e) {
      assertClass(RunnerException.class, e);
      assertEquals("bolt.runner.reader.read.list", e.getMessage());
      assertClass(UncheckedIOException.class, e.getCause());
      assertEquals("java.io.IOException: Fake IO Exception.", e.getCause().getMessage());
    }

    try {
      reader.close();
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  void testByteArraySplitter() {
    final var string = "\r\n\r\n";
    final var supplier = (Supplier<BoltReader>) () -> forString(string, UTF_8);
    final var list = BoltReader.readList(supplier);
    final var array = BoltReader.readArray(supplier);
    assertEquals(3, list.size());
    assertEquals(3, array.length);
  }

  @Test
  void testPartitionSplit() {
    final var string = "\r" + "\r\n" + "\n" + "\u0085" + "\u2028" + "\u2029" + "\f" + "";
    final var supplier = (Supplier<BoltReader>) () -> forString(string, UTF_16);
    final var array = BoltReader.readArray(supplier);
    final var expected = new String[] {"", "", "", "", "", "", "", ""};
    assertArrayEquals(expected, array);
  }

  @Test
  void testStringSplitter5() {
    final var string = "1\n1\n345";
    final var supplier = (Supplier<BoltReader>) () -> forString(string, UTF_8);
    final var list = BoltReader.readList(supplier);
    final var array = BoltReader.readArray(supplier);
    assertEquals(3, list.size());
    assertEquals(3, array.length);
  }

  @Test
  void testInputSplitting() {
    testThis("empty", "");
    testThis("blank", " ");
    testThis("new1", "", "");
    testThis("new2", "", "", "");
    testThis("new3", "", "", "", "");
  }

  private void testThis(String test, String... array) {
    final var size = array.length;
    final var builder = new StringBuilder();
    if (size > 0) {
      builder.append(array[0]);
    }
    for (var i = 1; i < size; i++) {
      builder.append("\n").append(array[i]);
    }
    final var input = builder.toString();
    final var list = BoltReader.readList(() -> forString(input, UTF_8));

    if (array.length != list.size()) {
      assertEquals(array.length, list.size(), test + " [" + Arrays.toString(list.toArray()) + "]");
    }
    for (var i = 0; i < size; i++) {
      assertEquals(array[i], list.get(i), test);
    }
  }

  @Test
  void testReadLine() throws IOException {
    final var string = "Hello World\ud835\udccc";
    final var reader = forString(string, UTF_16);
    final var line = reader.readLine();
    assertEquals(string, line);
    assertFalse(reader.hasNext());
  }

  private BoltReader forString(String string, Charset charset) {
    final var input = new ByteArrayInputStream(string.getBytes(charset));
    return new BoltReader(input, charset);
  }
}
