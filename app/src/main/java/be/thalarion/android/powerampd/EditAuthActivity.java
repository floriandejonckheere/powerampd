package be.thalarion.android.powerampd;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

public class EditAuthActivity extends PreferenceActivity {

    private EditTextPreference password;
    private CheckBoxPreference permRead, permAdd, permControl, permAdmin;
    private PasswordEntry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_auth_edit);
        setContentView(R.layout.activity_auth_edit);

        password    = (EditTextPreference) findPreference("pref_password");
        permRead    = (CheckBoxPreference) findPreference("pref_permission_read");
        permAdd     = (CheckBoxPreference) findPreference("pref_permission_add");
        permControl = (CheckBoxPreference) findPreference("pref_permission_control");
        permAdmin   = (CheckBoxPreference) findPreference("pref_permission_admin");

        entry = PasswordEntry.findById(PasswordEntry.class, getIntent().getLongExtra("id", 0));
        if (entry == null) {
            // New entry
            entry = new PasswordEntry(getApplicationContext().getString(R.string.pref_password_default), false, false, false, false);
            findViewById(R.id.button_delete).setEnabled(false);
        }

        password.setText(entry.password);
        permRead.setChecked(entry.readPerm);
        permAdd.setChecked(entry.addPerm);
        permControl.setChecked(entry.controlPerm);
        permAdmin.setChecked(entry.adminPerm);

        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString("pref_password", entry.password)
                .putBoolean("pref_permission_read", entry.readPerm)
                .putBoolean("pref_permission_add", entry.addPerm)
                .putBoolean("pref_permission_control", entry.controlPerm)
                .putBoolean("pref_permission_admin", entry.adminPerm)
                .commit();

        findPreference("pref_password").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if (((String) o).length() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.toast_error_password_length, Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });

        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entry.password = password.getText();
                entry.readPerm = permRead.isChecked();
                entry.addPerm = permAdd.isChecked();
                entry.controlPerm = permControl.isChecked();
                entry.adminPerm = permAdmin.isChecked();
                entry.save();
                finish();
            }
        });

        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entry.delete();
                finish();
            }
        });

        MainActivity.bindPreferenceSummaryToValue(findPreference("pref_password"));
    }

}
