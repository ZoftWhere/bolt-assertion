package app.zoftwhere.bolt;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramOutput;

import static app.zoftwhere.bolt.RunnerHelper.forInput;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Bolt program class.
 *
 * @since 6.0.0
 */
class BoltProgram implements RunnerProgram {

    private final RunConsoleArgued program;

    private final String[] arguments;

    private final Charset programCharset;

    BoltProgram(
        RunConsoleArgued program, //
        String[] arguments, //
        Charset programCharset) //
    {
        this.program = program;
        this.arguments = arguments;
        this.programCharset = programCharset;
    }

    BoltProgram(RunConsole program, Charset programCharset) {
        this.program = (strings, inputStream, outputStream) -> { /**/
            program.call(inputStream, outputStream);
        };
        this.arguments = null;
        this.programCharset = programCharset;
    }

    @Override
    public RunnerProgramOutput input(String... input) {
        final InputStreamSupplier getInput = () -> forInput(input);
        return RunnerHelper.executeRunConsole(program, programCharset, arguments, getInput, UTF_8);
    }

    @Override
    public RunnerProgramOutput input(InputStreamSupplier getInputStream) {
        return create(getInputStream, UTF_8);
    }

    @Override
    public RunnerProgramOutput input(InputStreamSupplier getInputStream, Charset charset) {
        return create(getInputStream, charset);
    }

    @Override
    public RunnerProgramOutput loadInput(String resourceName, Class<?> withClass) {
        final InputStreamSupplier inputSupplier = () -> withClass.getResourceAsStream(resourceName);
        return create(inputSupplier, UTF_8);
    }

    @Override
    public RunnerProgramOutput loadInput(String resourceName, Class<?> withClass, Charset charset) {
        final InputStreamSupplier inputSupplier = () -> withClass.getResourceAsStream(resourceName);
        return create(inputSupplier, charset);
    }

    /**
     * Creates a {@link RunnerProgramOutput} for the program input {@code InputStream}.
     *
     * @param getInputStream {@code InputStream} supplier for the program input
     * @param charset        the charset of the {@code InputStream}
     * @return a {@link RunnerProgramOutput instance}
     * @since 6.0.0
     */
    private RunnerProgramOutput create(InputStreamSupplier getInputStream, Charset charset) {
        return RunnerHelper.executeRunConsole(program, programCharset, arguments, getInputStream, charset);
    }

}
