package app.zoftwhere.bolt.deluge;

import static app.zoftwhere.bolt.BoltTestHelper.objectInArray;

public enum DelugeProgramType {
    INPUT_STANDARD,
    INPUT_STANDARD_ARGUED,
    INPUT_CONSOLE,
    INPUT_CONSOLE_ARGUED,
    PROGRAM_STANDARD,
    PROGRAM_STANDARD_ARGUED,
    PROGRAM_CONSOLE,
    PROGRAM_CONSOLE_ARGUED;

    public boolean isArgued() {
        DelugeProgramType[] array = {
            INPUT_STANDARD_ARGUED, INPUT_CONSOLE_ARGUED, PROGRAM_STANDARD_ARGUED, PROGRAM_CONSOLE_ARGUED
        };
        return objectInArray(this, array);
    }

    public boolean isProgramFirst() {
        DelugeProgramType[] array = {
            PROGRAM_STANDARD, PROGRAM_CONSOLE, PROGRAM_STANDARD_ARGUED, PROGRAM_CONSOLE_ARGUED
        };
        return objectInArray(this, array);
    }

    public boolean isInputFirst() {
        DelugeProgramType[] array = {
            INPUT_STANDARD, INPUT_STANDARD_ARGUED, INPUT_CONSOLE, INPUT_CONSOLE_ARGUED
        };
        return objectInArray(this, array);
    }

}
