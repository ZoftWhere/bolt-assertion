package app.zoftwhere.bolt.api;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunnerResultConsumer;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

/**
 * Bolt Assertion Runner Abstract Unit class.
 *
 * <p>This is a package-private abstract class for domain-specific language composition.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
abstract class AbstractUnit {

  /**
   * Constructor for AbstractUnit.
   *
   * @since 6.0.0
   */
  public AbstractUnit() {}

  interface Encoding<T> {

    T encoding(Charset encoding);
  }

  interface RunNoArguments<T> {

    T run(RunStandard program);

    T run(Charset charset, RunStandard program);

    T runConsole(RunConsole program);

    T runConsole(Charset charset, RunConsole program);
  }

  interface RunWithArguments<T> {

    T run(RunStandardArgued program);

    T run(Charset charset, RunStandardArgued program);

    T runConsole(RunConsoleArgued program);

    T runConsole(Charset charset, RunConsoleArgued program);
  }

  interface Arguments<T> {

    T argument(String... arguments);
  }

  interface Input<T> {

    T input(String... input);

    T input(Charset charset, String... input);

    T input(InputStreamSupplier supplier);

    T input(InputStreamSupplier supplier, Charset charset);

    T loadInput(String resourceName, Class<?> withClass);

    T loadInput(String resourceName, Class<?> withClass, Charset charset);
  }

  interface Comparison<T, C> {

    T comparator(Comparator<C> comparator);
  }

  interface Output {

    String[] output();

    Duration executionDuration();

    Optional<Exception> error();
  }

  interface Expected<T> {

    T expected(String... expected);

    T expected(InputStreamSupplier supplier);

    T expected(InputStreamSupplier supplier, Charset charset);

    T loadExpectation(String resourceName, Class<?> withClass);

    T loadExpectation(String resourceName, Class<?> withClass, Charset charset);
  }

  interface Assertions<T extends Result> {

    void assertSuccess();

    void assertFailure();

    void assertError();

    void assertCheck(RunnerResultConsumer consumer);

    void onOffence(RunnerResultConsumer consumer);

    T result();
  }

  interface Result {

    boolean isSuccess();

    boolean isFailure();

    boolean isError();

    String[] output();

    String[] expected();

    Duration executionDuration();

    int offendingIndex();

    Optional<String> message();

    Optional<Exception> error();
  }

  @FunctionalInterface
  interface CallerNoArguments<T1, T2> {

    void call(T1 t1, T2 t2) throws Exception;
  }

  @FunctionalInterface
  interface CallerWithArguments<T1, T2> {

    void call(String[] arguments, T1 t1, T2 t2) throws Exception;
  }

  @FunctionalInterface
  interface ThrowingSupplier<T> {

    T get() throws Exception;
  }

  @FunctionalInterface
  interface ThrowingConsumer<T> {

    void accept(T input) throws Exception;
  }
}
