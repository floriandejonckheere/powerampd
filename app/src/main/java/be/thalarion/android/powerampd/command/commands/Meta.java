package be.thalarion.android.powerampd.command.commands;

import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolMessage;

public class Meta {

    public static class Debug extends Command {
        public Debug() throws ProtocolException { super(null, Permission.PERMISSION_ADMIN, 0, 0); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            state.send(new ProtocolMessage(String.format("authenticated: %s", state.isAuthenticated())));
            state.send(new ProtocolMessage(String.format("auth_enabled: %s", state.getPreferences().getBoolean("pref_auth_enabled", true))));
            state.send(new ProtocolMessage(String.format("can_none: %s", state.authorize(Permission.PERMISSION_NONE))));
            state.send(new ProtocolMessage(String.format("can_read: %s", state.authorize(Permission.PERMISSION_READ))));
            state.send(new ProtocolMessage(String.format("can_add: %s", state.authorize(Permission.PERMISSION_ADD))));
            state.send(new ProtocolMessage(String.format("can_control: %s", state.authorize(Permission.PERMISSION_CONTROL))));
            state.send(new ProtocolMessage(String.format("can_admin: %s", state.authorize(Permission.PERMISSION_ADMIN))));
        }
    }
}
