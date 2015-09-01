package be.thalarion.android.powerampd;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import be.thalarion.android.powerampd.protocol.Protocol;

/**
 * Handle - handles client socket communication
 */
public class Handle {

    protected final Context context;
    private final Socket socket;

    protected BufferedReader reader;
    protected BufferedWriter writer;

    public Handle(Context context, Socket socket) {
        this.context = context;
        this.socket = socket;

        try {
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
            this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    protected void send(Protocol protocol) {
        try {
            this.writer.write(protocol.toString());
            this.writer.flush();
        } catch(IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    protected void exit() {
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
