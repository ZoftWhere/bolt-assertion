package app.zoftwhere.bolt.deluge;

import java.time.Duration;

import app.zoftwhere.bolt.api.RunnerProgramOutput;

public class DelugeProgramOutput {

    public static DelugeProgramOutput from(RunnerProgramOutput programOutput) {
        var output = programOutput.output();
        var duration = programOutput.executionDuration();
        var error = programOutput.error().orElse(null);
        return new DelugeProgramOutput(output, duration, error);
    }

    static DelugeProgramOutput from(String[] output, Duration duration, Exception error) {
        return new DelugeProgramOutput(output, duration, error);
    }

    private final String[] output;

    private final Exception error;

    private final Duration duration;

    private DelugeProgramOutput(String[] output, Duration duration, Exception error) {
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
