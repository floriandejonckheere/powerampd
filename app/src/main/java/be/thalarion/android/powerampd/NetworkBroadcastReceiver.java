package be.thalarion.android.powerampd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (info != null) {
            if (info.isConnected()) {
                if(preferences.getBoolean("pref_enabled", true)) {
                    Intent daemonIntent = new Intent(context, DaemonService.class);
                    context.startService(daemonIntent);
                }
            } else {
                Intent daemonIntent = new Intent(context, DaemonService.class);
                context.stopService(daemonIntent);
            }
        }
    }
}
