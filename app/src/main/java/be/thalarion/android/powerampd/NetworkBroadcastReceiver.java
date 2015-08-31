package be.thalarion.android.powerampd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        if(preferences.getBoolean("enabled", true)) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null) {
                if (info.isConnected()) {
                    Intent daemonIntent = new Intent(context, DaemonService.class);
                    context.startService(daemonIntent);
                } else {
                    Intent daemonIntent = new Intent(context, DaemonService.class);
                    context.stopService(daemonIntent);
                }
            }
        }
    }
}
