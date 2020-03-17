package app.zoftwhere.bolt.api;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Optional;

/**
 * Runner program execution output interface.
 *
 * @since 6.0.0
 */
public interface RunnerProgramOutput
    extends AbstractUnit.Comparison<RunnerPreTest, String>, RunnerPreTest
{

    /**
     * Specify the comparator.
     *
     * @param comparator {@code String} comparator
     * @return {@link RunnerPreTest}
     * @since 1.0.0
     */
    @Override
    RunnerPreTest comparator(Comparator<String> comparator);

    @Override
    String[] output();

    @Override
    Optional<Exception> exception();

    @Override
    RunnerAsserter expected(String... expected);

    @Override
    RunnerAsserter expected(RunnerInterface.InputStreamSupplier supplier);

    @Override
    RunnerAsserter expected(RunnerInterface.InputStreamSupplier supplier, Charset charset);

    @Override
    RunnerAsserter loadExpectation(String resourceName, Class<?> withClass);

    @Override
    RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset);

}
