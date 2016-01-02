package be.thalarion.android.powerampd.protocol;

import be.thalarion.android.powerampd.R;

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
            return String.format("%s\n", getClass().getResource("proto_ok"));
        } else return String.format("%s %s\n", getClass().getResource("proto_ok"), this.message);
    }
}
