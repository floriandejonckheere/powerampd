package be.thalarion.android.powerampd.command;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolListOK;

public class CommandList implements Executable {

    private final List<Command> list;
    private final boolean listOk;

    /**
     * CommandList - list of commands
     * @param listOk whether to send a 'list_OK' after executing every command
     */
    public CommandList(boolean listOk) {
        this.list = new ArrayList<Command>();
        this.listOk = listOk;
    }

    public void execute(State state)
            throws ProtocolException {
        for (Command command : list) {
            command.execute(state);
            if (listOk)
                state.send(new ProtocolListOK());
        }
    }

    public void add(Command command) {
        list.add(command);
    }

}
