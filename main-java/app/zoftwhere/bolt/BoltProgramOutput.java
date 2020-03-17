package app.zoftwhere.bolt;

import java.util.Comparator;

import app.zoftwhere.bolt.api.RunnerPreTest;
import app.zoftwhere.bolt.api.RunnerProgramOutput;

/**
 * Bolt program output class.
 *
 * @since 6.0.0
 */
class BoltProgramOutput extends BoltPreTest implements RunnerProgramOutput {

    private final String[] output;

    private final Exception exception;

    BoltProgramOutput(String[] output, Exception exception) {
        super(output, exception, null);
        this.output = output;
        this.exception = exception;
    }

    @Override
    public RunnerPreTest comparator(Comparator<String> comparator) {
        return new BoltPreTest(output, exception, comparator);
    }

}
