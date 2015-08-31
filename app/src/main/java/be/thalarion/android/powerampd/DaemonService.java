package be.thalarion.android.powerampd;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DaemonService extends Service {

    private android.support.v4.app.NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int notificationID = 0;

    // Daemon
    private Thread serverThread;
    private ServerSocket serverSocket;
    public static final int port = 6600;

    // UI handler
    private Handler handler;

    public DaemonService() {
    }

    public void onCreate() {
        this.notificationBuilder = new NotificationCompat.Builder(this);
        this.serverThread = new Thread(new ServerThread());
        this.handler = new Handler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        this.handler.post(new NotificationThread(getString(R.string.notification_title_starting),
                                                getString(R.string.notification_text_starting)));
        this.serverThread.start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    class ServerThread implements Runnable {
        @Override
        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(port);
                while(!Thread.currentThread().isInterrupted()) {
                    socket = serverSocket.accept();
                    DaemonThread daemonThread = new DaemonThread(getApplicationContext(), socket);
                }

            } catch(IOException e) {
                Toast.makeText(getApplicationContext(), R.string.toast_error_bind, Toast.LENGTH_LONG);
            }
        }
    }

    class NotificationThread implements Runnable {

        private String title;
        private String text;

        public NotificationThread(String title, String text) {
            this.title = title;
            this.text = text;
        }

        @Override
        public void run() {
            notificationBuilder
                    .setContentTitle(this.title)
                    .setContentText(this.text)
                    // .setSmallIcon(null) TODO
                    .setContentIntent(null); // TODO

            notificationManager.notify(notificationID, notificationBuilder.build());
        }
    }
}
