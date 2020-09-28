package app.zoftwhere.bolt.deluge;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import app.zoftwhere.bolt.Runner;

public class DelugeBuilder {

    @SuppressWarnings("WeakerAccess")
    public static DelugeBuilder from(DelugeProgramType type, DelugeSetting setting, DelugeData input) {
        return new DelugeBuilder(type, setting, input);
    }

    @SuppressWarnings("WeakerAccess")
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

    public static int runTest(List<Charset> encodingList, List<DelugeSetting> settingList, List<DelugeData> inputList) {
        return new DelugeForge(encodingList, settingList, inputList).runTest();
    }

    @SuppressWarnings("WeakerAccess")
    public static String runComparison(DelugeProgramOutput expected, DelugeProgramOutput actual) {
        return DelugeControl.runComparison(expected, actual);
    }

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

    private DelugeBuilder(DelugeProgramType type, DelugeSetting setting, DelugeData input) {
        this.type = type;
        this.setting = setting;
        this.input = input;
    }

    DelugeProgramOutput buildExpectedOutput() {
        return DelugeMock.from(type, setting, input).buildExpectedOutput();
    }

    Charset outputCharset() {
        return setting.hasCharSet() ? setting.charset() : defaultEncoding();
    }

    Charset inputCharset() {
        return input.hasCharset() ? input.charset() : defaultEncoding();
    }

    private Charset defaultEncoding() {
        return setting.defaultEncoding() != null ? setting.defaultEncoding() : Runner.DEFAULT_ENCODING;
    }

    private static DelugeSetting forSetting() {
        return DelugeSetting.from();
    }

    private static DelugeSetting forSetting(Charset charset) {
        return DelugeSetting.from(charset, false);
    }

    private static DelugeSetting forSetting(String[] argumentArray) {
        return DelugeSetting.from(argumentArray);
    }

    private static DelugeSetting forSetting(String[] argumentArray, Exception error) {
        return DelugeSetting.from(argumentArray, error);
    }

    private static DelugeSetting forSetting(String[] argumentArray, Exception error, Charset charset) {
        return DelugeSetting.from(argumentArray, error, charset);
    }

    private static DelugeSetting forSetting(String[] argumentArray, Charset charset) {
        return DelugeSetting.from(argumentArray, charset);
    }

    private static DelugeSetting forSetting(Exception error) {
        return DelugeSetting.from(error);
    }

    private static DelugeSetting forSetting(Exception error, Charset charset) {
        return DelugeSetting.from(error, charset);
    }

}
