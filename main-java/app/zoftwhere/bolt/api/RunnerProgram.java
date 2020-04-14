package app.zoftwhere.bolt.api;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;

/**
 * Runner program interface.
 *
 * @since 6.0.0
 */
public interface RunnerProgram extends AbstractUnit.Input<RunnerProgramOutput> {

    /**
     * Specify the input.
     *
     * @param input program input
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramOutput input(String... input);

    /**
     * Specify the input.
     *
     * @param supplier {@link java.io.InputStream} supplier for program input
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    RunnerProgramOutput input(InputStreamSupplier supplier);

    /**
     * Specify the input.
     *
     * @param supplier {@link java.io.InputStream} supplier for program input
     * @param charset  character encoding of {@link java.io.InputStream}
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    RunnerProgramOutput input(InputStreamSupplier supplier, Charset charset);

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading program input
     * @param withClass    {@link Class} with which to retrieve the program input
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramOutput loadInput(String resourceName, Class<?> withClass);

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading program input
     * @param withClass    {@link Class} with which to retrieve the program input
     * @param charset      character encoding of resource
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramOutput loadInput(String resourceName, Class<?> withClass, Charset charset);

}
