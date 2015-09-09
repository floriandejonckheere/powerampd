package be.thalarion.android.powerampd;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;
import android.util.Log;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.command.State;

public class DaemonService extends Service {

    private android.support.v4.app.NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private static final int notificationID = R.string.notification_text_running;

    // Broadcast receivers
    private BroadcastReceiver trackBroadcastReceiver;
    private BroadcastReceiver statusBroadcastReceiver;
    private BroadcastReceiver playingModeBroadcastReceiver;

    // Daemon
    private Thread serverThread;
    private ServerSocket serverSocket;
    private int port;

    // UI handler
    private Handler handler;

    public void onCreate() {
        this.notificationBuilder = new NotificationCompat.Builder(this);
        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        this.handler = new Handler();
        this.port = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pref_port", getString(R.string.pref_port_default)));

        this.trackBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                State.trackIntent = intent;
            }
        };
        this.statusBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                State.statusIntent = intent;
            }
        };
        this.playingModeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                State.playingModeIntent = intent;
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (serverThread == null) {
            register();
            serverThread = new Thread(new ServerThread());
            serverThread.start();

            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            handler.post(new NotificationThread(getString(R.string.notification_title_running),
                    String.format("%s %s:%d\n", getString(R.string.notification_text_running), ip, + port)));
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (serverThread != null) {
            Log.i("powerampd-daemon", "Stopping service");
            // Set interrupt flag
            serverThread.interrupt();
            try {
                // Close server socket causing accept() to exit with a SocketException
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverThread = null;
            handler.post(new NotificationThread(null, null));
            unregister();
        }
    }

    // Register Poweramp broadcast receivers
    private void register() {
        registerReceiver(trackBroadcastReceiver, new IntentFilter(PowerampAPI.ACTION_TRACK_CHANGED));
        registerReceiver(statusBroadcastReceiver, new IntentFilter(PowerampAPI.ACTION_STATUS_CHANGED));
        registerReceiver(playingModeBroadcastReceiver, new IntentFilter(PowerampAPI.ACTION_PLAYING_MODE_CHANGED));
    }

    // Unregister Poweramp broadcast receivers
    private void unregister() {
        unregisterReceiver(trackBroadcastReceiver);
        unregisterReceiver(statusBroadcastReceiver);
        unregisterReceiver(playingModeBroadcastReceiver);
    }

    /**
     * ServerThread - bind to TCP port and fork
     */
    class ServerThread implements Runnable {
        private List<Socket> clientSockets = new ArrayList<Socket>();

        @Override
        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(port);
                while (!Thread.currentThread().isInterrupted()) {
                    socket = serverSocket.accept();
                    clientSockets.add(socket);
                    new Thread(new ClientThread(getApplicationContext(), socket)).start();
                }
            } catch (SocketException e) {
                // Socket.close() called in service
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.i("powerampd-server", "Stopping server thread and client threads");
                for (Socket s: clientSockets) {
                    if (!s.isClosed())
                        try {
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
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
            if (this.title == null && this.text == null) {
                notificationManager.cancel(notificationID);
            } else {
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.setAction(Intent.ACTION_MAIN);
                resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
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
