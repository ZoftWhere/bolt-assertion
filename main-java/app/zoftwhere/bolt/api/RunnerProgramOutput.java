package app.zoftwhere.bolt.api;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;

/**
 * Runner program execution output interface.
 *
 * @since 6.0.0
 */
public interface RunnerProgramOutput
    extends AbstractUnit.Comparison<RunnerPreTest, String>, RunnerPreTest
{

    /**
     * <p>Specify the comparator.
     * </p>
     * <p>The comparator will only be loaded if the program output does not contain an exception.
     * </p>
     * <p>The execution result will contain an exception if the loaded comparator is null.
     * </p>
     *
     * @param comparator {@link String} {@link Comparator} for program line output
     * @return {@link RunnerPreTest}
     * @since 1.0.0
     */
    @Override
    RunnerPreTest comparator(Comparator<String> comparator);

    @Override
    String[] output();

    @Override
    Optional<Exception> error();

    @Override
    Duration executionDuration();

    @Override
    RunnerAsserter expected(String... expected);

    @Override
    RunnerAsserter expected(InputStreamSupplier supplier);

    @Override
    RunnerAsserter expected(InputStreamSupplier supplier, Charset charset);

    @Override
    RunnerAsserter loadExpectation(String resourceName, Class<?> withClass);

    @Override
    RunnerAsserter loadExpectation(String resourceName, Class<?> withClass, Charset charset);

}
