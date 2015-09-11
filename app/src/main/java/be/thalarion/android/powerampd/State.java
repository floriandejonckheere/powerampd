package be.thalarion.android.powerampd;

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
import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.PasswordEntry;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.Protocol;
import be.thalarion.android.powerampd.protocol.ProtocolException;

/**
 * Application state of client-server communication
 */
public class State {

    protected Context context;
    private Socket socket;

    private BufferedReader reader;
    private BufferedWriter writer;

    private long passwordEntryId;

    public State(Context context, Socket socket) {
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
            e.printStackTrace();
            close();
        }
    }

    public void close() {
        Log.i("powerampd-client", "Closing client socket");
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            Thread.currentThread().interrupt();
            socket.close();
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
        // Check permissions only if it is enabled
        if (getPreferences().getBoolean("pref_auth_enabled", true)) {
            if (permission == Permission.PERMISSION_NONE)
                return true;

            // Check default permissions
            if (!isAuthenticated()) {
                switch (permission) {
                    case PERMISSION_READ:
                        return getPreferences().getBoolean("pref_permission_read_default", false);
                    case PERMISSION_ADD:
                        return getPreferences().getBoolean("pref_permission_add_default", false);
                    case PERMISSION_CONTROL:
                        return getPreferences().getBoolean("pref_permission_control_default", false);
                    case PERMISSION_ADMIN:
                        return getPreferences().getBoolean("pref_permission_admin_default", false);
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

    public void command(int action) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .setPackage(PowerampAPI.PACKAGE_NAME)
                .putExtra(PowerampAPI.COMMAND, action)
        );
    }

    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
