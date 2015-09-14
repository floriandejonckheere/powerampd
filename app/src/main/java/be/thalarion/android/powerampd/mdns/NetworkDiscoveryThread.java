package be.thalarion.android.powerampd.mdns;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.SystemState;

public class NetworkDiscoveryThread implements Runnable {

    private static final long SLEEP_TIME = 5000;
    private static JmDNS jmDNS;

    private final Context context;

    private WifiManager.MulticastLock lock;

    public NetworkDiscoveryThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        // Native Android NSD APIs are available only for API >= 16
        // Multicast is disabled by default on Android
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("powerampdmdnslock");
        lock.setReferenceCounted(true);
        lock.acquire();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            jmDNS = JmDNS.create();
            HashMap<String, byte[]> properties = new HashMap<String, byte[]>();
            properties.put("description", context.getString(R.string.mdns_description).getBytes());
            ServiceInfo serviceInfo = ServiceInfo.create(
                    context.getString(R.string.mdns_type),
                    prefs.getString("pref_mdns_name", context.getString(R.string.pref_mdns_name_default)),
                    Integer.parseInt(prefs.getString("pref_port", context.getString(R.string.pref_port_default))),
                    0,
                    0,
                    true,
                    properties);
            jmDNS.registerService(serviceInfo);

            try {
                while (!Thread.currentThread().isInterrupted())
                    Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (lock != null) lock.release();
                jmDNS.unregisterAllServices();
                jmDNS.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
