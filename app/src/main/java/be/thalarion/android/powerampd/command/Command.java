package be.thalarion.android.powerampd.command;

import be.thalarion.android.powerampd.service.State;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public interface Command {

    public void execute(State state)
            throws ProtocolException;

}
