package be.thalarion.android.powerampd.service;

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

import com.maxmpz.poweramp.player.PowerampAPI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.MainActivity;
import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.state.Database;
import be.thalarion.android.powerampd.state.System;

public class DaemonService extends Service {

    public static DaemonService instance;

    private NotificationManager notificationManager;
    private static final int notificationID = 1;

    // Broadcast receivers
    private BroadcastReceiver trackBroadcastReceiver;
    private BroadcastReceiver statusBroadcastReceiver;
    private BroadcastReceiver playingModeBroadcastReceiver;

    private BroadcastReceiver scanBroadcastReceiver;

    // Daemon
    private Thread serverThread;
    private ServerSocket serverSocket;
    private int port;

    // UI handler
    private Handler handler;

    // Service Discovery
    private Thread zeroConfThread;

    public void onCreate() {
        instance = this;

        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        this.handler = new Handler();
        this.port = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("pref_port",
                getString(R.string.pref_port_default)));

        this.trackBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.trackIntent = intent;
            }
        };
        this.statusBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.statusIntent = intent;
            }
        };
        this.playingModeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.playingModeIntent = intent;
            }
        };
        this.scanBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(PowerampAPI.Scanner.ACTION_DIRS_SCAN_STARTED) || action.equals(PowerampAPI.Scanner.ACTION_TAGS_SCAN_STARTED)) {
                    Database.scanning = true;
                } else if (action.equals(PowerampAPI.Scanner.ACTION_DIRS_SCAN_FINISHED) || action.equals(PowerampAPI.Scanner.ACTION_TAGS_SCAN_FINISHED)) {
                    Database.scanning = false;
                }
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) { throw new UnsupportedOperationException("Not yet implemented"); }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (serverThread == null) {
            register();
            serverThread = new Thread(new ServerThread());
            serverThread.start();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
                    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

                    updateOrSetNotification(getApplicationContext(), getString(R.string.notification_title_running),
                            getString(R.string.notification_text_running_ip, ip, port));
                }
            });
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (serverThread != null) {
            // Set interrupt flag
            serverThread.interrupt();
            try {
                // Close server socket causing accept() to exit with a SocketException
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverThread = null;
            handler.post(new Runnable() {
                @Override
                public void run() { cancelNotification(getApplicationContext()); }
            });
            unregister();
        }
    }

    private void register() {
        /**
         * Poweramp broadcast receivers
         */
        registerReceiver(trackBroadcastReceiver, new IntentFilter(PowerampAPI.ACTION_TRACK_CHANGED));
        registerReceiver(statusBroadcastReceiver, new IntentFilter(PowerampAPI.ACTION_STATUS_CHANGED));
        registerReceiver(playingModeBroadcastReceiver, new IntentFilter(PowerampAPI.ACTION_PLAYING_MODE_CHANGED));

        IntentFilter filter = new IntentFilter();
        filter.addAction(PowerampAPI.Scanner.ACTION_DIRS_SCAN_STARTED);
        filter.addAction(PowerampAPI.Scanner.ACTION_DIRS_SCAN_FINISHED);
        filter.addAction(PowerampAPI.Scanner.ACTION_TAGS_SCAN_STARTED);
        filter.addAction(PowerampAPI.Scanner.ACTION_TAGS_SCAN_PROGRESS);
        filter.addAction(PowerampAPI.Scanner.ACTION_TAGS_SCAN_FINISHED);;
        filter.addAction(PowerampAPI.Scanner.ACTION_FAST_TAGS_SCAN_FINISHED);
        registerReceiver(scanBroadcastReceiver, filter);

        startZeroConfThread();
    }

    private void unregister() {
        /**
         * Poweramp broadcast receivers
         */
        unregisterReceiver(trackBroadcastReceiver);
        unregisterReceiver(statusBroadcastReceiver);
        unregisterReceiver(playingModeBroadcastReceiver);

        unregisterReceiver(scanBroadcastReceiver);

        zeroConfThread.interrupt();
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
                    int timeout = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("pref_timeout", getString(R.string.pref_timeout_default)));
                    socket.setSoTimeout(timeout * 1000);
                    clientSockets.add(socket);
                    new Thread(new ClientThread(getApplicationContext(), socket)).start();
                }
            } catch (SocketException e) {
                // Socket.close() called in service
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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

    public static void updateOrSetNotification(Context context, String title, String text) {
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent intent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(intent)
                .setOngoing(true);

        ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).notify(notificationID, builder.build());
    }

    public static void cancelNotification(Context context) {
        ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).cancel(notificationID);
    }

    public void startZeroConfThread() {
        // New thread to start a new thread
        // Because join() blocks
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (zeroConfThread != null) {
                    zeroConfThread.interrupt();
                    try {
                        zeroConfThread.join();
                    } catch (InterruptedException ignored) {}
                }
                zeroConfThread = new Thread(new ZeroConfThread(getApplicationContext(), handler));
                zeroConfThread.start();
            }
        }).start();
    }
}
