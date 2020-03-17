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
     * @param supplier {@code InputStream} function for input
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    RunnerProgramOutput input(InputStreamSupplier supplier);

    /**
     * Specify the input.
     *
     * @param supplier {@code InputStream} function for input
     * @param charset  the {@code InputStream} character set encoding
     * @return {@link RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    RunnerProgramOutput input(InputStreamSupplier supplier, Charset charset);

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading input
     * @param withClass    resource class for retrieving resource as {@code InputStream}
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramOutput loadInput(String resourceName, Class<?> withClass);

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading input
     * @param withClass    resource class for retrieving resource as {@code InputStream}
     * @param charset      resource character set encoding
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramOutput loadInput(String resourceName, Class<?> withClass, Charset charset);

}
