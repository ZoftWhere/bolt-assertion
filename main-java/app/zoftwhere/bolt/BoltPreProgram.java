package app.zoftwhere.bolt;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;

/**
 * Bolt pre-program loader class.
 *
 * @since 6.0.0
 */
class BoltPreProgram implements RunnerPreProgram {

    private final RunConsoleArgued program;

    private final Charset outputCharset;

    BoltPreProgram(RunConsoleArgued program, Charset outputCharset) {
        this.program = program;
        this.outputCharset = outputCharset;
    }

    /**
     * Specify program arguments.
     *
     * @param arguments program arguments
     * @return {@link RunnerProgram}
     * @since 1.0.0
     */
    @Override
    public RunnerProgram argument(String... arguments) {
        return new BoltProgram(program, arguments, outputCharset);
    }

}
