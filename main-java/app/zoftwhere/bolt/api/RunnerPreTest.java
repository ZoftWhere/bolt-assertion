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
     * Specify the expected program output.
     *
     * @param expected variable argument for expected program line output
     * @return {@link RunnerAsserter}
     * @since 1.0.0
     */
    @Override
    RunnerAsserter expected(String... expected);

    /**
     * Specify the expected program output.
     *
     * @param supplier {@link InputStream} supplier for program input
     * @return {@link RunnerAsserter}
     * @since 6.0.0
     */
    @Override
    RunnerAsserter expected(InputStreamSupplier supplier);

    /**
     * Specify the expected program output.
     *
     * @param supplier {@link InputStream} supplier for expected program output
     * @param charset  {@link Charset} for {@link InputStream}
     * @return {@link RunnerAsserter}
     * @since 6.0.0
     */
    @Override
    RunnerAsserter expected(InputStreamSupplier supplier, Charset charset);

    /**
     * Specify the resource to load as expected program output.
     *
     * @param resourceName resource name of resource to be loaded as expected program output
     * @param withClass    {@link Class} with which its {@link ClassLoader} will load the expected program output
     * @return {@link RunnerAsserter}
     * @since 1.0.0
     */
    @Override
    RunnerAsserter loadExpectation(String resourceName, Class<?> withClass);

    /**
     * Specify the resource to load as expected program output.
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
