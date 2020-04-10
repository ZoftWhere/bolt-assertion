package app.zoftwhere.bolt.deluge;

import java.nio.charset.Charset;

public class DelugeBuilder {

    private final DelugeData data;

    private final DelugeSettings settings;

    private final DelugeProgramType programType;

    public DelugeBuilder() {
        this.data = null;
        this.programType = null;
        this.settings = null;
    }

    private DelugeBuilder(DelugeProgramType programType, DelugeData data, DelugeSettings settings) {
        this.data = data;
        this.settings = settings;
        this.programType = programType;
    }

    public DelugeBuilder forProgram(DelugeProgramType programType) {
        return new DelugeBuilder(programType, this.data, this.settings);
    }

    public DelugeBuilder forStringArray(String[] input) {
        return new DelugeBuilder(programType, DelugeData.forStringArray(input), settings);
    }

    public DelugeBuilder forInputStream(String[] input) {
        return new DelugeBuilder(programType, DelugeData.forInputStream(input), settings);
    }

    public DelugeBuilder forInputStream(String[] input, Charset charset) {
        return new DelugeBuilder(programType, DelugeData.forInputStream(input, charset), settings);
    }

    public DelugeBuilder forResource(String name, Class<?> withClass, String[] input) {
        return new DelugeBuilder(programType, DelugeData.forResource(name, withClass, input), settings);
    }

    public DelugeBuilder forResource(String name, Class<?> withClass, String[] input, Charset charset) {
        return new DelugeBuilder(programType, DelugeData.forResource(name, withClass, input, charset), settings);
    }

    public DelugeBuilder withSettings(String[] argumentArray) {
        DelugeSettings settings = DelugeSettings.from(argumentArray);
        return new DelugeBuilder(this.programType, this.data, settings);
    }

    public DelugeBuilder withSettings(Throwable throwable) {
        DelugeSettings settings = DelugeSettings.from(throwable);
        return new DelugeBuilder(this.programType, this.data, settings);
    }

    public DelugeBuilder withSettings(String[] argumentArray, Throwable throwable) {
        DelugeSettings settings = DelugeSettings.from(argumentArray, throwable);
        return new DelugeBuilder(this.programType, this.data, settings);
    }

    public DelugeBuilder withSettings(String[] argumentArray, Charset charset) {
        DelugeSettings settings = DelugeSettings.from(argumentArray, charset);
        return new DelugeBuilder(this.programType, this.data, settings);
    }

    public DelugeBuilder withSettings(Throwable throwable, Charset charset) {
        DelugeSettings settings = DelugeSettings.from(throwable, charset);
        return new DelugeBuilder(this.programType, this.data, settings);
    }

    public DelugeBuilder withSettings(String[] argumentArray, Throwable throwable, Charset charset) {
        DelugeSettings settings = DelugeSettings.from(argumentArray, throwable, charset);
        return new DelugeBuilder(this.programType, this.data, settings);
    }

    public boolean hasProgramType() {
        return programType != null;
    }

    public boolean hasSettings() {
        return settings != null;
    }

    public boolean hasData() {
        return data != null;
    }

    public boolean hasArgumentArray() {
        return settings != null && settings.hasArgumentArray();
    }

    public void runTest() {
        DelugeControl.from(programType, settings, data).runTest();
    }

}