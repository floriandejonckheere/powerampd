package be.thalarion.android.powerampd.command.commands;

import java.util.List;

import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.protocol.Connection;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolMessage;

public class Database {

    public static class Update extends Command {
        public Update(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(Connection conn)
                throws ProtocolException {
            checkArguments(0, 1);
            if (cmdline.size() == 1) {
                // Update everything
                conn.send(new ProtocolMessage(String.format("updating_db: %d", be.thalarion.android.powerampd.state.Database.update(conn.getContext()))));
            } else {
                // Update URI
                throw new ProtocolException(ProtocolException.ACK_ERROR_SYSTEM, "not implemented yet");
            }
        }
    }

    public static class Rescan extends Command {
        public Rescan(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(Connection conn)
                throws ProtocolException {
            checkArguments(0, 1);
            if (cmdline.size() == 1) {
                // Rescan everything
                conn.send(new ProtocolMessage(String.format("updating_db: %d", be.thalarion.android.powerampd.state.Database.rescan(conn.getContext()))));
            } else {
                // Rescan URI
                throw new ProtocolException(ProtocolException.ACK_ERROR_SYSTEM, "not implemented yet");
            }
        }
    }

}
