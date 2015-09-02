package be.thalarion.android.powerampd.protocol;

public class ProtocolMessage implements Protocol {

    private final String message;

    public ProtocolMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%s\n", this.message);
    }
}
