package be.thalarion.android.powerampd.command;

import android.content.Context;
import android.content.Intent;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.Protocol;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public class Handle {

    private Context context;
    private Socket socket;

    private BufferedReader reader;
    private BufferedWriter writer;

    // TODO: replace by PasswordEntry
    private List<Permission> permissions;

    public Handle(Context context, Socket socket) {
        this.context = context;
        this.socket = socket;

        this.permissions = new ArrayList<Permission>();

        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
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

    public List<Permission> authenticate(String password)
        throws ProtocolException {
        // TODO: replace stub
        permissions.add(Permission.PERMISSION_READ);
        permissions.add(Permission.PERMISSION_CONTROL);
        if (password.equals("password")) {
            return permissions;
        } else throw new ProtocolException(ProtocolException.ACK_ERROR_PASSWORD, "incorrect password");
    }

    public boolean authorize(Permission permission) {
        if (permission == Permission.PERMISSION_NONE)
            return true;

        for (int i = 0; i < permissions.size(); i++) {
            if (permissions.get(i).equals(permission))
                return true;
        }

        return false;
    }

    public void command(int action) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .setPackage(PowerampAPI.PACKAGE_NAME)
                .putExtra(PowerampAPI.COMMAND, action)
        );
    }
}
