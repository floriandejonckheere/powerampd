package be.thalarion.android.powerampd.service;

import android.content.Context;

import java.io.IOException;
import java.net.Socket;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.Executable;
import be.thalarion.android.powerampd.command.Parser;
import be.thalarion.android.powerampd.protocol.Connection;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolOK;

/**
 * ClientThread - handles information flow from and to a client
 */
public class ClientThread implements Runnable {

    private final Connection conn;
    private final Context context;

    public ClientThread(Context context, Socket socket) {
        this.conn = new Connection(context, socket);
        this.context = context;
    }

    @Override
    public void run() {
        try {
            // MPD protocol version
            conn.send(new ProtocolOK(context.getString(R.string.proto_handshake)));

            Parser parser = new Parser(context);

            while (!Thread.currentThread().isInterrupted()) {
                String line = conn.readLine();
                Executable executable;
                if (line == null || line.length() == 0) {
                    conn.send(new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, conn.getContext().getString(R.string.proto_error_command_none)));
                    break;
                } else try {
                    // Parse command
                    executable = parser.parse(line);
                    executable.execute(conn);
                } catch (ProtocolException e) {
                    conn.send(e);
                }
            }
        } catch (IOException e) {
            // socket.close() called in ServerThread
        } finally {
            conn.close();
        }
    }

}
