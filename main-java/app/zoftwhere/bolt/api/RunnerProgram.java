package app.zoftwhere.bolt.api;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import java.nio.charset.Charset;

/**
 * Runner program interface.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public interface RunnerProgram extends AbstractUnit.Input<RunnerProgramOutput> {

  /**
   * Specify the input.
   *
   * @param input program input
   * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
   * @since 1.0.0
   */
  @Override
  RunnerProgramOutput input(String... input);

  /**
   * Specify the input.
   *
   * @param supplier {@link java.io.InputStream} supplier for program input
   * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
   * @since 6.0.0
   */
  @Override
  RunnerProgramOutput input(InputStreamSupplier supplier);

  /**
   * Specify the input.
   *
   * @param supplier {@link java.io.InputStream} supplier for program input
   * @param charset character encoding of {@link java.io.InputStream}
   * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
   * @since 6.0.0
   */
  @Override
  RunnerProgramOutput input(InputStreamSupplier supplier, Charset charset);

  /**
   * Specify the input.
   *
   * @param resourceName resource name for loading program input
   * @param withClass {@link java.lang.Class} with which to retrieve the program input
   * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
   * @since 1.0.0
   */
  @Override
  RunnerProgramOutput loadInput(String resourceName, Class<?> withClass);

  /**
   * Specify the input.
   *
   * @param resourceName resource name for loading program input
   * @param withClass {@link java.lang.Class} with which to retrieve the program input
   * @param charset character encoding of resource
   * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
   * @since 1.0.0
   */
  @Override
  RunnerProgramOutput loadInput(String resourceName, Class<?> withClass, Charset charset);
}
