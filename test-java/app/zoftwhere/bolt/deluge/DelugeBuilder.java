package app.zoftwhere.bolt.deluge;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import app.zoftwhere.bolt.Runner;

/**
 * <p>Deluge Builder.
 * </p>
 *
 * @author Osmund
 * @since 6.0.0
 */
public class DelugeBuilder {

    /**
     * DelugeBuilder with program type, program setting, and program input.
     *
     * @param type    deluge program type
     * @param setting deluge program setting
     * @param input   deluge program data
     * @return {@link app.zoftwhere.bolt.deluge.DelugeBuilder}.
     * @since 11.0.0
     */
    public static DelugeBuilder from(DelugeProgramType type, DelugeSetting setting, DelugeData input) {
        return new DelugeBuilder(type, setting, input);
    }

    /**
     * DelugeBuilder with user defined default encoding, type, setting, and input.
     *
     * @param encoding user defined default encoding
     * @param type     deluge program type
     * @param setting  deluge program setting
     * @param input    deluge program data
     * @return {@link app.zoftwhere.bolt.deluge.DelugeBuilder}
     * @since 11.4.0
     */
    public static DelugeBuilder from(
        Charset encoding,
        DelugeProgramType type,
        DelugeSetting setting,
        DelugeData input
    )
    {
        final var withEncoding = setting != null ? DelugeSetting.withEncoding(setting, encoding) : null;
        return new DelugeBuilder(type, withEncoding, input);
    }

    /**
     * Return number of tests run.
     *
     * @param encodingList character encoding list.
     * @param settingList  deluge setting list.
     * @param inputList    program input data list.
     * @return number of tests run
     */
    public static int runTest(List<Charset> encodingList, List<DelugeSetting> settingList, List<DelugeData> inputList) {
        return new DelugeForge(encodingList, settingList, inputList).runTest();
    }

    /**
     * Return error message for comparison.
     *
     * @param expected expected program output.
     * @param actual   actual program output.
     * @return error message for comparison, null otherwise
     */
    @SuppressWarnings("WeakerAccess")
    public static String runComparison(DelugeProgramOutput expected, DelugeProgramOutput actual) {
        return DelugeControl.runComparison(expected, actual);
    }

    /**
     * Return instance with default settings.
     *
     * @return instance with default settings
     * @since 11.0.0
     */
    public static DelugeSetting forSetting() {
        return DelugeSetting.from();
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param charset program data character encoding
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.0.0
     */
    public static DelugeSetting forSetting(Charset charset) {
        return DelugeSetting.from(charset, false);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param argumentArray program argument array
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.4.0
     */
    public static DelugeSetting forSetting(String[] argumentArray) {
        return DelugeSetting.from(argumentArray);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param argumentArray program argument array
     * @param charset       program data character encoding
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.4.0
     */
    public static DelugeSetting forSetting(String[] argumentArray, Charset charset) {
        return DelugeSetting.from(argumentArray, charset);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param argumentArray program argument array
     * @param error         loading program data exception
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.4.0
     */
    public static DelugeSetting forSetting(String[] argumentArray, Exception error) {
        return DelugeSetting.from(argumentArray, error);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param argumentArray program argument array
     * @param error         loading program data exception
     * @param charset       program data character encoding
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.4.0
     */
    public static DelugeSetting forSetting(String[] argumentArray, Exception error, Charset charset) {
        return DelugeSetting.from(argumentArray, error, charset);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param error loading program data exception
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.4.0
     */
    public static DelugeSetting forSetting(Exception error) {
        return DelugeSetting.from(error);
    }

    /**
     * {@link app.zoftwhere.bolt.deluge.DelugeSetting} factory method.
     *
     * @param error   loading program data exception
     * @param charset program data character encoding
     * @return {@link app.zoftwhere.bolt.deluge.DelugeSetting} instance
     * @since 11.4.0
     */
    public static DelugeSetting forSetting(Exception error, Charset charset) {
        return DelugeSetting.from(error, charset);
    }

    /**
     * Return a list of {@link app.zoftwhere.bolt.deluge.DelugeSetting} for configurations.
     *
     * @param argumentList program argument list
     * @param errorList    program error list
     * @param charsetList  program character encoding list
     * @return {@link java.util.List} of {@link app.zoftwhere.bolt.deluge.DelugeSetting}
     */
    public static List<DelugeSetting> programSetting(
        List<String[]> argumentList,
        List<Exception> errorList,
        List<Charset> charsetList
    )
    {
        final var list = new ArrayList<DelugeSetting>();

        list.add(forSetting());

        for (final var argument : argumentList) {
            list.add(forSetting(argument));

            for (final var error : errorList) {
                list.add(forSetting(argument, error));

                for (final var charset : charsetList) {
                    list.add(forSetting(argument, error, charset));
                }
            }

            for (final var charset : charsetList) {
                list.add(forSetting(argument, charset));
            }
        }

        for (final var error : errorList) {
            list.add(forSetting(error));

            for (final var charset : charsetList) {
                list.add(forSetting(error, charset));
            }
        }

        for (final var charset : charsetList) {
            list.add(forSetting(charset));
        }

        return list;
    }

    public static DelugeData forStringArray(String[] input) {
        return DelugeData.forStringArray(input);
    }

    public static DelugeData forStringArray(String[] input, Charset charset) {
        return DelugeData.forStringArray(input, charset);
    }

    public static DelugeData forInputStream(String[] input, Charset charset, boolean withCharset) {
        return DelugeData.forInputStream(input, charset, withCharset);
    }

    public static DelugeData forInputStream(Exception e) {
        return DelugeData.forInputStream(e);
    }

    public static DelugeData forInputStream(Exception e, Charset charset) {
        return DelugeData.forInputStream(e, charset);
    }

    public static DelugeData forResource(String name, Class<?> withClass) {
        return DelugeData.forResource(name, withClass);
    }

    public static DelugeData forResource(String name, Class<?> withClass, Charset charset) {
        return DelugeData.forResource(name, withClass, charset);
    }

    private final DelugeProgramType type;

    private final DelugeSetting setting;

    private final DelugeData input;

    /**
     * Constructor for {@link app.zoftwhere.bolt.deluge.DelugeBuilder} (private).
     *
     * @param type    program type
     * @param setting deluge setting
     * @param input   program input data
     */
    private DelugeBuilder(DelugeProgramType type, DelugeSetting setting, DelugeData input) {
        this.type = type;
        this.setting = setting;
        this.input = input;
    }

    DelugeProgramOutput buildExpectedOutput() {
        return DelugeMock.from(type, setting, input).buildExpectedOutput();
    }

    public Charset outputCharset() {
        return setting.hasCharSet() ? setting.charset() : defaultEncoding();
    }

    public Charset inputCharset() {
        return input.hasCharset() ? input.charset() : defaultEncoding();
    }

    public Charset defaultEncoding() {
        return setting.defaultEncoding() != null ? setting.defaultEncoding() : Runner.DEFAULT_ENCODING;
    }

}
