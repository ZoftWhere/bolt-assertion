package app.zoftwhere.bolt.api;

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

abstract class AbstractUnit {

    interface RunNoArguments<T> {

        T run(ThrowingConsumer2<Scanner, BufferedWriter> program);

        T run(Charset charset, ThrowingConsumer2<Scanner, BufferedWriter> program);

        T runConsole(ThrowingConsumer2<InputStream, OutputStream> program);

        T runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program);
    }

    interface RunWithArguments<T> {

        T run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program);

        T run(Charset charset, ThrowingConsumer3<String[], Scanner, BufferedWriter> program);

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

    interface Assertions<T extends TestResult> {

        void assertSuccess();

        void assertFailure();

        void assertException();

        void assertCheck(ThrowingConsumer1<T> consumer);

        void onOffence(ThrowingConsumer1<T> consumer);

        T result();
    }

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

}
