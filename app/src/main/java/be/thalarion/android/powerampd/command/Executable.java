package be.thalarion.android.powerampd.command;

import be.thalarion.android.powerampd.protocol.Connection;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public interface Executable {

    void execute(Connection conn)
            throws ProtocolException;

}
