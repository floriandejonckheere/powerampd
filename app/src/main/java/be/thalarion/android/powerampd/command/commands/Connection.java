package be.thalarion.android.powerampd.command.commands;

import java.util.List;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public class Connection {

    public static class Close extends Command {
        public Close(List<String> cmdline) throws ProtocolException { super(cmdline, Permission.PERMISSION_NONE, 0, 0); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            state.close();
            throw new ProtocolException.EmptyException();
        }
    }

    public static class Password extends Command {
        public Password(List<String> cmdline) throws ProtocolException { super(cmdline, Permission.PERMISSION_NONE, 1, 1); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            if (!state.authenticate(cmdline.get(1)))
                throw new ProtocolException(ProtocolException.ACK_ERROR_PASSWORD, cmdline.get(0),
                        state.context.getString(R.string.proto_error_password));
        }
    }

    public static class Ping extends Command {
        public Ping() throws ProtocolException { super(null, Permission.PERMISSION_NONE, 0, 0); }

        @Override
        public void executeCommand(State state) throws ProtocolException {}
    }
}
