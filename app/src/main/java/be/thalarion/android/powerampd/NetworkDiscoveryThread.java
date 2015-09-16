package be.thalarion.android.powerampd;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import be.thalarion.android.powerampd.R;

public class NetworkDiscoveryThread implements Runnable {

    // Lock for name changes
    public static final Object lock = new Object();

    private final Context context;
    private JmDNS jmDNS;
    private WifiManager.MulticastLock multicastLock;

    public NetworkDiscoveryThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        // Native Android NSD APIs are available only for API >= 16
        // Multicast is disabled by default on Android
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        multicastLock = wifi.createMulticastLock("powerampdmdnslock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();

        try {
            String hostname = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("pref_mdns_hostname", Build.MODEL.replace(' ', '-'));
            jmDNS = JmDNS.create(hostname);
            Log.i("powerampd-mdns", jmDNS.getHostName());

            // Register ServiceInfo
            register();

            /**
             * register() takes quite a while to complete. If the thread is interrupted during this
             * time, unregister() will not be called.
             */
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    synchronized (lock) {
                        lock.wait();
                    }
                    unregister();
                    register();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Unregister ServiceInfo
                unregister();

                if (multicastLock != null) multicastLock.release();
                jmDNS.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void register()
            throws IOException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

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
    }

    private void unregister() {
        jmDNS.unregisterAllServices();
    }
}
