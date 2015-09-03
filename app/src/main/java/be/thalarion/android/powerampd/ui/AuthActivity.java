package be.thalarion.android.powerampd.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import be.thalarion.android.powerampd.R;

public class AuthActivity extends PreferenceActivity {

    private List<PasswordEntry> passwordEntries;

    protected static final int ACTION_NEW     = 1;
    protected static final int ACTION_EDIT    = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addPreferencesFromResource(R.xml.settings_auth);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (!preference.hasKey()) return true;

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, EditAuthActivity.class);
        switch (item.getItemId()) {
            case R.id.action_auth_add:
                intent.putExtra("requestCode", EditAuthActivity.ACTION_NEW);
                return true;
            default:
                intent.putExtra("requestCode", EditAuthActivity.ACTION_EDIT);
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EditAuthActivity.ACTION_NEW && resultCode == RESULT_OK) {
            // Add item to list
        } else if (requestCode == EditAuthActivity.ACTION_EDIT && resultCode == RESULT_OK) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_auth, menu);
        return true;
    }

}
