package be.thalarion.android.powerampd.command.commands;

import java.util.List;

import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolMessage;
import be.thalarion.android.powerampd.state.DatabaseState;

public class Database {

    public static class Update extends Command {
        public Update(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 1);
            if (cmdline.size() == 1) {
                // Update everything
                state.send(new ProtocolMessage(String.format("updating_db: %d", DatabaseState.update(state.getContext()))));
            } else {
                // Update URI
                throw new ProtocolException(ProtocolException.ACK_ERROR_SYSTEM, "not implemented yet");
            }
        }
    }

    public static class Rescan extends Command {
        public Rescan(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 1);
            if (cmdline.size() == 1) {
                // Rescan everything
                state.send(new ProtocolMessage(String.format("updating_db: %d", DatabaseState.rescan(state.getContext()))));
            } else {
                // Rescan URI
                throw new ProtocolException(ProtocolException.ACK_ERROR_SYSTEM, "not implemented yet");
            }
        }
    }

}
