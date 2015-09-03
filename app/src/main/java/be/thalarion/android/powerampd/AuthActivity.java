package be.thalarion.android.powerampd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AuthActivity extends PreferenceActivity {

    private ListAdapter adapter;

    protected static final int ACTION_NEW     = 1;
    protected static final int ACTION_EDIT    = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        addPreferencesFromResource(R.xml.settings_auth);

        List<PasswordEntry> passwordEntries = new ArrayList<PasswordEntry>();
        String entries = PreferenceManager.getDefaultSharedPreferences(this).getString("passwordEntries", "");
        String[] split = entries.split("\n");
        for (int i = 0; i < split.length; i ++) {
            passwordEntries.add(new PasswordEntry(split[i]));
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ListView entryList = ((ListView) findViewById(R.id.dynamic_list));
        adapter = new ListAdapter(this, R.layout.row, passwordEntries);
        entryList.setAdapter(adapter);
        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /**
                 * When editing an entry, the values are loaded into the shared preferences,
                 * where they are edited by a standard PreferenceActivity. Afterwards, the
                 * values are extracted and saved again in onActivityResult()
                 */
                PasswordEntry entry = adapter.getItem(i);
                prefs.edit()
                        .putString("pref_password", entry.getPassword())
                        .putBoolean("pref_permission_read", entry.read)
                        .putBoolean("pref_permission_add", entry.add)
                        .putBoolean("pref_permission_control", entry.control)
                        .putBoolean("pref_permission_admin", entry.admin)
                        .commit();
                Intent intent = new Intent(getApplicationContext(), EditAuthActivity.class);
                intent.putExtra("index", i);
                startActivityForResult(intent, ACTION_EDIT);
            }
        });
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                String entries = "";
                for (int i = 0; i < getListAdapter().getCount(); i++) {
                    entries += getListAdapter().getItem(i).toString() + "\n";
                }
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                        .putString("passwordEntries", entries)
                        .commit();
            }
        });
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (!preference.hasKey()) return true;

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_auth_add:
                // Set shared prefs to default value
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putString("pref_password",             "<password>")
                        .putBoolean("pref_permission_read",     false)
                        .putBoolean("pref_permission_add",      false)
                        .putBoolean("pref_permission_control",  false)
                        .putBoolean("pref_permission_admin",    false)
                        .commit();
                Intent intent = new Intent(this, EditAuthActivity.class);
                startActivityForResult(new Intent(this, EditAuthActivity.class), ACTION_NEW);
                return true;
        }
        return onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTION_NEW) {
                // Add item to list
                adapter.add(new PasswordEntry(intent.getStringExtra("passwordEntry")));
                adapter.notifyDataSetChanged();

            } else if (requestCode == ACTION_EDIT) {
                // Update item in list
                adapter.insert(new PasswordEntry(intent.getStringExtra("passwordEntry")), intent.getIntExtra("index", 0));
                adapter.notifyDataSetChanged();
            }
        } else Toast.makeText(this, R.string.toast_error_cancel, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_auth, menu);
        return true;
    }

}
