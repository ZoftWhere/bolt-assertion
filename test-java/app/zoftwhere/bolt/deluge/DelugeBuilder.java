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

    public DelugeBuilder forInputStream(Exception e) {
        return new DelugeBuilder(programType, DelugeData.forInputStream(e), settings);
    }

    public DelugeBuilder forInputStream(Exception e, Charset charset) {
        return new DelugeBuilder(programType, DelugeData.forInputStream(e, charset), settings);
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

    public DelugeBuilder withSettings(Exception error) {
        DelugeSettings settings = DelugeSettings.from(error);
        return new DelugeBuilder(this.programType, this.data, settings);
    }

    public DelugeBuilder withSettings(String[] argumentArray, Exception error) {
        DelugeSettings settings = DelugeSettings.from(argumentArray, error);
        return new DelugeBuilder(this.programType, this.data, settings);
    }

    public DelugeBuilder withSettings(String[] argumentArray, Charset charset) {
        DelugeSettings settings = DelugeSettings.from(argumentArray, charset);
        return new DelugeBuilder(this.programType, this.data, settings);
    }

    public DelugeBuilder withSettings(Exception error, Charset charset) {
        DelugeSettings settings = DelugeSettings.from(error, charset);
        return new DelugeBuilder(this.programType, this.data, settings);
    }

    public DelugeBuilder withSettings(String[] argumentArray, Exception error, Charset charset) {
        DelugeSettings settings = DelugeSettings.from(argumentArray, error, charset);
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
