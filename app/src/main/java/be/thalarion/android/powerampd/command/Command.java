package be.thalarion.android.powerampd.command;

import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.service.State;

public interface Command {

    public void execute(State state)
            throws ProtocolException;

}
