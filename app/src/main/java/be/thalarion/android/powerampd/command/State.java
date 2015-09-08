package be.thalarion.android.powerampd.command;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    /**
     * Poweramp state
     */
    public static Intent trackIntent;
    public static Intent statusIntent;
    public static Intent playingModeIntent;

    private Context context;
    private Socket socket;

    private BufferedReader reader;
    private BufferedWriter writer;

    private PasswordEntry passwordEntry;

    public State(Context context, Socket socket) {
        this.context = context;
        this.socket = socket;

        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public boolean isAuthenticated() {
        return (passwordEntry != null);
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
        try {
            reader.close();
            writer.close();
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
        passwordEntry = entries.get(0);

        return true;
    }

    public boolean authorize(Permission permission) {
        if (permission == Permission.PERMISSION_NONE)
            return true;

        if (!isAuthenticated())
            return false;

        return passwordEntry.can(permission);
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
