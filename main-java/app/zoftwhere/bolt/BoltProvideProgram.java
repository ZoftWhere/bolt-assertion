package app.zoftwhere.bolt;

import static app.zoftwhere.bolt.BoltUtility.arrayHasNull;
import static java.util.Objects.requireNonNull;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramOutput;
import app.zoftwhere.bolt.api.RunnerProvideProgram;
import java.nio.charset.Charset;

/**
 * Bolt Provide Program class.
 *
 * <p>This is a package-private class for {@link app.zoftwhere.bolt.Runner} program first interface
 * implementation.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
class BoltProvideProgram
    implements RunnerProvideProgram, RunnerPreProgram, RunnerProgram, BoltProvide {

  private final Charset encoding;

  private final String[] arguments;

  private final Charset outputCharset;

  private final BoltExecutor executor;

  private final RunnerException error;

  /**
   * Constructor for BoltProvideProgram (package-private).
   *
   * <p>Create instance of this multi-interfaced class for handling of runners that accept program
   * first.
   *
   * @param encoding default character encoding
   * @since 11.0.0
   */
  BoltProvideProgram(Charset encoding) {
    this.encoding = requireNonNull(encoding);
    this.arguments = null;
    this.executor = (arguments, inputCharset, inputStream, outputCharset, outputStream) -> null;
    this.outputCharset = encoding;
    this.error = null;
  }

  /**
   * Constructor for BoltProvideProgram (private).
   *
   * @param encoding character encoding to use by default when not specified
   * @param arguments program argument array
   * @param charset character encoding of program output
   * @param executor program executor interface
   * @param error execution error
   * @since 11.0.0
   */
  private BoltProvideProgram(
      Charset encoding,
      String[] arguments,
      Charset charset,
      BoltExecutor executor,
      RunnerException error) {
    this.encoding = encoding;
    this.arguments = arguments;
    this.outputCharset = charset;
    this.executor = executor;
    this.error = error;
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgram run(RunStandard program) {
    return run(encoding, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgram run(Charset charset, RunStandard program) {
    BoltExecutor executor = buildStandardExecutor(proxyRunStandard(program));
    return new BoltProvideProgram(encoding, arguments, charset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgram runConsole(RunConsole program) {
    return runConsole(encoding, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgram runConsole(Charset charset, RunConsole program) {
    BoltExecutor executor = buildConsoleExecutor(proxyRunConsole(program));
    return new BoltProvideProgram(encoding, arguments, charset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerPreProgram run(RunStandardArgued program) {
    return run(encoding, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerPreProgram run(Charset charset, RunStandardArgued program) {
    BoltExecutor executor = buildStandardExecutor(program);
    return new BoltProvideProgram(encoding, arguments, charset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerPreProgram runConsole(RunConsoleArgued program) {
    return runConsole(encoding, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program) {
    BoltExecutor executor = buildConsoleExecutor(program);
    return new BoltProvideProgram(encoding, arguments, charset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgram argument(String... arguments) {
    return new BoltProvideProgram(encoding, emptyOnNull(arguments), outputCharset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput input(String... input) {
    return input(encoding, input);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput input(Charset charset, String... input) {
    if (charset == null) {
      //noinspection ConstantConditions
      return buildOutput(encoding, arguments, charset, () -> null, outputCharset, executor, error);
    }

    if (input == null) {
      RunnerException error = new RunnerException("bolt.runner.variable.argument.input.null");
      return buildOutput(encoding, arguments, charset, () -> null, outputCharset, executor, error);
    }

    if (arrayHasNull(input)) {
      RunnerException error = new RunnerException("bolt.runner.variable.argument.input.has.null");
      return buildOutput(encoding, arguments, charset, () -> null, outputCharset, executor, error);
    }

    InputStreamSupplier supplier = () -> new BoltArrayInputStream(input, charset);
    return buildOutput(encoding, arguments, charset, supplier, outputCharset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput input(InputStreamSupplier supplier) {
    return buildOutput(encoding, arguments, encoding, supplier, outputCharset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput input(InputStreamSupplier supplier, Charset charset) {
    return buildOutput(encoding, arguments, charset, supplier, outputCharset, executor, error);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput loadInput(String resourceName, Class<?> withClass) {
    return loadInput(resourceName, withClass, encoding);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramOutput loadInput(String resourceName, Class<?> withClass, Charset charset) {
    if (charset == null) {
      //noinspection ConstantConditions
      return buildOutput(encoding, arguments, charset, () -> null, outputCharset, executor, error);
    }

    if (resourceName == null) {
      RunnerException error = new RunnerException("bolt.runner.load.input.resource.name.null");
      return buildOutput(encoding, arguments, charset, () -> null, outputCharset, executor, error);
    }

    if (withClass == null) {
      RunnerException error = new RunnerException("bolt.runner.load.input.resource.class.null");
      return buildOutput(encoding, arguments, charset, () -> null, outputCharset, executor, error);
    }

    if (withClass.getResource(resourceName) == null) {
      RunnerException error = new RunnerException("bolt.runner.load.input.resource.not.found");
      return buildOutput(encoding, arguments, charset, () -> null, outputCharset, executor, error);
    }

    InputStreamSupplier supplier = () -> withClass.getResourceAsStream(resourceName);
    return buildOutput(encoding, arguments, charset, supplier, outputCharset, executor, error);
  }
}
