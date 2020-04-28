package app.zoftwhere.bolt.deluge;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import app.zoftwhere.bolt.Runner;

public class DelugeBuilder {

    static DelugeBuilder from(DelugeProgramType type, DelugeSetting setting, DelugeData input) {
        return new DelugeBuilder(type, setting, input);
    }

    public static int runTest(List<Charset> encodingList, List<DelugeSetting> settingList, List<DelugeData> inputList) {
        return new DelugeForge(encodingList, settingList, inputList).runTest();
    }

    static String runComparison(DelugeProgramOutput expected, DelugeProgramOutput actual) {
        return DelugeControl.runComparison(expected, actual);
    }

    public static List<DelugeSetting> programSetting(
        List<String[]> argumentList,
        List<Exception> errorList,
        List<Charset> charsetList
    )
    {
        var list = new ArrayList<DelugeSetting>();

        list.add(forSetting());

        for (var argument : argumentList) {
            list.add(forSetting(argument));

            for (var error : errorList) {
                list.add(forSetting(argument, error));

                for (var charset : charsetList) {
                    list.add(forSetting(argument, error, charset));
                }
            }

            for (var charset : charsetList) {
                list.add(forSetting(argument, charset));
            }
        }

        for (var error : errorList) {
            list.add(forSetting(error));

            for (var charset : charsetList) {
                list.add(forSetting(error, charset));
            }
        }

        for (var charset : charsetList) {
            list.add(forSetting(charset, false));
        }

        return list;
    }

    public static DelugeSetting forSetting() {
        return DelugeSetting.from();
    }

    public static DelugeSetting forSetting(Charset charset, boolean isEncoding) {
        return DelugeSetting.from(charset, isEncoding);
    }

    @SuppressWarnings("unused")
    public static DelugeSetting forSetting(Charset encoding, String[] argument) {
        return DelugeSetting.from(encoding, argument);
    }

    @SuppressWarnings("unused")
    public static DelugeSetting forSetting(Charset encoding, String[] argument, Exception error) {
        return DelugeSetting.from(encoding, argument, error);
    }

    @SuppressWarnings("unused")
    public static DelugeSetting forSetting(Charset encoding, String[] argument, Charset charset) {
        return DelugeSetting.from(encoding, argument, charset);
    }

    @SuppressWarnings("unused")
    public static DelugeSetting forSetting(Charset encoding, String[] argument, Exception error, Charset charset) {
        return DelugeSetting.from(encoding, argument, error, charset);
    }

    @SuppressWarnings("unused")
    public static DelugeSetting forSetting(Charset encoding, Exception error) {
        return DelugeSetting.from(encoding, error);
    }

    @SuppressWarnings("unused")
    public static DelugeSetting forSetting(Charset encoding, Exception error, Charset charset) {
        return DelugeSetting.from(encoding, error, charset);
    }

    @SuppressWarnings("unused")
    public static DelugeSetting forSetting(Charset encoding, Charset charset) {
        return DelugeSetting.from(encoding, charset);
    }

    public static DelugeSetting forSetting(String[] argumentArray) {
        return DelugeSetting.from(argumentArray);
    }

    public static DelugeSetting forSetting(String[] argumentArray, Exception error) {
        return DelugeSetting.from(argumentArray, error);
    }

    public static DelugeSetting forSetting(String[] argumentArray, Exception error, Charset charset) {
        return DelugeSetting.from(argumentArray, error, charset);
    }

    public static DelugeSetting forSetting(String[] argumentArray, Charset charset) {
        return DelugeSetting.from(argumentArray, charset);
    }

    public static DelugeSetting forSetting(Exception error) {
        return DelugeSetting.from(error);
    }

    public static DelugeSetting forSetting(Exception error, Charset charset) {
        return DelugeSetting.from(error, charset);
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

    public DelugeBuilder() {
        this.type = null;
        this.setting = DelugeSetting.from();
        this.input = DelugeData.forStringArray(null);
    }

    private DelugeBuilder(DelugeProgramType type, DelugeSetting setting, DelugeData input) {
        this.type = type;
        this.setting = setting;
        this.input = input;
    }

    public DelugeBuilder withEncoding(Charset charset) {
        DelugeSetting setting = this.setting.updateEncoding(charset);
        return new DelugeBuilder(type, setting, input);
    }

    public DelugeBuilder withProgram(DelugeProgramType type, Exception error) {
        DelugeSetting setting = this.setting != null && this.setting.hasEncoding()
            ? DelugeSetting.from(this.setting.defaultEncoding(), error)
            : DelugeSetting.from(error);
        return new DelugeBuilder(type, setting, input);
    }

    public DelugeBuilder withProgram(DelugeProgramType type, Exception error, Charset charset) {
        if (type.isArgued()) {
            throw new DelugeException("type.argued");
        }

        DelugeSetting setting = this.setting != null && this.setting.hasEncoding()
            ? DelugeSetting.from(this.setting.defaultEncoding(), error, charset)
            : DelugeSetting.from(error, charset);
        return new DelugeBuilder(type, setting, input);
    }

    public DelugeBuilder withProgram(DelugeProgramType type, String[] arguments, Exception error) {
        if (!type.isArgued()) {
            throw new DelugeException("type.not.argued");
        }
        DelugeSetting setting = this.setting != null && this.setting.hasEncoding()
            ? DelugeSetting.from(this.setting.defaultEncoding(), arguments, error)
            : DelugeSetting.from(arguments, error);
        return new DelugeBuilder(type, setting, input);
    }

    public DelugeBuilder withProgram(DelugeProgramType type, String[] arguments, Exception error, Charset charset) {
        if (!type.isArgued()) {
            throw new DelugeException("type.not.argued");
        }

        DelugeSetting setting = this.setting != null && this.setting.hasEncoding()
            ? DelugeSetting.from(this.setting.defaultEncoding(), arguments, error, charset)
            : DelugeSetting.from(arguments, error, charset);
        return new DelugeBuilder(type, setting, input);
    }

    public DelugeBuilder withInput(DelugeData input) {
        return new DelugeBuilder(type, setting, input);
    }

    @SuppressWarnings("WeakerAccess")
    public Charset defaultEncoding() {
        return setting.defaultEncoding() != null ? setting.defaultEncoding() : Runner.DEFAULT_ENCODING;
    }

    public Charset outputCharset() {
        return setting.hasCharSet() ? setting.charset() : defaultEncoding();
    }

    public Charset inputCharset() {
        return input.hasCharset() ? input.charset() : defaultEncoding();
    }

    @SuppressWarnings("unused")
    public DelugeProgramType type() {
        return type;
    }

    @SuppressWarnings("unused")
    public DelugeSetting setting() {
        return setting;
    }

    public DelugeData input() {
        return input;
    }

    public DelugeProgramOutput buildExpectedOutput() {
        return DelugeMock.from(type, setting, input).buildExpectedOutput();
    }

}
