package app.zoftwhere.bolt;

import static app.zoftwhere.bolt.BoltTestHelper.arrayOfClass;
import static app.zoftwhere.bolt.BoltTestHelper.assertPublicConstructor;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RunnerExceptionTest {

  @Test
  void testNonFinal() {
    RuntimeException exception =
        new RunnerException("open", null) {
          @Override
          public String toString() {
            return "ExtensionException.class";
          }
        };
    try {
      throw exception;
    } catch (Exception e) {
      final var className = RunnerExceptionTest.class.getName() + "$1";
      assertEquals(className, e.getClass().getName());
    }
  }

  @Test
  void testPublicConstructors() {
    assertPublicConstructor(RunnerException.class, arrayOfClass(String.class));
    assertPublicConstructor(RunnerException.class, arrayOfClass(String.class, Throwable.class));
  }
}
