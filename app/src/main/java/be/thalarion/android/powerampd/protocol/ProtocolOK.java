package be.thalarion.android.powerampd.protocol;

public class ProtocolOK implements Protocol {

    private final String message;

    public ProtocolOK(String message) {
        this.message = message;
    }

    public ProtocolOK() {
        this(null);
    }

    @Override
    public String toString() {
        if (this.message == null) {
            return "OK\n";
        } else return String.format("OK %s\n", this.message);
    }
}
