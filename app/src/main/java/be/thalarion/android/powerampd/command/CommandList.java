package be.thalarion.android.powerampd.command;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolListOK;
import be.thalarion.android.powerampd.protocol.ProtocolOK;
import be.thalarion.android.powerampd.protocol.Connection;

public class CommandList implements Executable {

    public enum MODE {
        LIST,
        LIST_OK
    }

    private final List<Command> list;
    private final MODE mode;

    public CommandList(MODE mode) {
        this.list = new ArrayList<Command>();
        this.mode = mode;
    }

    public void execute(Connection conn)
            throws ProtocolException {
        int i = 0;
        try {
            for (Command command : list) {
                command.execute(conn);

                i++;

                if (mode.equals(MODE.LIST_OK))
                    conn.send(new ProtocolListOK());
            }
            conn.send(new ProtocolOK());
        } catch (ProtocolException e) {
            // Set line on which the execution failed
            e.setLine(i);
            throw e;
        }
    }

    public void add(Command command) {
        list.add(command);
    }

}
