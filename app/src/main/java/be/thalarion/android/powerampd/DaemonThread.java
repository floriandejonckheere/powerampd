package be.thalarion.android.powerampd;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class DaemonThread implements Runnable {

    private Socket socket;
    private Context context;

    public DaemonThread(Context context, Socket socket) {
        this.context = context;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

            // MPD protocol version
            writer.write("OK MPD 0.19.0\n");

            while(!Thread.currentThread().isInterrupted()) {
                String command = reader.readLine();
                writer.write(command);
            }
        } catch(Exception e) {
            Toast.makeText(this.context, R.string.toast_error_client, Toast.LENGTH_LONG);
        }
    }
}
