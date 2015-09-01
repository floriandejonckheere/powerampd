package be.thalarion.android.powerampd.protocol;

public class ProtocolError implements Protocol {

    public static final int UNKNOWN_COMMAND = 5;

    private final int error;
    private final int line;
    private final String command;
    private final String message;

    public ProtocolError(int error, int line, String command, String message) {
        this.error = error;
        this.line = line;
        this.command = command;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("ACK [%d@%d] {} %s\n", error, line, message);
    }

}
