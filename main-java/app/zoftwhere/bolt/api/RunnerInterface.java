package app.zoftwhere.bolt.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Runner instance interface.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public interface RunnerInterface extends RunnerProvideProgram, RunnerProvideInput {

  /**
   * Retrieve Runner character encoding.
   *
   * <p>If the runner was not specified a character encoding, or loaded with a ${@code null} ${@link
   * java.nio.charset.Charset}, then the Runner return the value of {@link
   * app.zoftwhere.bolt.Runner#DEFAULT_ENCODING}.
   *
   * @return character encoding
   * @since 11.0.0
   */
  Charset encoding();

  /**
   * {@link app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier} provides a functional
   * interface for creating an {@link java.io.InputStream} supplier.
   *
   * @since 6.0.0
   */
  @FunctionalInterface
  interface InputStreamSupplier extends AbstractUnit.ThrowingSupplier<InputStream> {

    /**
     * @return {@link java.io.InputStream}
     * @throws Exception for {@link java.io.InputStream} error
     */
    @Override
    InputStream get() throws Exception;
  }

  /**
   * {@link app.zoftwhere.bolt.api.RunnerInterface.RunConsole} provides a functional interface for
   * creating program calls.
   *
   * @since 6.0.0
   */
  @FunctionalInterface
  interface RunConsole extends AbstractUnit.CallerNoArguments<InputStream, OutputStream> {

    /**
     * @param inputStream program {@link java.io.InputStream}
     * @param outputStream program {@link java.io.OutputStream}
     * @throws Exception program error
     */
    @Override
    void call(InputStream inputStream, OutputStream outputStream) throws Exception;
  }

  /**
   * {@link app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued} provides a functional interface
   * for creating program calls.
   *
   * @since 6.0.0
   */
  @FunctionalInterface
  interface RunConsoleArgued extends AbstractUnit.CallerWithArguments<InputStream, OutputStream> {

    /**
     * @param arguments program argument array
     * @param inputStream program {@link java.io.InputStream}
     * @param outputStream program {@link java.io.OutputStream}
     * @throws Exception program error
     */
    @Override
    void call(String[] arguments, InputStream inputStream, OutputStream outputStream)
        throws Exception;
  }

  /**
   * {@link app.zoftwhere.bolt.api.RunnerInterface.RunnerResultConsumer} provides a functional
   * interface for creating execution result asserters.
   *
   * @since 6.0.0
   */
  @FunctionalInterface
  interface RunnerResultConsumer extends AbstractUnit.ThrowingConsumer<RunnerResult> {

    /**
     * A runner result consumer for asserting a execution result.
     *
     * @param result execution result
     * @throws Exception for failure state or error state (consumer driven)
     */
    @Override
    void accept(RunnerResult result) throws Exception;
  }

  /**
   * {@link app.zoftwhere.bolt.api.RunnerInterface.RunStandard} provides a functional interface for
   * creating program calls.
   *
   * @since 7.0.0
   */
  @FunctionalInterface
  interface RunStandard extends AbstractUnit.CallerNoArguments<Scanner, PrintStream> {

    /**
     * @param scanner program {@link java.util.Scanner}
     * @param out program {@link java.io.PrintStream}
     * @throws Exception on program error
     */
    @Override
    void call(Scanner scanner, PrintStream out) throws Exception;
  }

  /**
   * {@link app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued} provides a functional
   * interface for creating program calls.
   *
   * @since 7.0.0
   */
  @FunctionalInterface
  interface RunStandardArgued extends AbstractUnit.CallerWithArguments<Scanner, PrintStream> {

    /**
     * @param arguments program argument array
     * @param scanner program {@link java.util.Scanner}
     * @param out program {@link java.io.PrintStream}
     * @throws Exception on program error
     */
    @Override
    void call(String[] arguments, Scanner scanner, PrintStream out) throws Exception;
  }
}
