package app.zoftwhere.bolt.api;

/**
 * Runner program execution output interface.
 *
 * @since 6.0.0
 */
@SuppressWarnings("WeakerAccess")
public interface RunnerProgramOutput
    extends AbstractUnit.Comparison<RunnerPreTest, String>, RunnerPreTest { }
