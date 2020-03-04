package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Optional;
import java.util.Scanner;

import app.zoftwhere.function.ThrowingConsumer1;
import app.zoftwhere.function.ThrowingConsumer2;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

@SuppressWarnings("unused")
class RunnerInterfaces {

    interface IRunner extends RunnerProgramFirst, RunnerInputFirst { }

    /**
     * The interfaces that forms the basis for Runner#run() and Runner#runConsole().
     */
    private interface RunnerProgramFirst extends //
        RunWithArguments<RunnerPreProgram>, RunNoArguments<RunnerProgram> { }

    interface RunnerPreProgram extends Arguments<RunnerProgram> { }

    interface RunnerProgram extends Input<RunnerOutput> { }

    /**
     * The interfaces that forms the basis for Runner#input() and Runner#loadInput().
     */
    private interface RunnerInputFirst extends Input<RunnerInput> { }

    interface RunnerInput extends Arguments<RunnerLoader>, RunNoArguments<RunnerOutput> { }

    interface RunnerLoader extends RunWithArguments<RunnerOutput> { }

    interface RunnerOutput extends Comparison<RunnerPreTest, String>, RunnerOutputCommon { }

    interface RunnerPreTest extends RunnerOutputCommon { }

    interface RunnerOutputCommon extends Expected<RunnerAsserter<Runner.RunnerTestResult>> {

        String[] output();

        Exception exception();
    }

    interface RunnerAsserter<T extends AbstractTestResult> extends Assertions<T> {

        RunnerTestResult result();
    }

    static abstract class AbstractTestResult implements RunnerTestResult {
    }

    interface RunnerTestResult extends TestResult { }

    interface RunNoArguments<T> {

        T run(ThrowingConsumer2<Scanner, BufferedWriter> program);

        T runConsole(ThrowingConsumer2<InputStream, OutputStream> program);

        T runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program);
    }

    interface RunWithArguments<T> {

        T run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program);

        T runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program);

        T runConsole(Charset charset, ThrowingConsumer3<String[], InputStream, OutputStream> program);
    }

    interface Arguments<T> {

        T argument(String... arguments);
    }

    interface Input<T> {

        T input(String... input);

        T input(ThrowingFunction0<InputStream> getInputStream);

        T input(ThrowingFunction0<InputStream> getInputStream, Charset charset);

        T loadInput(String resourceName, Class<?> withClass);

        T loadInput(String resourceName, Class<?> withClass, Charset charset);
    }

    interface Comparison<T, C> {

        T comparator(Comparator<C> comparator);
    }

    interface Expected<T> {

        T expected(String... expected);

        T expected(ThrowingFunction0<InputStream> getInputStream);

        T expected(ThrowingFunction0<InputStream> getInputStream, Charset charset);

        T loadExpectation(String resourceName, Class<?> withClass);

        T loadExpectation(String resourceName, Class<?> withClass, Charset charset);
    }

    interface Assertions<T> {

        void assertSuccess();

        void assertFail();

        void assertException();

        void assertCheck(ThrowingConsumer1<T> consumer);
    }

    interface TestResult {

        boolean isSuccess();

        boolean isFail();

        String[] output();

        String[] expected();

        Optional<Exception> exception();

        Optional<String> message();
    }

}
