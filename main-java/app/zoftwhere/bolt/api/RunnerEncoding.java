package app.zoftwhere.bolt.api;

import java.nio.charset.Charset;

/**
 * Runner encoding interface.
 *
 * @author Osmund
 * @version 11.2.0
 * @since 11.0.0
 */
public interface RunnerEncoding extends AbstractUnit.Encoding<RunnerInterface> {

    /**
     * <p>Specifies the default character encoding to use for methods where one is not specified.
     * </p>
     *
     * @param encoding character encoding to use as default.
     * @return {@link app.zoftwhere.bolt.api.RunnerInterface} with specified default encoding.
     * @since 11.0.0
     */
    @Override
    @SuppressWarnings("unused")
    RunnerInterface encoding(Charset encoding);

}
