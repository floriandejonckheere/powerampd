package be.thalarion.android.powerampd;

import android.content.Intent;
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

    public static final int RESULT_DELETED = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_auth_edit);
        setContentView(R.layout.activity_auth_edit);

        MainActivity.bindPreferenceSummaryToValue(findPreference("pref_password"));

        password    = (EditTextPreference) findPreference("pref_password");
        permRead    = (CheckBoxPreference) findPreference("pref_permission_read");
        permAdd     = (CheckBoxPreference) findPreference("pref_permission_add");
        permControl = (CheckBoxPreference) findPreference("pref_permission_control");
        permAdmin   = (CheckBoxPreference) findPreference("pref_permission_admin");

        entry = (PasswordEntry) getIntent().getSerializableExtra("passwordEntry");
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString("pref_password", entry.getPassword())
                .putBoolean("pref_permission_read", entry.read)
                .putBoolean("pref_permission_add", entry.add)
                .putBoolean("pref_permission_control", entry.control)
                .putBoolean("pref_permission_admin", entry.admin)
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
                Intent intent = new Intent();
                intent.putExtra("passwordEntry", new PasswordEntry(
                        password.getText(),
                        permRead.isChecked(),
                        permAdd.isChecked(),
                        permControl.isChecked(),
                        permAdmin.isChecked()
                ));

                int index = getIntent().getIntExtra("index", -1);
                if (index != -1)
                    intent.putExtra("index", index);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                int index = getIntent().getIntExtra("index", -1);
                intent.putExtra("index", index);
                setResult(RESULT_DELETED, intent);
                finish();
            }
        });
    }

}
