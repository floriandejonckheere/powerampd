package be.thalarion.android.powerampd.protocol;

public class ProtocolAcknowledgement implements Protocol {

    private final String message;

    public ProtocolAcknowledgement(String message) {
        this.message = message;
    }

    public ProtocolAcknowledgement() {
        this(null);
    }

    @Override
    public String toString() {
        if(this.message == null) {
            return "OK\n";
        } else return String.format("OK %s\n", this.message);
    }
}
