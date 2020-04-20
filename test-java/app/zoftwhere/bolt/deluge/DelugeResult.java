package app.zoftwhere.bolt.deluge;

import java.time.Duration;

public class DelugeResult {

    public static DelugeResult from(String[] output, Duration duration, Exception error) {
        return new DelugeResult(output, duration, error);
    }

    private final String[] output;

    private final Exception error;

    private final Duration duration;

    DelugeResult(String[] output, Duration duration, Exception error) {
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
