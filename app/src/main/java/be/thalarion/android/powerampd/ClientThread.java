package be.thalarion.android.powerampd;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.command.CommandLine;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolOK;

/**
 * ClientThread - handles information flow from and to a client
 */
public class ClientThread implements Runnable {

    protected State state;

    public ClientThread(Context context, Socket socket) {
        this.state = new State(context, socket);
    }

    @Override
    public void run() {
        try {
            // MPD protocol version
            state.send(new ProtocolOK("MPD 0.19.0"));

            while (!Thread.currentThread().isInterrupted()) {
                String line = state.readLine();
                if (line == null || line.length() == 0) {
                    state.send(new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, "No command given"));
                    break;
                } else try {
                    Command command = Parser.parse(line);
                    command.execute(state);
                } catch (ProtocolException e) {
                    state.send(e);
                }
            }
        } catch (IOException e) {
            // socket.close() called in ServerThread
        } finally {
            state.close();
        }
    }

}
