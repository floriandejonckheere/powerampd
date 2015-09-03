package be.thalarion.android.powerampd.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import be.thalarion.android.powerampd.R;

public class EditAuthActivity extends PreferenceActivity {

    private PasswordEntry passwordEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_auth_edit);
        setContentView(R.layout.activity_auth_edit);

        // TODO: bind preference
        MainActivity.bindPreferenceSummaryToValue(findPreference("pref_password"), getSharedPreferences());
//        MainActivity.bindPreferenceSummaryToValue(findPreference("pref_permission_read"));
//        MainActivity.bindPreferenceSummaryToValue(findPreference("pref_permission_add"));
//        MainActivity.bindPreferenceSummaryToValue(findPreference("pref_permission_control"));
//        MainActivity.bindPreferenceSummaryToValue(findPreference("pref_permission_admin"));

        findPreference("pref_password").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if(((String) o).length() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.toast_error_password_length, Toast.LENGTH_LONG);
                    return false;
                }
                return true;
            }
        });

        String string = getIntent().getStringExtra("passwordEntry");
        if (string == null || string.length() == 0) {
            passwordEntry = new PasswordEntry();
        } else passwordEntry = new PasswordEntry(string);
    }

}
