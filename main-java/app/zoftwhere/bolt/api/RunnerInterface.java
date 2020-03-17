package app.zoftwhere.bolt.api;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Runner instance interface.
 *
 * @since 6.0.0
 */
public interface RunnerInterface extends RunnerProvideProgram, RunnerProvideInput {

    /**
     * {@link InputStreamSupplier} provides a function interface for creating an {@link InputStream} supplier.
     *
     * @since 6.0.0
     */
    @FunctionalInterface
    interface InputStreamSupplier extends AbstractUnit.ThrowingSupplier<InputStream> {

        /**
         * @return {@link InputStream}
         * @throws Throwable throwable from creating an {@link InputStream}
         */
        @Override
        InputStream get() throws Throwable;
    }

    /**
     * {@link RunConsole} provides a functional interfaces for creating program calls.
     *
     * @since 6.0.0
     */
    @FunctionalInterface
    interface RunConsole extends AbstractUnit.CallerNoArguments<InputStream, OutputStream> {

        /**
         * @param inputStream  program input
         * @param outputStream program {@link OutputStream}
         * @throws Throwable program {@link Throwable} on error
         */
        @Override
        void call(InputStream inputStream, OutputStream outputStream) throws Throwable;
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
         * @param inputStream  program input
         * @param outputStream program {@link OutputStream}
         * @throws Throwable program {@link Throwable} on error
         */
        @Override
        void call(String[] arguments, InputStream inputStream, OutputStream outputStream) throws Throwable;
    }

    /**
     * {@link RunnerResultConsumer} provides a functional interface for creating program test result asserters.
     *
     * @since 6.0.0
     */
    @FunctionalInterface
    interface RunnerResultConsumer extends AbstractUnit.ThrowingConsumer<RunnerProgramResult> {

        /**
         * A program test result consumer for asserting a test result.
         *
         * @param input program test result
         * @throws Throwable consumer throwable on test failure and/or error
         */
        @Override
        void accept(RunnerProgramResult input) throws Throwable;
    }

    /**
     * {@link RunStandard} provides a functional interfaces for creating program calls.
     *
     * @since 6.0.0
     */
    @FunctionalInterface
    interface RunStandard extends AbstractUnit.CallerNoArguments<Scanner, BufferedWriter> {

        /**
         * @param scanner scanner
         * @param writer  buffered writer
         * @throws Throwable program {@link Throwable} on error
         */
        @Override
        void call(Scanner scanner, BufferedWriter writer) throws Throwable;
    }

    /**
     * {@link RunStandardArgued} provides a functional interfaces for creating program calls.
     *
     * @since 6.0.0
     */
    @FunctionalInterface
    interface RunStandardArgued extends AbstractUnit.CallerWithArguments<Scanner, BufferedWriter> {

        /**
         * @param arguments program argument array
         * @param scanner   scanner
         * @param writer    buffered writer
         * @throws Throwable program {@link Throwable} on error
         */
        @Override
        void call(String[] arguments, Scanner scanner, BufferedWriter writer) throws Throwable;
    }

}
