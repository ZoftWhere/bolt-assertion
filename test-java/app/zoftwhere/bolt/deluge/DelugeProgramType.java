package app.zoftwhere.bolt.deluge;

/**
 * Bolt Assertion Deluge Program Type enum.
 *
 * @author Osmund
 * @since 6.0.0
 */
public enum DelugeProgramType {

  /**
   * Runner with input-first, no-argument scanner-printer program.
   *
   * @since 6.0.0
   */
  INPUT_STANDARD,

  /**
   * Runner with input-first, argument scanner-printer program.
   *
   * @since 6.0.0
   */
  INPUT_STANDARD_ARGUED,

  /**
   * Runner with input-first, no-argument input-output-stream program.
   *
   * @since 6.0.0
   */
  INPUT_CONSOLE,

  /**
   * Runner with input-first, argument input-output-stream program.
   *
   * @since 6.0.0
   */
  INPUT_CONSOLE_ARGUED,

  /**
   * Runner with program-first, no-argument scanner-printer program.
   *
   * @since 6.0.0
   */
  PROGRAM_STANDARD,

  /**
   * Runner with program-first, argument scanner-printer program.
   *
   * @since 6.0.0
   */
  PROGRAM_STANDARD_ARGUED,

  /**
   * Runner with input-first, no-argument input-output-stream program.
   *
   * @since 6.0.0
   */
  PROGRAM_CONSOLE,

  /**
   * Runner with input-first, argument input-output-stream program.
   *
   * @since 6.0.0
   */
  PROGRAM_CONSOLE_ARGUED;

  /**
   * Constructor for DelugeProgramType (private).
   *
   * @since 6.0.0
   */
  DelugeProgramType() {}

  /**
   * Returns if program type is for a runner with program-arguments specified.
   *
   * @return {@code true} for a runner with program-arguments specified, {@code false} otherwise
   * @since 6.0.0
   */
  public boolean isArgued() {
    return this == INPUT_STANDARD_ARGUED
        || this == INPUT_CONSOLE_ARGUED
        || this == PROGRAM_STANDARD_ARGUED
        || this == PROGRAM_CONSOLE_ARGUED;
  }

  /**
   * Returns if program type is for a runner with program first.
   *
   * @return {@code true} for a runner with scanner-printer program, {@code false} otherwise
   * @since 6.0.0
   */
  public boolean isProgramFirst() {
    return this == PROGRAM_STANDARD
        || this == PROGRAM_CONSOLE
        || this == PROGRAM_STANDARD_ARGUED
        || this == PROGRAM_CONSOLE_ARGUED;
  }

  /**
   * Returns if program type is for a runner with input first.
   *
   * @return {@code true} for a runner with scanner-printer program, {@code false} otherwise
   * @since 6.0.0
   */
  public boolean isInputFirst() {
    return this == INPUT_STANDARD
        || this == INPUT_STANDARD_ARGUED
        || this == INPUT_CONSOLE
        || this == INPUT_CONSOLE_ARGUED;
  }

  /**
   * Returns if program type is for a runner with scanner-printer program.
   *
   * @return {@code true} for a runner with scanner-printer program, {@code false} otherwise
   * @since 11.0.0
   */
  public boolean isStandard() {
    return this == INPUT_STANDARD
        || this == INPUT_STANDARD_ARGUED
        || this == PROGRAM_STANDARD
        || this == PROGRAM_STANDARD_ARGUED;
  }

  /**
   * Returns if program type is for a runner with input-output-stream program.
   *
   * @return {@code true} for a runner with input-output-stream program, {@code false} otherwise
   * @since 11.0.0
   */
  public boolean isConsole() {
    return this == INPUT_CONSOLE
        || this == INPUT_CONSOLE_ARGUED
        || this == PROGRAM_CONSOLE
        || this == PROGRAM_CONSOLE_ARGUED;
  }
}
