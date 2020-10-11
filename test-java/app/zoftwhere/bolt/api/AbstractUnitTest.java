package app.zoftwhere.bolt.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>Abstract Unit Test.
 * </p>
 * <p>This class is package-private as it is for testing purposes only.
 * </p>
 *
 * @author Osmund
 * @since 6.0.0
 */
class AbstractUnitTest {

    @Test
    void forCodeCoverage() {
        final var unit = new AbstractUnit() { };
        assertNotNull(unit);
    }

}
