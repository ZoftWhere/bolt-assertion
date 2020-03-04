package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import app.zoftwhere.function.ThrowingConsumer2;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

@SuppressWarnings("unused")
public class RunnerProxy extends RunnerInterfaces implements RunnerInterfaces.IRunner {

    private final Runner runner = new Runner();

    public RunnerProxy() {
    }

    @Override
    public Runner.RunnerInput input(String... input) {
        return runner.input(input);
    }

    @Override
    public Runner.RunnerInput input(ThrowingFunction0<InputStream> getInputStream) {
        return runner.input(getInputStream);
    }

    @Override
    public Runner.RunnerInput input(ThrowingFunction0<InputStream> getInputStream, Charset decode) {
        return runner.input(getInputStream, decode);
    }

    @Override
    public Runner.RunnerInput loadInput(String resourceName, Class<?> withClass) {
        return runner.loadInput(resourceName, withClass);
    }

    @Override
    public Runner.RunnerInput loadInput(String resourceName, Class<?> withClass, Charset decode) {
        return runner.loadInput(resourceName, withClass, decode);
    }

    @Override
    public Runner.RunnerProgram run(ThrowingConsumer2<Scanner, BufferedWriter> program) {
        return runner.run(program);
    }

    @Override
    public Runner.RunnerPreProgram run(
        ThrowingConsumer3<String[], Scanner, BufferedWriter> program)
    {
        return runner.run(program);
    }

    @Override
    public Runner.RunnerProgram runConsole(ThrowingConsumer2<InputStream, OutputStream> program) {
        return runner.runConsole(program);
    }

    @Override
    public Runner.RunnerProgram runConsole(Charset charset,
        ThrowingConsumer2<InputStream, OutputStream> program)
    {
        return runner.runConsole(charset, program);
    }

    @Override
    public Runner.RunnerPreProgram runConsole(
        ThrowingConsumer3<String[], InputStream, OutputStream> program)
    {
        return runner.runConsole(program);
    }

    @Override
    public Runner.RunnerPreProgram runConsole(Charset charset,
        ThrowingConsumer3<String[], InputStream, OutputStream> program)
    {
        return runner.runConsole(charset, program);
    }

    public static String getBoltExceptionName() {
        return Runner.BoltAssertionException.class.getName();
    }

}
