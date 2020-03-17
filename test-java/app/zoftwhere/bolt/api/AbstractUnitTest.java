package app.zoftwhere.bolt.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractUnitTest {

    @Test
    void forCodeCoverage() {
        var unit = new AbstractUnit(){};
        assertNotNull(unit);
    }

}