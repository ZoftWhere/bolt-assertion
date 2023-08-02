package app.zoftwhere.bolt;

import static app.zoftwhere.bolt.BoltTestHelper.newStringArrayInputStream;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

class BoltLineIteratorTest {

  @Test
  void testScannerBlank() {
    final var input = "";
    try (Scanner scanner = new Scanner(input)) {
      final var iterator = new BoltLineIterator(scanner);
      assertEquals("", iterator.next());
      assertNull(iterator.next());
    }
  }

  @Test
  void testScannerBlank2() {
    final var input = "\r\n";
    try (Scanner scanner = new Scanner(input)) {
      final var iterator = new BoltLineIterator(scanner);
      assertEquals("", iterator.next());
      assertEquals("", iterator.next());
      assertNull(iterator.next());
    }
  }

  @Test
  void testInputStreamBlank() throws Exception {
    final var input = new String[] {""};
    try (InputStream inputStream = newStringArrayInputStream(input, UTF_8)) {
      BoltLineIterator iterator = new BoltLineIterator(inputStream, UTF_8);
      assertEquals("", iterator.next());
      assertNull(iterator.next());
    }
  }

  @Test
  void testInputStreamBlank2() throws Exception {
    final var input = new String[] {"", ""};
    try (InputStream inputStream = newStringArrayInputStream(input, UTF_8)) {
      BoltLineIterator iterator = new BoltLineIterator(inputStream, UTF_8);
      assertEquals("", iterator.next());
      assertEquals("", iterator.next());
      assertNull(iterator.next());
    }
  }

  @Test
  void testInputStreamASCII() throws Exception {
    final var input = "Hello\nWorld".getBytes(US_ASCII);
    try (InputStream inputStream = new ByteArrayInputStream(input)) {
      BoltLineIterator iterator = new BoltLineIterator(inputStream, US_ASCII);
      assertEquals("Hello", iterator.next());
      assertEquals("World", iterator.next());
      assertNull(iterator.next());
    }
  }

  @Test
  void testInputStreamUTF16() throws Exception {
    final var input = "Hello\nWorld".getBytes(UTF_16);
    try (InputStream inputStream = new ByteArrayInputStream(input)) {
      BoltLineIterator iterator = new BoltLineIterator(inputStream, UTF_16BE);
      assertEquals("\ufeffHello", iterator.next());
      assertEquals("World", iterator.next());
      assertNull(iterator.next());
    }
  }
}
