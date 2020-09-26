package app.zoftwhere.bolt;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * <p>Bolt Executor functional interface.
 * </p>
 * <p>This is a package-private interface for creating internal implementations with lambdas.
 * </p>
 *
 * @author Osmund
 * @version 11.2.0
 * @since 9.0.0
 */
@FunctionalInterface
interface BoltExecutor {

    Exception execute(String[] arguments,
        Charset inputCharset,
        InputStream inputStream,
        Charset outputCharset,
        OutputStream outputStream);

}
