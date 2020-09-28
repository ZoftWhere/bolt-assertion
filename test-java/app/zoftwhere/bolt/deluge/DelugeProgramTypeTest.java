package app.zoftwhere.bolt.deluge;

import org.junit.jupiter.api.Test;

class DelugeProgramTypeTest {

    @Test
    void testProgramTypeCompleteness() {
        for (var programType : DelugeProgramType.values()) {
            if (programType.isProgramFirst() && programType.isInputFirst()) {
                throw new DelugeException("deluge.program.type.occlusion");
            }

            if (!programType.isProgramFirst() && !programType.isInputFirst()) {
                throw new DelugeException("deluge.program.type.exclusion");
            }

            if (programType.isStandard() && programType.isConsole()) {
                throw new DelugeException("deluge.program.type.occlusion");
            }

            if (!programType.isStandard() && !programType.isConsole()) {
                throw new DelugeException("deluge.program.type.exclusion");
            }
        }
    }

}