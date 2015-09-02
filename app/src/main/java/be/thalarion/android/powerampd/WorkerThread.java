package be.thalarion.android.powerampd;

import android.content.Context;
import android.content.Intent;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import be.thalarion.android.powerampd.protocol.Protocol;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolOK;

/**
 * WorkerThread - handles information flow from and to a client
 */
public class WorkerThread implements Runnable {

    private Context context;
    private Socket socket;
    private Handle handle;

    private BufferedReader reader;
    private BufferedWriter writer;

    public WorkerThread(Context context, Socket socket) {
        this.context = context;
        this.socket = socket;
        this.handle = new Handle();
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    @Override
    public void run() {
        try {
            // MPD protocol version
            send(new ProtocolOK("MPD 0.19.0"));

            while (!Thread.currentThread().isInterrupted()) {
                String line = reader.readLine();
                if (line == null || line.length() == 0) {
                    send(new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, "No command given"));
                    break;
                } else try {
                    List<String> cmdline = Parser.tokenize(line);
                    Command command = Parser.parse(cmdline);
                    command.execute(handle);
                } catch (ProtocolException e) {
                    // Unknown, malformed or not implemented command
                    send(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        exit();
    }

    private void send(Protocol protocol) {
        try {
            writer.write(protocol.toString());
        } catch (IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    private void exit() {
        try {
            reader.close();
            writer.close();
            Thread.currentThread().interrupt();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle - handle to socket operations
     */
    protected class Handle {
        public void send(Protocol protocol) { WorkerThread.this.send(protocol); }
        public void exit() { WorkerThread.this.exit(); }
        protected void command(int action){
            WorkerThread.this.context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND).setPackage(PowerampAPI.PACKAGE_NAME).putExtra(PowerampAPI.COMMAND, action));
        }
    }
}
