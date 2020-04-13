package app.zoftwhere.bolt;

import java.io.OutputStream;
import java.nio.charset.Charset;

import app.zoftwhere.bolt.api.RunnerInterface.InputStreamSupplier;

/**
 * <p>Bolt Program Executor functional interface.
 * </p>
 * <p>This is a package-private interface for creating internal implementations with lambdas.
 * </p>
 *
 * @since 6.0.0
 */
@FunctionalInterface
interface BoltProgramExecutor {

    Throwable execute(String[] arguments,
        Charset inputCharset,
        InputStreamSupplier inputStreamSupplier,
        Charset outputCharset,
        OutputStream outputStream);

}
