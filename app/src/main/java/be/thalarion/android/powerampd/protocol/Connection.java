package be.thalarion.android.powerampd.protocol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import be.thalarion.android.powerampd.PasswordEntry;
import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.Protocol;
import be.thalarion.android.powerampd.protocol.ProtocolException;

/**
 * Client-server connection
 */
public class Connection {

    private Context context;
    private Socket socket;

    private BufferedReader reader;
    private BufferedWriter writer;

    private long passwordEntryId;

    public Connection(Context context, Socket socket) {
        this.context = context;
        this.socket = socket;
        this.passwordEntryId = -1;

        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public boolean isAuthenticated() {
        return (passwordEntryId != -1);
    }

    public String readLine()
            throws IOException {
        return reader.readLine();
    }

    public void send(Protocol protocol) {
        try {
            writer.write(protocol.toString());
            writer.flush();
        } catch (IOException e) {
            // IOException thrown on command 'close'
        }
    }

    public void close() {
        Log.i("powerampd-client", "Closing client socket");
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            Thread.currentThread().interrupt();
            if (!socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate(String password)
            throws ProtocolException {
        List<PasswordEntry> entries = PasswordEntry.find(PasswordEntry.class, "password = ?", password);
        if (entries == null || entries.size() == 0)
            return false;

        // Only the first entry is accepted
        passwordEntryId = entries.get(0).getId();

        return true;
    }

    public boolean authorize(Permission permission) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // Check permissions only if it is enabled
        if (prefs.getBoolean("pref_auth_enabled", context.getString(R.string.pref_auth_enabled_default).equals("true"))) {
            if (permission == Permission.PERMISSION_NONE)
                return true;

            // Check default permissions
            if (!isAuthenticated()) {
                switch (permission) {
                    case PERMISSION_READ:
                        return prefs.getBoolean("pref_permission_read_default", context.getString(R.string.pref_permission_read_default).equals("true"));
                    case PERMISSION_ADD:
                        return prefs.getBoolean("pref_permission_add_default", context.getString(R.string.pref_permission_add_default).equals("true"));
                    case PERMISSION_CONTROL:
                        return prefs.getBoolean("pref_permission_control_default", context.getString(R.string.pref_permission_control_default).equals("true"));
                    case PERMISSION_ADMIN:
                        return prefs.getBoolean("pref_permission_admin_default", context.getString(R.string.pref_permission_admin_default).equals("true"));
                }
            } else {
                PasswordEntry entry = PasswordEntry.findById(PasswordEntry.class, passwordEntryId);
                if (entry == null)
                    return false;

                return entry.can(permission);
            }
        }
        return true;
    }

    public Context getContext() {
        return context;
    }
}
