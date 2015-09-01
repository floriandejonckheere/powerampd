package be.thalarion.android.powerampd;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class DaemonService extends Service {

    private android.support.v4.app.NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private static final int notificationID = R.string.notification_text_running;

    // Daemon
    private static Thread serverThread;
    private static ServerSocket serverSocket;

    // UI handler
    private Handler handler;

    public void onCreate() {
        this.notificationBuilder = new NotificationCompat.Builder(this);
        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        this.handler = new Handler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("powerampd", String.format("%s\n", (serverThread == null)));
        if(DaemonService.serverThread == null) {
            DaemonService.serverThread = new Thread(new ServerThread());
            DaemonService.serverThread.start();
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            int port = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pref_port", getString(R.string.pref_port_default)));
            handler.post(new NotificationThread(getString(R.string.notification_title_running),
                    String.format("%s %s:%d\n", getString(R.string.notification_text_running), ip, + port)));
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(DaemonService.serverThread != null) {
            this.serverThread.interrupt();
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.serverThread = null;
            handler.post(new NotificationThread(null, null));
        }
    }


    /**
     * ServerThread - bind to TCP port and fork
     */
    class ServerThread implements Runnable {
        @Override
        public void run() {
            int port = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pref_port", getString(R.string.pref_port_default)));
            Socket socket;
            try {
                serverSocket = new ServerSocket(port);
                while (!Thread.currentThread().isInterrupted()) {
                    socket = serverSocket.accept();
                    new DaemonThread(getApplicationContext(), socket);
                }

            } catch(SocketException e) {
                // Socket.close() called in service
                stopSelf();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * NotificationThread - persistent notification management
     */
    class NotificationThread implements Runnable {

        private String title;
        private String text;

        public NotificationThread(String title, String text) {
            this.title = title;
            this.text = text;
        }

        @Override
        public void run() {
            if(this.title == null && this.text == null) {
                notificationManager.cancel(notificationID);
            } else {
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder
                        .setContentTitle(this.title)
                        .setContentText(this.text)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentIntent(intent)
                        .setOngoing(true);

                notificationManager.notify(notificationID, notificationBuilder.build());
            }
        }
    }
}
