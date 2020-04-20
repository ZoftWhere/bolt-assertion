package app.zoftwhere.bolt.deluge;

import java.nio.charset.Charset;
import java.util.Scanner;

import app.zoftwhere.bolt.Runner;

@SuppressWarnings("WeakerAccess")
public class DelugeBuilder {

    public static DelugeBuilder from(DelugeProgramType type, DelugeSettings setting, DelugeData input) {
        return new DelugeBuilder(type, setting, input);
    }

    public static void runTest(DelugeProgramType type, DelugeSettings setting, DelugeData input) {
        DelugeControl.runTest(type, setting, input);
    }

    public static String runComparison(DelugeResult expected, DelugeResult actual) {
        return DelugeControl.runComparison(expected, actual);
    }

    public static DelugeSettings forSetting() {
        return DelugeSettings.from();
    }

    public static DelugeSettings forSetting(Charset charset, boolean isEncoding) {
        return DelugeSettings.from(charset, isEncoding);
    }

    public static DelugeSettings forSetting(Charset encoding, String[] argument) {
        return DelugeSettings.from(encoding, argument);
    }

    public static DelugeSettings forSetting(Charset encoding, String[] argument, Exception error) {
        return DelugeSettings.from(encoding, argument, error);
    }

    public static DelugeSettings forSetting(Charset encoding, String[] argument, Charset charset) {
        return DelugeSettings.from(encoding, argument, charset);
    }

    public static DelugeSettings forSetting(Charset encoding, String[] argument, Exception error, Charset charset) {
        return DelugeSettings.from(encoding, argument, error, charset);
    }

    public static DelugeSettings forSetting(Charset encoding, Exception error) {
        return DelugeSettings.from(encoding, error);
    }

    public static DelugeSettings forSetting(Charset encoding, Exception error, Charset charset) {
        return DelugeSettings.from(encoding, error, charset);
    }

    public static DelugeSettings forSetting(Charset encoding, Charset charset) {
        return DelugeSettings.from(encoding, charset);
    }

    public static DelugeSettings forSetting(String[] argumentArray) {
        return DelugeSettings.from(argumentArray);
    }

    public static DelugeSettings forSetting(String[] argumentArray, Exception error) {
        return DelugeSettings.from(argumentArray, error);
    }

    public static DelugeSettings forSetting(String[] argumentArray, Exception error, Charset charset) {
        return DelugeSettings.from(argumentArray, error, charset);
    }

    public static DelugeSettings forSetting(String[] argumentArray, Charset charset) {
        return DelugeSettings.from(argumentArray, charset);
    }

    public static DelugeSettings forSetting(Exception error) {
        return DelugeSettings.from(error);
    }

    public static DelugeSettings forSetting(Exception error, Charset charset) {
        return DelugeSettings.from(error, charset);
    }

    public static DelugeData forStringArray(String[] input) {
        return DelugeData.forStringArray(input);
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

    public static DelugeLineScanner newLineScanner(Scanner scanner) {
        return new DelugeLineScanner(scanner);
    }

    private final DelugeProgramType type;

    private final DelugeSettings setting;

    private final DelugeData input;

    public DelugeBuilder() {
        this.type = null;
        this.setting = DelugeSettings.from();
        this.input = DelugeData.forStringArray(null);
    }

    private DelugeBuilder(DelugeProgramType type, DelugeSettings setting, DelugeData input) {
        this.type = type;
        this.setting = setting;
        this.input = input;
    }

    public DelugeBuilder withEncoding(Charset charset) {
        DelugeSettings setting = DelugeSettings.from(charset, true);
        return new DelugeBuilder(type, setting, input);
    }

    public DelugeBuilder withProgram(DelugeProgramType type, Exception error) {
        DelugeSettings setting = this.setting != null && this.setting.hasEncoding()
            ? DelugeSettings.from(this.setting.defaultEncoding(), error)
            : DelugeSettings.from(error);
        return new DelugeBuilder(type, setting, input);
    }

    public DelugeBuilder withProgram(DelugeProgramType type, Exception error, Charset charset) {
        if (type.isArgued()) {
            throw new DelugeException("type.argued");
        }

        DelugeSettings setting = this.setting != null && this.setting.hasEncoding()
            ? DelugeSettings.from(this.setting.defaultEncoding(), error, charset)
            : DelugeSettings.from(error, charset);
        return new DelugeBuilder(type, setting, input);
    }

    public DelugeBuilder withProgram(DelugeProgramType type, String[] arguments, Exception error) {
        if (!type.isArgued()) {
            throw new DelugeException("type.not.argued");
        }
        DelugeSettings setting = this.setting != null && this.setting.hasEncoding()
            ? DelugeSettings.from(this.setting.defaultEncoding(), arguments, error)
            : DelugeSettings.from(arguments, error);
        return new DelugeBuilder(type, setting, input);
    }

    public DelugeBuilder withProgram(DelugeProgramType type, String[] strings, Exception error, Charset charset) {
        if (!type.isArgued()) {
            throw new DelugeException("type.not.argued");
        }

        DelugeSettings setting = this.setting != null && this.setting.hasEncoding()
            ? DelugeSettings.from(this.setting.defaultEncoding(), strings, error, charset)
            : DelugeSettings.from(strings, error, charset);
        return new DelugeBuilder(type, setting, input);
    }

    public DelugeBuilder withInput(DelugeData input) {
        return new DelugeBuilder(type, setting, input);
    }

    public Charset defaultEncoding() {
        return setting.defaultEncoding() != null ? setting.defaultEncoding() : Runner.DEFAULT_ENCODING;
    }

    public Charset outputCharset() {
        return setting.hasCharSet() ? setting.charset() : defaultEncoding();
    }

    public Charset inputCharset() {
        return input.hasCharset() ? input.charset() : defaultEncoding();
    }

    public DelugeProgramType type() {
        return type;
    }

    public DelugeSettings setting() {
        return setting;
    }

    public DelugeData input() {
        return input;
    }

    public DelugeResult buildExpectedOutput() {
        return DelugeMock.from(type, setting, input).buildExpectedOutput();
    }

}
