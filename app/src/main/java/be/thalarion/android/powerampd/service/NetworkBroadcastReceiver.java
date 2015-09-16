package be.thalarion.android.powerampd.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import be.thalarion.android.powerampd.R;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkBroadcastReceiver.toggleService(context);
    }

    /**
     * toggleService - start or stop service based on network and preferences
     * @param context
     */
    public static void toggleService(Context context, boolean enabled) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        Intent intent = new Intent(context, DaemonService.class);

        if (info != null && info.isConnected() && enabled) {
            context.startService(intent);
        } else context.stopService(intent);
    }

    public static void toggleService(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        toggleService(context, preferences.getBoolean("pref_enabled", context.getString(R.string.pref_enabled_default).equals("true")));
    }
}
