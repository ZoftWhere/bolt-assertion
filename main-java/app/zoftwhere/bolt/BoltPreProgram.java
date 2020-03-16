package app.zoftwhere.bolt;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.function.ThrowingConsumer3;

/**
 * Bolt pre-program loader class.
 *
 * @since 6.0.0
 */
class BoltPreProgram implements RunnerPreProgram {

    private final ThrowingConsumer3<String[], InputStream, OutputStream> program;

    private final Charset outputCharset;

    BoltPreProgram(ThrowingConsumer3<String[], InputStream, OutputStream> program, Charset outputCharset) {
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
