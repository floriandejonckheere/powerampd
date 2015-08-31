package be.thalarion.android.powerampd;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;

public class DaemonService extends Service {

    private android.support.v4.app.NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    private int notificationID = 0;

    public DaemonService() {
    }

    public void onCreate() {
        this.notificationBuilder = new NotificationCompat.Builder(this);
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

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
