package app.zoftwhere.bolt.deluge;

import app.zoftwhere.bolt.api.RunnerProgramOutput;
import java.time.Duration;

class DelugeProgramOutput {

  private final String[] output;

  private final Exception error;

  private final Duration duration;

  private DelugeProgramOutput(String[] output, Duration duration, Exception error) {
    this.output = output;
    this.duration = duration;
    this.error = error;
  }

  static DelugeProgramOutput from(RunnerProgramOutput programOutput) {
    final var output = programOutput.output();
    final var duration = programOutput.executionDuration();
    final var error = programOutput.error().orElse(null);
    return new DelugeProgramOutput(output, duration, error);
  }

  static DelugeProgramOutput from(String[] output, Duration duration, Exception error) {
    return new DelugeProgramOutput(output, duration, error);
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
