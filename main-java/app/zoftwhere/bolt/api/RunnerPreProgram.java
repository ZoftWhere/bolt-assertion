package app.zoftwhere.bolt.api;

/**
 * Runner pre-program interface.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public interface RunnerPreProgram extends AbstractUnit.Arguments<RunnerProgram> {

  /**
   * Specify the program arguments.
   *
   * @param arguments program argument array
   * @return {@link app.zoftwhere.bolt.api.RunnerProgram}
   * @since 1.0.0
   */
  @Override
  RunnerProgram argument(String... arguments);
}
