package be.thalarion.android.powerampd;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import be.thalarion.android.powerampd.protocol.Protocol;
import be.thalarion.android.powerampd.protocol.ProtocolCompletion;
import be.thalarion.android.powerampd.protocol.ProtocolError;
import be.thalarion.android.powerampd.protocol.Tokenizer;

public class DaemonThread implements Runnable {

    private Socket socket;
    private Context context;

    private BufferedReader reader;
    private BufferedWriter writer;

    public DaemonThread(Context context, Socket socket) {
        this.context = context;
        this.socket = socket;

        try {
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    @Override
    public void run() {
        try {
            Log.i("powerampd", "Sending protocol handshake");
            // MPD protocol version
            send(new ProtocolCompletion());

            while(!Thread.currentThread().isInterrupted()) {
                String command = reader.readLine();
                if(command.length() == 0) {
                    send(new ProtocolError(ProtocolError.UNKNOWN_COMMAND, 0, command, "No command given"));
                    exit();
                }
                List<String> cmdline = Tokenizer.tokenize(command);
                Log.i("powerampd", String.format("%d\n", cmdline.size()));
                if(cmdline.get(0).equals("volume")) {

                } else {
                    send(new ProtocolError(ProtocolError.UNKNOWN_COMMAND, 0, cmdline.get(0),
                            String.format("unknown command \"%s\"", cmdline.get(0))));
                }

            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Protocol protocol) {
        try {
            this.writer.write(protocol.toString());
            this.writer.flush();
        } catch(IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    public void exit() {
        try {
            this.reader.close();
            this.writer.close();
            Thread.currentThread().interrupt();
            this.socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
