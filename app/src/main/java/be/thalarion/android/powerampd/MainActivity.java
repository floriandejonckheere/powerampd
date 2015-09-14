package be.thalarion.android.powerampd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class MainActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addPreferencesFromResource(R.xml.settings);

        bindPreferenceSummaryToValue(findPreference("pref_port"));
        bindPreferenceSummaryToValue(findPreference("pref_mdns_name"));

        findPreference("pref_enabled").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (info != null)
                    if (o.toString().equals("true")) {
                        if (info.isConnected())
                            getApplicationContext().startService(new Intent(getApplicationContext(), DaemonService.class));
                    } else {
                        getApplicationContext().stopService(new Intent(getApplicationContext(), DaemonService.class));
                    }
                return true;
            }
        });

        findPreference("pref_mdns_name").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (((String) o).length() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.toast_error_mdns_name_length, Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null)
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_enabled", true)) {
                if (info.isConnected())
                    getApplicationContext().startService(new Intent(getApplicationContext(), DaemonService.class));
            } else {
                getApplicationContext().stopService(new Intent(getApplicationContext(), DaemonService.class));
            }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (!preference.hasKey()) return true;

        if (preference.getKey().equals("pref_sub_auth")) {
            startActivity(new Intent(getApplicationContext(), AuthActivity.class));
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    protected static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list settings, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other settings, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    protected static void bindPreferenceSummaryToValue(Preference preference) {
        bindPreferenceSummaryToValue(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext()));
    }

    protected static void bindPreferenceSummaryToValue(Preference preference, SharedPreferences sharedPreferences) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                sharedPreferences.getString(preference.getKey(), ""));
    }

}
