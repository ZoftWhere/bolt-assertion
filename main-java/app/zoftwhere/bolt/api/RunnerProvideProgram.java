package app.zoftwhere.bolt.api;

import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import java.nio.charset.Charset;

/**
 * Runner accept program interface.
 *
 * <p>This interface that forms the basis for Runner#run() and Runner#runConsole().
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public interface RunnerProvideProgram
    extends AbstractUnit.RunNoArguments<RunnerProgram>,
        AbstractUnit.RunWithArguments<RunnerPreProgram> {

  /**
   * Specify the scanner-printer program without arguments.
   *
   * @param program scanner-printer program without arguments
   * @return {@link app.zoftwhere.bolt.api.RunnerProgram}
   * @since 7.0.0
   */
  @Override
  RunnerProgram run(RunStandard program);

  /**
   * Specify the scanner-printer program without arguments.
   *
   * @param charset character encoding of program {@link java.io.PrintStream}
   * @param program scanner-printer program without arguments
   * @return {@link app.zoftwhere.bolt.api.RunnerProgram}
   * @since 7.0.0
   */
  @Override
  RunnerProgram run(Charset charset, RunStandard program);

  /**
   * Specify the input-output-stream program without arguments.
   *
   * @param program input-output-stream program without arguments
   * @return {@link app.zoftwhere.bolt.api.RunnerProgram}
   * @since 6.0.0
   */
  @Override
  RunnerProgram runConsole(RunConsole program);

  /**
   * Specify the input-output-stream program without arguments.
   *
   * @param charset character encoding of program {@link java.io.OutputStream}
   * @param program input-output-stream program without arguments
   * @return {@link app.zoftwhere.bolt.api.RunnerProgram}
   * @since 6.0.0
   */
  @Override
  RunnerProgram runConsole(Charset charset, RunConsole program);

  /**
   * Specify the scanner-printer program with arguments.
   *
   * @param program scanner-printer program with arguments
   * @return {@link app.zoftwhere.bolt.api.RunnerProgram}
   * @since 7.0.0
   */
  @Override
  RunnerPreProgram run(RunStandardArgued program);

  /**
   * Specify the scanner-printer program with arguments.
   *
   * @param charset character encoding of program {@link java.io.PrintStream}
   * @param program scanner-printer program with arguments
   * @return {@link app.zoftwhere.bolt.api.RunnerProgram}
   * @since 7.0.0
   */
  @Override
  RunnerPreProgram run(Charset charset, RunStandardArgued program);

  /**
   * Specify the input-output-stream program with arguments.
   *
   * @param program input-output-stream program with arguments
   * @return {@link app.zoftwhere.bolt.api.RunnerProgram}
   * @since 6.0.0
   */
  @Override
  RunnerPreProgram runConsole(RunConsoleArgued program);

  /**
   * Specify the input-output-stream program with arguments.
   *
   * @param charset character encoding of program {@link java.io.OutputStream}
   * @param program input-output-stream program with arguments
   * @return {@link app.zoftwhere.bolt.api.RunnerProgram}
   * @since 6.0.0
   */
  @Override
  RunnerPreProgram runConsole(Charset charset, RunConsoleArgued program);
}
