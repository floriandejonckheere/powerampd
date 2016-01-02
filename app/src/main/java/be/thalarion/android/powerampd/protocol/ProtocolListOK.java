package be.thalarion.android.powerampd.protocol;

public class ProtocolListOK implements Protocol {
    @Override
    public String toString() {
        return String.format("%s\n", getClass().getResource("proto_list_ok"));
    }
}
