package be.thalarion.android.powerampd.command;

import be.thalarion.android.powerampd.protocol.ProtocolException;

public interface Executable {

    void execute(State state)
            throws ProtocolException;

}
