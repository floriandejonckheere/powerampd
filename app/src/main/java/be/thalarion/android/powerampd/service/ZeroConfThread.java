package be.thalarion.android.powerampd.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import be.thalarion.android.powerampd.R;

public class ZeroConfThread implements Runnable {

    public static final int SLEEP_TIME = 5000;

    private final Context context;
    private final Handler handler;

    private JmDNS jmDNS;
    private WifiManager.MulticastLock multicastLock;

    public ZeroConfThread(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        // Native Android NSD APIs are available only for API >= 16
        // Multicast is disabled by default on Android
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        multicastLock = wifi.createMulticastLock("powerampdmdnslock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            String hostname = prefs.getString("pref_mdns_hostname", context.getString(R.string.pref_mdns_hostname_default));
            jmDNS = JmDNS.create(hostname);

            // Update persistent notification
            handler.post(new Runnable() {
                @Override
                public void run() {
                    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                    int port = Integer.valueOf(prefs.getString("pref_port",
                            context.getString(R.string.pref_port_default)));
                    String hostname = jmDNS.getHostName().substring(0, jmDNS.getHostName().length() - 1);

                    DaemonService.updateOrSetNotification(context,
                            context.getString(R.string.notification_title_running),
                            context.getString(R.string.notification_text_running_zeroconf, hostname, ip, port));
                }
            });

            Log.i("powerampd-mdns", jmDNS.getInterface().getCanonicalHostName());

            // Register ServiceInfo
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
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(SLEEP_TIME);
                }
            } catch (InterruptedException ignored) {
            } finally {
                // Unregister ServiceInfo
                jmDNS.unregisterAllServices();

                if (multicastLock != null) multicastLock.release();
                jmDNS.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
