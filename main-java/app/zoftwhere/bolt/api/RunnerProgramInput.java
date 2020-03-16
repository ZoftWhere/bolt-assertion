package app.zoftwhere.bolt.api;

/**
 * Runner program execution input interface.
 *
 * @since 6.0.0
 */
public interface RunnerProgramInput extends AbstractUnit.Arguments<RunnerLoader>,
    AbstractUnit.RunNoArguments<RunnerProgramOutput> { }
