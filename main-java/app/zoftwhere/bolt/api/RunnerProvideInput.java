package app.zoftwhere.bolt.api;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;

/**
 * <p>Runner provide input interface.
 * </p>
 * <p>This interface that forms the basis for Runner#input() and Runner#loadInput().
 * </p>
 *
 * @since 6.0.0
 */
public interface RunnerProvideInput extends AbstractUnit.Input<RunnerProgramInput> {

    /**
     * Specify the input.
     *
     * @param input program input
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramInput input(String... input);

    /**
     * Specify the input.
     *
     * @param supplier {@code InputStream} function for input
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramInput input(InputStreamSupplier supplier);

    /**
     * Specify the input.
     *
     * @param supplier {@code InputStream} function for input
     * @param charset  the {@code InputStream} character set encoding
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramInput input(InputStreamSupplier supplier, Charset charset);

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading input
     * @param withClass    resource class for retrieving resource as {@code InputStream}
     * @return {@link RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramInput loadInput(String resourceName, Class<?> withClass);

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
    RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset);

}
