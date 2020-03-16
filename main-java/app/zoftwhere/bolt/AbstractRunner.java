package app.zoftwhere.bolt;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

import app.zoftwhere.bolt.api.RunnerInput;
import app.zoftwhere.bolt.api.RunnerInterface;
import app.zoftwhere.bolt.api.RunnerPreProgram;
import app.zoftwhere.bolt.api.RunnerProgram;
import app.zoftwhere.function.ThrowingConsumer2;
import app.zoftwhere.function.ThrowingConsumer3;
import app.zoftwhere.function.ThrowingFunction0;

public abstract class AbstractRunner implements RunnerInterface {

    public AbstractRunner() {
    }

    @Override
    public abstract RunnerProgram run(ThrowingConsumer2<Scanner, BufferedWriter> program);

    @Override
    public abstract RunnerProgram run(Charset charset, ThrowingConsumer2<Scanner, BufferedWriter> program);

    @Override
    public abstract RunnerProgram runConsole(ThrowingConsumer2<InputStream, OutputStream> program);

    @Override
    public abstract RunnerProgram runConsole(Charset charset, ThrowingConsumer2<InputStream, OutputStream> program);

    @Override
    public abstract RunnerPreProgram run(ThrowingConsumer3<String[], Scanner, BufferedWriter> program);

    @Override
    public abstract RunnerPreProgram run(Charset charset, ThrowingConsumer3<String[], Scanner, BufferedWriter> program);

    @Override
    public abstract RunnerPreProgram runConsole(ThrowingConsumer3<String[], InputStream, OutputStream> program);

    @Override
    public abstract RunnerPreProgram runConsole(Charset charset,
        ThrowingConsumer3<String[], InputStream, OutputStream> program);

    @Override
    public abstract RunnerInput input(String... input);

    @Override
    public abstract RunnerInput input(ThrowingFunction0<InputStream> getInputStream);

    @Override
    public abstract RunnerInput input(ThrowingFunction0<InputStream> getInputStream, Charset charset);

    @Override
    public abstract RunnerInput loadInput(String resourceName, Class<?> withClass);

    @Override
    public abstract RunnerInput loadInput(String resourceName, Class<?> withClass, Charset charset);

}
