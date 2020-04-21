package app.zoftwhere.bolt.deluge;

import java.time.Duration;

import app.zoftwhere.bolt.api.RunnerProgramOutput;

public class DelugeProgramOutput {

    @SuppressWarnings("WeakerAccess")
    public static DelugeProgramOutput from(String[] output, Duration duration, Exception error) {
        return new DelugeProgramOutput(output, duration, error);
    }

    public static DelugeProgramOutput from(RunnerProgramOutput programOutput) {
        String[] output = programOutput.output();
        Duration duration = programOutput.executionDuration();
        Exception error = programOutput.error().orElse(null);
        return new DelugeProgramOutput(output, duration, error);
    }

    private final String[] output;

    private final Exception error;

    private final Duration duration;

    DelugeProgramOutput(String[] output, Duration duration, Exception error) {
        this.output = output;
        this.duration = duration;
        this.error = error;
    }

    String[] output() {
        return output;
    }

    Exception error() {
        return error;
    }

    Duration executionDuration() {
        return duration;
    }

}
