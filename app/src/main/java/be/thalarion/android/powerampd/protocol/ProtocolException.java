package be.thalarion.android.powerampd.protocol;

public class ProtocolException extends Exception implements Protocol {

    public static final int ACK_ERROR_NOT_LIST  = 1;
    public static final int ACK_ERROR_ARG       = 2;
    public static final int ACK_ERROR_PASSWORD = 3;
    public static final int ACK_ERROR_PERMISSION = 4;
    public static final int ACK_ERROR_UNKNOWN = 5;

    public static final int ACK_ERROR_NO_EXIST = 50;
    public static final int ACK_ERROR_PLAYLIST_MAX = 51;
    public static final int ACK_ERROR_SYSTEM = 52;
    public static final int ACK_ERROR_PLAYLIST_LOAD = 53;
    public static final int ACK_ERROR_UPDATE_ALREADY = 54;
    public static final int ACK_ERROR_PLAYER_SYNC = 55;
    public static final int ACK_ERROR_EXIST = 56;


    private final int error;
    private final int line;
    private final String command;
    private final String message;

    public ProtocolException(int error, int line, String command, String message) {
        this.error = error;
        this.line = line;
        this.command = command;
        this.message = message;
    }

    public ProtocolException(int error, String command, String message) {
        this(error, 0, command, message);
    }

    public ProtocolException(int error, int line, String message) {
        this(error, line, "", message);
    }

    public ProtocolException(int error, String message) {
        this(error, 0, "", message);
    }

    @Override
    public String toString() {
        return String.format("ACK [%d@%d] {%s} %s\n", this.error, this.line, this.command, this.message);
    }
}
