package app.zoftwhere.bolt;

import java.util.Comparator;

import app.zoftwhere.bolt.api.RunnerPreTest;

/**
 * Bolt pre-test class.
 *
 * @since 6.0.0
 */
class BoltPreTest extends BoltCommonOutput implements RunnerPreTest {

    BoltPreTest(String[] found, Exception exception, Comparator<String> comparator) {
        super(found, exception, comparator);
    }

}
