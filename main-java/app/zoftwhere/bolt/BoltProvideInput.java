package app.zoftwhere.bolt;

import static app.zoftwhere.bolt.BoltUtility.arrayHasNull;
import static java.util.Objects.requireNonNull;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import app.zoftwhere.bolt.api.RunnerLoader;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerProvideInput;
import java.nio.charset.Charset;

/**
 * Bolt Provide Input class.
 *
 * <p>This is a package-private class for providing its functionality.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
class BoltProvideInput
    implements RunnerProvideInput, RunnerProgramInput, RunnerLoader, BoltProvide {

  private final Charset encoding;

  private final String[] arguments;

  private final Charset inputCharset;

  private final InputStreamSupplier supplier;

  private final RunnerException error;

  /**
   * Constructor for BoltProvideInput (package-private).
   *
   * <p>Create instance of this multi-interfaced class for handling of runners that accept input
   * first.
   *
   * @param encoding character encoding to use by default when not specified
   * @since 11.0.0
   */
  BoltProvideInput(Charset encoding) {
    this.encoding = requireNonNull(encoding);
    this.arguments = null;
    this.inputCharset = encoding;
    this.supplier = () -> null;
    this.error = null;
  }

  /**
   * Constructor for BoltProvideInput (private).
   *
   * @param encoding character encoding to use by default when not specified
   * @param arguments program arguments
   * @param inputCharset character encoding of {@link java.io.InputStream}
   * @param supplier {@link java.io.InputStream} supplier for program input
   * @param error execution error
   * @since 11.0.0
   */
  private BoltProvideInput(
      Charset encoding,
      String[] arguments,
      Charset inputCharset,
      InputStreamSupplier supplier,
      RunnerException error) {
    this.encoding = encoding;
    this.arguments = arguments;
    this.inputCharset = inputCharset;
    this.supplier = supplier;
    this.error = error;
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput input(String... input) {
    return input(encoding, input);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput input(Charset charset, String... input) {
    if (charset == null) {
      //noinspection ConstantConditions
      return new BoltProvideInput(encoding, arguments, charset, supplier, error);
    }

    if (input == null) {
      RunnerException error = new RunnerException("bolt.runner.variable.argument.input.null");
      return new BoltProvideInput(encoding, arguments, charset, supplier, error);
    }

    if (arrayHasNull(input)) {
      RunnerException error = new RunnerException("bolt.runner.variable.argument.input.has.null");
      return new BoltProvideInput(encoding, arguments, charset, supplier, error);
    }

    InputStreamSupplier supplier = () -> new BoltArrayInputStream(input, charset);
    return new BoltProvideInput(encoding, arguments, charset, supplier, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput input(InputStreamSupplier supplier) {
    return new BoltProvideInput(encoding, arguments, inputCharset, supplier, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput input(InputStreamSupplier supplier, Charset charset) {
    return new BoltProvideInput(encoding, arguments, charset, supplier, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
    return loadInput(resourceName, withClass, encoding);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset) {
    if (charset == null) {
      //noinspection ConstantConditions
      return new BoltProvideInput(encoding, arguments, charset, supplier, error);
    }

    if (resourceName == null) {
      RunnerException error = new RunnerException("bolt.runner.load.input.resource.name.null");
      return new BoltProvideInput(encoding, arguments, charset, supplier, error);
    }

    if (withClass == null) {
      RunnerException error = new RunnerException("bolt.runner.load.input.resource.class.null");
      return new BoltProvideInput(encoding, arguments, charset, supplier, error);
    }

    if (withClass.getResource(resourceName) == null) {
      RunnerException error = new RunnerException("bolt.runner.load.input.resource.not.found");
      return new BoltProvideInput(encoding, arguments, charset, supplier, error);
    }

    InputStreamSupplier supplier = () -> withClass.getResourceAsStream(resourceName);
    return new BoltProvideInput(encoding, arguments, charset, supplier, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerLoader argument(String... arguments) {
    return new BoltProvideInput(encoding, emptyOnNull(arguments), inputCharset, supplier, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput run(RunStandard program) {
    return run(encoding, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput run(Charset charset, RunStandard program) {
    BoltExecutor executor = buildStandardExecutor(proxyRunStandard(program));
    return buildOutput(encoding, arguments, inputCharset, supplier, charset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput runConsole(RunConsole program) {
    return runConsole(encoding, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput runConsole(Charset charset, RunConsole program) {
    BoltExecutor executor = buildConsoleExecutor(proxyRunConsole(program));
    return buildOutput(encoding, arguments, inputCharset, supplier, charset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput run(RunStandardArgued program) {
    return run(encoding, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput run(Charset charset, RunStandardArgued program) {
    BoltExecutor executor = buildStandardExecutor(program);
    return buildOutput(encoding, arguments, inputCharset, supplier, charset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput runConsole(RunConsoleArgued program) {
    return runConsole(encoding, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput runConsole(Charset charset, RunConsoleArgued program) {
    BoltExecutor executor = buildConsoleExecutor(program);
    return buildOutput(encoding, arguments, inputCharset, supplier, charset, executor, error);
  }
}
