package be.thalarion.android.powerampd.command;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.ClientThread;

public class CommandList implements Command {

    private List<Command> list;

    public CommandList() {
        this.list = new ArrayList<Command>();
    }

    public void execute(ClientThread.Handle handle)
        throws ProtocolException {
        for (Command command: list) {
            command.execute(handle);
        }
    }

}
