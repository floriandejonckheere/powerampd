package be.thalarion.android.powerampd;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import be.thalarion.android.powerampd.command.Handle;
import be.thalarion.android.powerampd.command.SpecificCommand;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolOK;

/**
 * ClientThread - handles information flow from and to a client
 */
public class ClientThread implements Runnable {

    private Handle handle;

    public ClientThread(Context context, Socket socket) {
        this.handle = new Handle(context, socket);
    }

    @Override
    public void run() {
        try {
            // MPD protocol version
            handle.send(new ProtocolOK("MPD 0.19.0"));

            while (!Thread.currentThread().isInterrupted()) {
                String line = handle.readLine();
                if (line == null || line.length() == 0) {
                    Log.i("powerampd", "Empty line");
                    handle.send(new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, "No command given"));
                    break;
                } else try {
                    List<String> cmdline = Parser.tokenize(line);
                    SpecificCommand specificCommand = Parser.parse(cmdline);
                    specificCommand.execute(handle);
                } catch (ProtocolException e) {
                    // Unknown, malformed or not implemented command
                    handle.send(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        handle.close();
    }

}
