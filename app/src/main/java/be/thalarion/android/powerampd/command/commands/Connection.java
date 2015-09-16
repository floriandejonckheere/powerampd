package be.thalarion.android.powerampd.command.commands;

import java.util.List;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public class Connection {

    public static class Close extends Command {
        public Close() { super(null, Permission.PERMISSION_NONE); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            state.close();
        }
    }

    /**
     * Null - empty response
     */
    public static class Null extends Command {
        public Null() { super(null, Permission.PERMISSION_NONE); }

        @Override
        public void executeCommand(State state) throws ProtocolException {}
    }

    public static class Password extends Command {
        public Password(List<String> cmdline) { super(cmdline, Permission.PERMISSION_NONE); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            checkArguments(1, 1);
            if (!state.authenticate(cmdline.get(1)))
                throw new ProtocolException(ProtocolException.ACK_ERROR_PASSWORD, cmdline.get(0),
                        state.context.getString(R.string.proto_error_password));
        }
    }

    public static class Ping extends Command {
        public Ping(List<String> cmdline) { super(cmdline, Permission.PERMISSION_NONE); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            checkArguments(0, 0);
        }
    }
}
