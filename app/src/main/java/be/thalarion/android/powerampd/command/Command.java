package be.thalarion.android.powerampd.command;

import be.thalarion.android.powerampd.ClientThread;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public interface Command {

    public void execute(Handle handle)
            throws ProtocolException;

}
