package app.zoftwhere.bolt.api;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Optional;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsole;
import app.zoftwhere.bolt.api.RunnerInterface.RunConsoleArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandard;
import app.zoftwhere.bolt.api.RunnerInterface.RunStandardArgued;
import app.zoftwhere.bolt.api.RunnerInterface.RunnerResultConsumer;

abstract class AbstractUnit {

    @SuppressWarnings("unused")
    public AbstractUnit() {
    }

    @SuppressWarnings("unused")
    interface RunNoArguments<T> {

        T run(RunStandard program);

        T run(Charset charset, RunStandard program);

        T runConsole(RunConsole program);

        T runConsole(Charset charset, RunConsole program);
    }

    @SuppressWarnings("unused")
    interface RunWithArguments<T> {

        T run(RunStandardArgued program);

        T run(Charset charset, RunStandardArgued program);

        T runConsole(RunConsoleArgued program);

        T runConsole(Charset charset, RunConsoleArgued program);
    }

    @SuppressWarnings("unused")
    interface Arguments<T> {

        T argument(String... arguments);
    }

    @SuppressWarnings("unused")
    interface Input<T> {

        T input(String... input);

        T input(InputStreamSupplier supplier);

        T input(InputStreamSupplier supplier, Charset charset);

        T loadInput(String resourceName, Class<?> withClass);

        T loadInput(String resourceName, Class<?> withClass, Charset charset);
    }

    @SuppressWarnings("unused")
    interface Comparison<T, C> {

        T comparator(Comparator<C> comparator);
    }

    @SuppressWarnings("unused")
    interface Output {

        String[] output();

        Optional<Exception> exception();
    }

    @SuppressWarnings("unused")
    interface Expected<T> {

        T expected(String... expected);

        T expected(InputStreamSupplier supplier);

        T expected(InputStreamSupplier supplier, Charset charset);

        T loadExpectation(String resourceName, Class<?> withClass);

        T loadExpectation(String resourceName, Class<?> withClass, Charset charset);
    }

    @SuppressWarnings("unused")
    interface Assertions<T extends TestResult> {

        void assertSuccess();

        void assertFailure();

        void assertException();

        void assertCheck(RunnerResultConsumer consumer);

        void onOffence(RunnerResultConsumer consumer);

        T result();
    }

    @SuppressWarnings("unused")
    interface TestResult {

        boolean isSuccess();

        boolean isFailure();

        boolean isException();

        int offendingIndex();

        String[] output();

        String[] expected();

        Optional<Exception> exception();

        Optional<String> message();
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    @FunctionalInterface
    interface CallerNoArguments<T1, T2> {

        void call(T1 t1, T2 t2) throws Throwable;
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    @FunctionalInterface
    interface CallerWithArguments<T1, T2> {

        void call(String[] arguments, T1 t1, T2 t2) throws Throwable;
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    @FunctionalInterface
    interface ThrowingSupplier<T> {

        T get() throws Throwable;
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    @FunctionalInterface
    interface ThrowingConsumer<T> {

        void accept(T input) throws Throwable;
    }

}
