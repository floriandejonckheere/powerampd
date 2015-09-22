package be.thalarion.android.powerampd.command.commands;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.protocol.Connection;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolMessage;

public class Meta {

    public static class Debug extends Command {
        public Debug() { super(null, Permission.PERMISSION_ADMIN); }

        @Override
        public void executeCommand(Connection conn) throws ProtocolException {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(conn.getContext());
            conn.send(new ProtocolMessage(String.format("authenticated: %s", conn.isAuthenticated())));
            conn.send(new ProtocolMessage(String.format("auth_enabled: %s", prefs.getBoolean("pref_auth_enabled", conn.getContext().getString(R.string.pref_auth_enabled_default).equals("true")))));
            conn.send(new ProtocolMessage(String.format("can_none: %s", conn.authorize(Permission.PERMISSION_NONE))));
            conn.send(new ProtocolMessage(String.format("can_read: %s", conn.authorize(Permission.PERMISSION_READ))));
            conn.send(new ProtocolMessage(String.format("can_add: %s", conn.authorize(Permission.PERMISSION_ADD))));
            conn.send(new ProtocolMessage(String.format("can_control: %s", conn.authorize(Permission.PERMISSION_CONTROL))));
            conn.send(new ProtocolMessage(String.format("can_admin: %s", conn.authorize(Permission.PERMISSION_ADMIN))));
        }
    }

    public static class DelayedException extends Command {
        private final ProtocolException exception;

        public DelayedException(ProtocolException exception) {
            super(null, Permission.PERMISSION_NONE);
            this.exception = exception;
        }

        @Override
        public void executeCommand(Connection conn) throws ProtocolException {
            throw exception;
        }
    }
}
