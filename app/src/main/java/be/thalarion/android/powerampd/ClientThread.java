package be.thalarion.android.powerampd;

import android.content.Context;

import java.io.IOException;
import java.net.Socket;

import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolOK;

/**
 * ClientThread - handles information flow from and to a client
 */
public class ClientThread implements Runnable {

    private final State state;

    // Application context
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

            while (!Thread.currentThread().isInterrupted()) {
                String line = state.readLine();
                if (line == null || line.length() == 0) {
                    state.send(new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, state.context.getString(R.string.proto_error_no_command)));
                    break;
                } else try {
                    // Parse command
                    Parser parser = new Parser(context);
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
