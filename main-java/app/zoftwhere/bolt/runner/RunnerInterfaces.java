package app.zoftwhere.bolt.runner;

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

    interface IRunner<T extends TestResult> extends RunnerProgramFirst<T>, RunnerInputFirst<T> { }

    //

    private interface RunnerProgramFirst<T> extends //
        RunWithArguments<RunnerPreProgram<T>>, RunNoArguments<RunnerProgram<T>> { }

    interface RunnerPreProgram<T> extends Arguments<RunnerProgram<T>> { }

    interface RunnerProgram<T> extends Input<RunnerOutput<T>> { }

    //

    private interface RunnerInputFirst<T> extends Input<RunnerInput<T>> { }

    interface RunnerInput<T> extends Arguments<RunnerLoader<T>>, RunNoArguments<RunnerOutput<T>> { }

    interface RunnerLoader<T> extends RunWithArguments<RunnerOutput<T>> { }

    //

    interface RunnerOutput<T> extends Comparison<RunnerPreTest<T>, String>, RunnerOutputCommon<T> { }

    interface RunnerPreTest<T> extends RunnerOutputCommon<T> { }

    @SuppressWarnings("unused")
    interface RunnerOutputCommon<T> extends Expected<RunnerAsserter<T>> {

        String[] output();

        Exception exception();
    }

    @SuppressWarnings("unused")
    interface RunnerAsserter<T> extends Assertions<T> {

        RunnerTestResult result();
    }

    interface RunnerTestResult extends TestResult { }

    //

    @SuppressWarnings("unused")
    interface RunNoArguments<T> {

        T run(ThrowingConsumer2<Scanner, BufferedWriter> program);

        T runConsole(ThrowingConsumer2<InputStream, OutputStream> program);

        T runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program);
    }

    @SuppressWarnings("unused")
    interface RunWithArguments<T> {

        T run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program);

        T runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program);

        T runConsole(Charset charset, ThrowingConsumer3<String[], InputStream, OutputStream> program);
    }

    @SuppressWarnings("unused")
    interface Arguments<T> {

        T argument(String... arguments);
    }

    @SuppressWarnings("unused")
    interface Input<T> {

        T input(String... input);

        T input(ThrowingFunction0<InputStream> getInputStream);

        T input(ThrowingFunction0<InputStream> getInputStream, Charset charset);

        T loadInput(String resourceName, Class<?> withClass);

        T loadInput(String resourceName, Class<?> withClass, Charset charset);
    }

    @SuppressWarnings("unused")
    interface Comparison<T, C> {

        T comparator(Comparator<C> comparator);
    }

    @SuppressWarnings("unused")
    interface Expected<T> {

        T expected(String... expected);

        T expected(ThrowingFunction0<InputStream> getInputStream);

        T expected(ThrowingFunction0<InputStream> getInputStream, Charset charset);

        T loadExpectation(String resourceName, Class<?> withClass);

        T loadExpectation(String resourceName, Class<?> withClass, Charset charset);
    }

    @SuppressWarnings("unused")
    interface Assertions<T> {

        void assertSuccess();

        void assertFail();

        void assertException();

        void assertCheck(ThrowingConsumer1<T> consumer);
    }

    @SuppressWarnings("unused")
    interface TestResult {

        boolean isSuccess();

        boolean isFail();

        String[] output();

        String[] expected();

        Optional<Exception> exception();

        Optional<String> message();
    }
}
