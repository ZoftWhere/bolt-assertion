package app.zoftwhere.bolt.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AbstractUnitTest {

    @Test
    void forCodeCoverage() {
        final var unit = new AbstractUnit() { };
        assertNotNull(unit);
    }

}
