package be.thalarion.android.powerampd;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DaemonService extends Service {

    private android.support.v4.app.NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    private int notificationID = 0;

    // Daemon
    private Thread serverThread;
    private ServerSocket serverSocket;
    public static final int port = 6600;

    public DaemonService() {
    }

    public void onCreate() {
        this.notificationBuilder = new NotificationCompat.Builder(this);
        this.serverThread = new Thread(new ServerThread());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String address = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        this.notificationBuilder.setContentTitle(this.getString(R.string.notification_title_starting))
                                    .setContentText(this.getString(R.string.notification_text_starting) + address)
                                    .setContentIntent(null);
        notificationManager.notify(this.notificationID, this.notificationBuilder.build());

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
            } catch(IOException e) {
                Toast.makeText(getApplicationContext(), R.string.toast_error_bind, Toast.LENGTH_LONG);
            }
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                    DaemonThread daemonThread = new DaemonThread(getApplicationContext(), socket);
                } catch(IOException e) {
                    Toast.makeText(getApplicationContext(), R.string.toast_error_socket, Toast.LENGTH_LONG);
                }
            }
        }
    }
}
