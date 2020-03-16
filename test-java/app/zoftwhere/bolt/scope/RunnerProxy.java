package app.zoftwhere.bolt.scope;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import app.zoftwhere.bolt.AbstractRunner;
import app.zoftwhere.bolt.Runner;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.bolt.api.RunnerProgramInput;
import app.zoftwhere.function.ThrowingConsumer2;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

public class RunnerProxy extends AbstractRunner {

    private final Runner runner = new Runner();

    @SuppressWarnings("WeakerAccess")
    public RunnerProxy() {
    }

    @Override
    public RunnerProgramInput input(String... input) {
        return runner.input(input);
    }

    @Override
    public RunnerProgramInput input(ThrowingFunction0<InputStream> getInputStream) {
        return runner.input(getInputStream);
    }

    @Override
    public RunnerProgramInput input(ThrowingFunction0<InputStream> getInputStream, Charset decode) {
        return runner.input(getInputStream, decode);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass) {
        return runner.loadInput(resourceName, withClass);
    }

    @Override
    public RunnerProgramInput loadInput(String resourceName, Class<?> withClass, Charset decode) {
        return runner.loadInput(resourceName, withClass, decode);
    }

    @Override
    public RunnerProgram run(ThrowingConsumer2<Scanner, BufferedWriter> program) {
        return runner.run(program);
    }

    @Override
    public RunnerProgram run(Charset charset, ThrowingConsumer2<Scanner, BufferedWriter> program) {
        return runner.run(charset, program);
    }

    @Override
    public RunnerPreProgram run(
        ThrowingConsumer3<String[], Scanner, BufferedWriter> program)
    {
        return runner.run(program);
    }

    @Override
    public RunnerPreProgram run(
        Charset charset, ThrowingConsumer3<String[], Scanner, BufferedWriter> program)
    {
        return runner.run(charset, program);
    }

    @Override
    public RunnerProgram runConsole(ThrowingConsumer2<InputStream, OutputStream> program) {
        return runner.runConsole(program);
    }

    @Override
    public RunnerProgram runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program)
    {
        return runner.runConsole(charset, program);
    }

    @Override
    public RunnerPreProgram runConsole(
        ThrowingConsumer3<String[], InputStream, OutputStream> program)
    {
        return runner.runConsole(program);
    }

    @Override
    public RunnerPreProgram runConsole(Charset charset,
        ThrowingConsumer3<String[], InputStream, OutputStream> program)
    {
        return runner.runConsole(charset, program);
    }

}
