package be.thalarion.android.powerampd.service;

import android.content.Context;

import java.io.IOException;
import java.net.Socket;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.Executable;
import be.thalarion.android.powerampd.command.Parser;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolOK;

/**
 * ClientThread - handles information flow from and to a client
 */
public class ClientThread implements Runnable {

    private final State state;
    private final Context context;

    public ClientThread(Context context, Socket socket) {
        this.state = new State(context, socket);
        this.context = context;
    }

    @Override
    public void run() {
        try {
            // MPD protocol version
            state.send(new ProtocolOK(context.getString(R.string.proto_handshake)));

            Parser parser = new Parser(context);

            while (!Thread.currentThread().isInterrupted()) {
                String line = state.readLine();
                Executable executable = null;
                if (line == null || line.length() == 0) {
                    state.send(new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, state.getContext().getString(R.string.proto_error_command_none)));
                    break;
                } else try {
                    // Parse command
                    executable = parser.parse(line);
                    executable.execute(state);
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
