package app.zoftwhere.bolt;

import java.io.OutputStream;
import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface;

@FunctionalInterface
interface BoltProgramExecutor {

    Throwable execute(String[] arguments,
        Charset inputCharset,
        RunnerInterface.InputStreamSupplier inputStreamSupplier,
        Charset outputCharset,
        OutputStream outputStream);

}
