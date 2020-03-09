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

    public interface RunnerOutputCommon extends Expected<RunnerAsserter> {

        String[] output();

        Exception exception();
    }

    interface RunnerAsserter extends Assertions<Runner.RunnerTestResult> { }

    interface RunnerTestResult extends TestResult { }

    static abstract class AbstractTestResult implements TestResult {
    }

    private interface RunNoArguments<T> {

        T run(ThrowingConsumer2<Scanner, BufferedWriter> program);

        T run(Charset charset, ThrowingConsumer2<Scanner, BufferedWriter> program);

        T runConsole(ThrowingConsumer2<InputStream, OutputStream> program);

        T runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program);
    }

    private interface RunWithArguments<T> {

        T run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program);

        T run(Charset charset, ThrowingConsumer3<String[], Scanner, BufferedWriter> program);

        T runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program);

        T runConsole(Charset charset, ThrowingConsumer3<String[], InputStream, OutputStream> program);
    }

    private interface Arguments<T> {

        T argument(String... arguments);
    }

    private interface Input<T> {

        T input(String... input);

        T input(ThrowingFunction0<InputStream> getInputStream);

        T input(ThrowingFunction0<InputStream> getInputStream, Charset charset);

        T loadInput(String resourceName, Class<?> withClass);

        T loadInput(String resourceName, Class<?> withClass, Charset charset);
    }

    private interface Comparison<T, C> {

        T comparator(Comparator<C> comparator);
    }

    private interface Expected<T> {

        T expected(String... expected);

        T expected(ThrowingFunction0<InputStream> getInputStream);

        T expected(ThrowingFunction0<InputStream> getInputStream, Charset charset);

        T loadExpectation(String resourceName, Class<?> withClass);

        T loadExpectation(String resourceName, Class<?> withClass, Charset charset);
    }

    private interface Assertions<T extends AbstractTestResult> {

        void assertSuccess();

        void assertFailure();

        void assertException();

        void assertCheck(ThrowingConsumer1<T> consumer);

        void onOffence(ThrowingConsumer1<T> consumer);

        T result();
    }

    private interface TestResult {

        boolean isSuccess();

        boolean isFailure();

        boolean isException();

        int offendingIndex();

        String[] output();

        String[] expected();

        Optional<Exception> exception();

        Optional<String> message();
    }

}
