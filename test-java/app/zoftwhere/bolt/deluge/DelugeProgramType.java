package app.zoftwhere.bolt.deluge;

public enum DelugeProgramType {

    INPUT_STANDARD,
    INPUT_STANDARD_ARGUED,
    INPUT_CONSOLE,
    INPUT_CONSOLE_ARGUED,
    PROGRAM_STANDARD,
    PROGRAM_STANDARD_ARGUED,
    PROGRAM_CONSOLE,
    PROGRAM_CONSOLE_ARGUED;

    DelugeProgramType() {
    }

    public boolean isArgued() {
        return this == INPUT_STANDARD_ARGUED ||
            this == INPUT_CONSOLE_ARGUED ||
            this == PROGRAM_STANDARD_ARGUED ||
            this == PROGRAM_CONSOLE_ARGUED;
    }

    public boolean isProgramFirst() {
        return this == PROGRAM_STANDARD ||
            this == PROGRAM_CONSOLE ||
            this == PROGRAM_STANDARD_ARGUED ||
            this == PROGRAM_CONSOLE_ARGUED;
    }

    public boolean isInputFirst() {
        return this == INPUT_STANDARD ||
            this == INPUT_STANDARD_ARGUED ||
            this == INPUT_CONSOLE ||
            this == INPUT_CONSOLE_ARGUED;
    }

    public boolean isStandard() {
        return this == INPUT_STANDARD ||
            this == INPUT_STANDARD_ARGUED ||
            this == PROGRAM_STANDARD ||
            this == PROGRAM_STANDARD_ARGUED;
    }

    public boolean isConsole() {
        return this == INPUT_CONSOLE ||
            this == INPUT_CONSOLE_ARGUED ||
            this == PROGRAM_CONSOLE ||
            this == PROGRAM_CONSOLE_ARGUED;
    }

}
