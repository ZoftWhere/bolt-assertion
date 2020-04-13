package app.zoftwhere.bolt.api;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;

/**
 * Runner pre-test interface.
 *
 * @since 6.0.0
 */
public interface RunnerPreTest extends AbstractUnit.Expected<RunnerAsserter>, AbstractUnit.Output {

    /**
     * Retrieve the actual program output.
     *
     * @return array copy of the program output
     * @since 1.0.0
     */
    @Override
    String[] output();

    /**
     * Retrieve the program error.
     *
     * @return {@link Optional} of the program error (empty on success or failure)
     * @since 6.0.0
     */
    @Override
    Optional<Exception> exception();

    /**
     * <p>Specify the expected program output.
     * </p>
     * <p>The expectation will only be loaded if the expectation is not null, and has one or more items.
     * </p>
     * <p>If the expectation is loaded, the program does not have an exception, and the array contains a null, the
     * program result will be loaded with a corresponding exception.
     * </p>
     *
     * @param expected variable argument for expected program line output
     * @return {@link RunnerAsserter}
     * @since 1.0.0
     */
    @Override
    RunnerAsserter expected(String... expected);

    /**
     * <p>Specify the expected program output.
     * </p>
     * <p>The expectation will only be loaded if the program output does not contain an exception.
     * </p>
     * <p>If the expectation is loading, and an exception occurs, the program result will be loaded with a
     * corresponding exception.
     * </p>
     *
     * @param supplier {@link InputStream} supplier for program input
     * @return {@link RunnerAsserter}
     * @since 6.0.0
     */
    @Override
    RunnerAsserter expected(InputStreamSupplier supplier);

    /**
     * <p>Specify the expected program output.
     * </p>
     * <p>The expectation will only be loaded if the program output does not contain an exception.
     * </p>
     * <p>If the expectation is loading, and an exception occurs, the program result will be loaded with a
     * corresponding exception.
     * </p>
     *
     * @param supplier {@link InputStream} supplier for expected program output
     * @param charset  {@link Charset} for {@link InputStream}
     * @return {@link RunnerAsserter}
     * @since 6.0.0
     */
    @Override
    RunnerAsserter expected(InputStreamSupplier supplier, Charset charset);

    /**
     * <p>Specify the resource to load as expected program output.
     * </p>
     * <p>The expectation will only be loaded if the program output does not contain an exception.
     * </p>
     * <p>If the expectation is loading, and an exception occurs, the program result will be loaded with a
     * corresponding exception.
     * </p>
     *
     * @param resourceName resource name of resource to be loaded as expected program output
     * @param withClass    {@link Class} with which its {@link ClassLoader} will load the expected program output
     * @return {@link RunnerAsserter}
     * @since 1.0.0
     */
    @Override
    RunnerAsserter loadExpectation(String resourceName, Class<?> withClass);

    /**
     * <p>Specify the resource to load as expected program output.
     * </p>
     * <p>The expectation will only be loaded if the program output does not contain an exception.
     * </p>
     * <p>If the expectation is loading, and an exception occurs, the program result will be loaded with a
     * corresponding exception.
     * </p>
     *
     * @param resourceName resource name of resource to be loaded as expected program output
     * @param withClass    {@link Class} with which its {@link ClassLoader} will load the expected program output
     * @param charset      {@link Charset} of the resource
     * @return {@link RunnerAsserter}
     * @since 1.0.0
     */
    @Override
    RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset);

}
