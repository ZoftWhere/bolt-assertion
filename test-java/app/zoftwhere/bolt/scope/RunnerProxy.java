package app.zoftwhere.bolt.scope;

import app.zoftwhere.bolt.AbstractRunner;
import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import java.nio.charset.Charset;

/**
 * Runner Proxy class.
 *
 * <p>This is a package-private class for scope testing.
 *
 * @author Osmund
 * @since 1.0.0
 */
class RunnerProxy extends AbstractRunner {

  private final Runner runner = new Runner();

  /**
   * Constructor for RunnerProxy (package-private).
   *
   * @since 7.1.0
   */
  RunnerProxy() {}

  /** {@inheritDoc} */
  @Override
  public RunnerInterface encoding(Charset encoding) {
    return ((AbstractRunner) runner).encoding(encoding);
  }

  /** {@inheritDoc} */
  @Override
  public Charset encoding() {
    return runner.encoding();
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput input(String... input) {
    return runner.input(input);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput input(Charset charset, String... input) {
    return runner.input(charset, input);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput input(InputStreamSupplier supplier) {
    return runner.input(supplier);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput input(InputStreamSupplier supplier, Charset decode) {
    return runner.input(supplier, decode);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
    return runner.loadInput(resourceName, withClass);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset decode) {
    return runner.loadInput(resourceName, withClass, decode);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgram run(RunStandard program) {
    return runner.run(program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgram run(Charset charset, RunStandard program) {
    return runner.run(charset, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerPreProgram run(RunStandardArgued program) {
    return runner.run(program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerPreProgram run(Charset charset, RunStandardArgued program) {
    return runner.run(charset, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgram runConsole(RunConsole program) {
    return runner.runConsole(program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerProgram runConsole(Charset charset, RunConsole program) {
    return runner.runConsole(charset, program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerPreProgram runConsole(RunConsoleArgued program) {
    return runner.runConsole(program);
  }

  /** {@inheritDoc} */
  @Override
  public RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program) {
    return runner.runConsole(charset, program);
  }
}
