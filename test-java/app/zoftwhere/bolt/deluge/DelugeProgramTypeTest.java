package app.zoftwhere.bolt.deluge;

import org.junit.jupiter.api.Test;

class DelugeProgramTypeTest {

    @Test
    void testProgramTypeCompleteness() {
        for (DelugeProgramType programType : DelugeProgramType.values()) {
            if (programType.isProgramFirst() && programType.isInputFirst()) {
                throw new DelugeException("deluge.program.type.occlusion");
            }
            if (!programType.isProgramFirst() && !programType.isInputFirst()) {
                throw new DelugeException("deluge.program.type.exclusion");
            }
        }
    }

}