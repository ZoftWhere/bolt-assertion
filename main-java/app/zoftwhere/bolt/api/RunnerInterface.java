package app.zoftwhere.bolt.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Runner instance interface.
 *
 * @since 6.0.0
 */
public interface RunnerInterface extends RunnerProvideProgram, RunnerProvideInput {

    /**
     * {@link InputStreamSupplier} provides a functional interface for creating an {@link InputStream} supplier.
     *
     * @since 6.0.0
     */
    @FunctionalInterface
    interface InputStreamSupplier extends AbstractUnit.ThrowingSupplier<InputStream> {

        /**
         * @return {@link InputStream}
         * @throws Exception for {@link InputStream} error
         */
        @Override
        InputStream get() throws Exception;
    }

    /**
     * {@link RunConsole} provides a functional interfaces for creating program calls.
     *
     * @since 6.0.0
     */
    @FunctionalInterface
    interface RunConsole extends AbstractUnit.CallerNoArguments<InputStream, OutputStream> {

        /**
         * @param inputStream  program {@link InputStream}
         * @param outputStream program {@link OutputStream}
         * @throws Exception program error
         */
        @Override
        void call(InputStream inputStream, OutputStream outputStream) throws Exception;
    }

    /**
     * {@link RunConsoleArgued} provides a functional interfaces for creating program calls.
     *
     * @since 6.0.0
     */
    @FunctionalInterface
    interface RunConsoleArgued extends AbstractUnit.CallerWithArguments<InputStream, OutputStream> {

        /**
         * @param arguments    program argument array
         * @param inputStream  program {@link InputStream}
         * @param outputStream program {@link OutputStream}
         * @throws Exception program error
         */
        @Override
        void call(String[] arguments, InputStream inputStream, OutputStream outputStream) throws Exception;
    }

    /**
     * {@link RunnerResultConsumer} provides a functional interface for creating execution result asserters.
     *
     * @since 6.0.0
     */
    @FunctionalInterface
    interface RunnerResultConsumer extends AbstractUnit.ThrowingConsumer<RunnerResult> {

        /**
         * A runner result consumer for asserting a execution result.
         *
         * @param result execution result
         * @throws Exception for failure state or error state (consumer driven)
         */
        @Override
        void accept(RunnerResult result) throws Exception;
    }

    /**
     * {@link RunStandard} provides a functional interfaces for creating program calls.
     *
     * @since 7.0.0
     */
    @FunctionalInterface
    interface RunStandard extends AbstractUnit.CallerNoArguments<Scanner, PrintStream> {

        /**
         * @param scanner program {@link Scanner}
         * @param out     program {@link PrintStream}
         * @throws Exception on program error
         */
        @Override
        void call(Scanner scanner, PrintStream out) throws Exception;
    }

    /**
     * {@link RunStandardArgued} provides a functional interfaces for creating program calls.
     *
     * @since 7.0.0
     */
    @FunctionalInterface
    interface RunStandardArgued extends AbstractUnit.CallerWithArguments<Scanner, PrintStream> {

        /**
         * @param arguments program argument array
         * @param scanner   program {@link Scanner}
         * @param out       program {@link PrintStream}
         * @throws Exception on program error
         */
        @Override
        void call(String[] arguments, Scanner scanner, PrintStream out) throws Exception;
    }

}
