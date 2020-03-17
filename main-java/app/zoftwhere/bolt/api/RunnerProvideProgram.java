package app.zoftwhere.bolt.api;

/**
 * <p>Runner accept program interface.
 * </p>
 * <p>This interface that forms the basis for Runner#run() and Runner#runConsole().
 * </p>
 *
 * @since 6.0.0
 */
public interface RunnerProvideProgram
    extends AbstractUnit.RunNoArguments<RunnerProgram>, AbstractUnit.RunWithArguments<RunnerPreProgram> { }
