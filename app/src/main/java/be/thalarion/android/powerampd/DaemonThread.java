package be.thalarion.android.powerampd;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import be.thalarion.android.powerampd.protocol.Protocol;
import be.thalarion.android.powerampd.protocol.ProtocolCompletion;
import be.thalarion.android.powerampd.protocol.ProtocolError;
import be.thalarion.android.powerampd.protocol.Tokenizer;

public class DaemonThread implements Runnable {

    private Socket socket;
    private Context context;

    private final BufferedReader reader = null;
    private final BufferedWriter writer = null;

    public DaemonThread(Context context, Socket socket) {
        this.context = context;
        this.socket = socket;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    @Override
    public void run() {
        try {
            // MPD protocol version
            send(new ProtocolCompletion());

            while(!Thread.currentThread().isInterrupted()) {
                String command = reader.readLine();
                String[] cmdline = Tokenizer.tokenize(command);
                if(cmdline.length == 0) {
                    send(new ProtocolError(ProtocolError.NO_COMMAND, 0, command, "No command given"));
                    exit();
                }
                if(cmdline[0].equals("volume")) {

                } else {
                    send(new ProtocolError(ProtocolError.UNKNOWN_COMMAND, 0, cmdline[0],
                            String.format("Unknown command \"%s\"", cmdline[0])));
                }

            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Protocol protocol) {
        try {
            this.writer.write(protocol.toString());
        } catch(IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    public void exit() {
        try {
            this.socket.close();
            Thread.currentThread().interrupt();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
