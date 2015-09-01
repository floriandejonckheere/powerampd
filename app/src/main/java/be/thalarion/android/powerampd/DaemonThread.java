package be.thalarion.android.powerampd;

import android.content.Context;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import be.thalarion.android.powerampd.protocol.ProtocolAcknowledgement;
import be.thalarion.android.powerampd.protocol.ProtocolException;

/**
 * DaemonThread - handles information flow (receiving, parsing, sending) and Poweramp broadcasts
 */
public class DaemonThread implements Runnable {

    private Handle handle;

    public DaemonThread(Context context, Socket socket) {
        this.handle = new Handle(context, socket);
    }

    @Override
    public void run() {
        register();
        try {
            // MPD protocol version
            handle.send(new ProtocolAcknowledgement("MPD 0.19.0"));

            while (!Thread.currentThread().isInterrupted()) {
                String line = handle.reader.readLine();
                if (line == null || line.length() == 0) {
                    handle.send(new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, "No command given"));
                    break;
                } else try {
                    List<String> cmdline = Parser.tokenize(line);
                    Command command = Parser.parse(cmdline);
                    command.execute(handle, cmdline);
                    handle.send(new ProtocolAcknowledgement());
                } catch (ProtocolException e) {
                    // Unknown, malformed or not implemented command
                    handle.send(e);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        this.handle.exit();
        unregister();
    }

    // Register Poweramp broadcast receivers
    private void register() {

    }

    // Unregister Poweramp broadcast receivers
    private void unregister() {

    }
}
