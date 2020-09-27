package app.zoftwhere.bolt.api;

import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;

/**
 * <p>Runner provide input interface.
 * </p>
 * <p>This interface that forms the basis for Runner#input() and Runner#loadInput().
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
 * @since 6.0.0
 */
public interface RunnerProvideInput extends AbstractUnit.Input<RunnerProgramInput> {

    /**
     * Specify the input.
     *
     * @param input program input
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramInput input(String... input);

    /**
     * <p>Specify the input.
     * </p>
     * <p>The variable-argument input will be converted to a program input {@link java.io.InputStream} with the {@link
     * java.nio.charset.Charset} provided.</p>
     *
     * @param charset character encoding of input
     * @param input   program input
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 11.0.0
     */
    @Override
    RunnerProgramInput input(Charset charset, String... input);

    /**
     * Specify the input.
     *
     * @param supplier {@link java.io.InputStream} supplier for program input
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    RunnerProgramInput input(InputStreamSupplier supplier);

    /**
     * Specify the input.
     *
     * @param supplier {@link java.io.InputStream} supplier for program input
     * @param charset  character encoding of {@link java.io.InputStream}
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 6.0.0
     */
    @Override
    RunnerProgramInput input(InputStreamSupplier supplier, Charset charset);

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading program input
     * @param withClass    {@link java.lang.Class} with which to retrieve the program input
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramInput loadInput(String resourceName, Class<?> withClass);

    /**
     * Specify the input.
     *
     * @param resourceName resource name for loading program input
     * @param withClass    {@link java.lang.Class} with which to retrieve the program input
     * @param charset      character encoding of resource
     * @return {@link app.zoftwhere.bolt.api.RunnerProgramOutput}
     * @since 1.0.0
     */
    @Override
    RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset charset);

}
