package be.thalarion.android.powerampd;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.maxmpz.poweramp.player.PowerAMPiAPIHelper;
import com.maxmpz.poweramp.player.PowerampAPI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class DaemonThread implements Runnable {

    private Socket socket;
    private Context context;

    private BufferedReader reader;
    private BufferedWriter writer;

    private enum Command {
        COMMAND_LIST_BEGIN,
        COMMAND_LIST_OK_BEGIN,
        COMMAND_LIST_END,
        NEXT,
        PREVIOUS
    }

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
            send(new Protocol.Handshake());

            while(!Thread.currentThread().isInterrupted()) {
                String line = reader.readLine();
                if(line == null || line.length() == 0) {
                    send(new Protocol.Error(Protocol.Error.UNKNOWN_COMMAND, 0, line, "No command given"));
                    exit();
                } else {
                    List<String> cmdline = Tokenizer.tokenize(line);

                    try {
                        switch(Command.valueOf(cmdline.get(0).toUpperCase())) {
                            case NEXT:
                                command(PowerampAPI.Commands.NEXT);
                                send(new Protocol.Completion());
                                break;
                            case PREVIOUS:
                                command(PowerampAPI.Commands.PREVIOUS);
                                send(new Protocol.Completion());
                                break;
                        }
                    } catch(IllegalArgumentException e) {
                        // Unknown command
                        send(new Protocol.Error(Protocol.Error.UNKNOWN_COMMAND, 0, cmdline.get(0),
                                String.format("unknown command \"%s\"", cmdline.get(0))));
                    }
                }

            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void send(Protocol protocol) {
        try {
            this.writer.write(protocol.toString());
            this.writer.flush();
        } catch(IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    private void exit() {
        try {
            this.reader.close();
            this.writer.close();
            Thread.currentThread().interrupt();
            this.socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void command(int action){
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND).setPackage(PowerampAPI.PACKAGE_NAME).putExtra(PowerampAPI.COMMAND, action));
    }
}
