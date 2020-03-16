package app.zoftwhere.bolt.api;

/**
 * The public interface that forms the basis for Runner#run() and Runner#runConsole().
 */
public interface RunnerProgramFirst extends //
    AbstractUnit.RunWithArguments<RunnerPreProgram>,
    AbstractUnit.RunNoArguments<RunnerProgram> { }
