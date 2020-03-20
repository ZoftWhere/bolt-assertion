package app.zoftwhere.bolt;

import java.io.OutputStream;
import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;

@FunctionalInterface
interface BoltProgramExecutor {

    Throwable execute(String[] arguments,
        Charset inputCharset,
        InputStreamSupplier inputStreamSupplier,
        Charset outputCharset,
        OutputStream outputStream);

}
