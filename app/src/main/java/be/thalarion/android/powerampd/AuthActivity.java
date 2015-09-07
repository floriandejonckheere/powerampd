package be.thalarion.android.powerampd;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthActivity extends PreferenceActivity
        implements View.OnClickListener,
                    Preference.OnPreferenceClickListener {

    private SQLiteDatabase database;
    private List<PasswordEntry> passwordEntries;

    protected static final int ACTION_NEW     = 1;
    protected static final int ACTION_EDIT    = 2;

    private PreferenceCategory entryList;
    private Button addPasswordButton;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        addPreferencesFromResource(R.xml.settings_auth);

        database = openOrCreateDatabase("PasswordEntries", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS PasswordEntries (" +
                "password VARCHAR, " +
                "permRead BOOLEAN NOT NULL DEFAULT 0 CHECK (permRead IN (0,1)), " +
                "permAdd BOOLEAN NOT NULL DEFAULT 0 CHECK (permAdd IN (0,1)), " +
                "permControl BOOLEAN NOT NULL DEFAULT 0 CHECK (permControl IN (0,1)), " +
                "permAdmin BOOLEAN NOT NULL DEFAULT 0 CHECK (permAdmin IN (0,1)));");

        passwordEntries = new ArrayList<PasswordEntry>();
        entryList = (PreferenceCategory) findPreference("pref_auth_entries");

        addPasswordButton = (Button) findViewById(R.id.button_add_password);
        addPasswordButton.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        entryList.removeAll();

        Cursor c = database.rawQuery("SELECT * FROM PasswordEntries;", null);
        while (c.moveToNext()) {
            PasswordEntry entry = new PasswordEntry(
                    c.getString(c.getColumnIndex("password")),
                    (c.getInt(c.getColumnIndex("permRead")) == 1),
                    (c.getInt(c.getColumnIndex("permAdd")) == 1),
                    (c.getInt(c.getColumnIndex("permControl")) == 1),
                    (c.getInt(c.getColumnIndex("permAdmin")) == 1)
            );
            Preference pref = new Preference(this) {
                @Override
                protected View onCreateView(ViewGroup parent) {
                    return super.onCreateView(parent);
                }
            };
            pref.setTitle(entry.getPassword());
            pref.setSummary(entry.getPermissionSummary());
            pref.setOnPreferenceClickListener(this);

            entryList.addPreference(pref);
            passwordEntries.add(entry);
        }
        c.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            PasswordEntry entry = (PasswordEntry) data.getSerializableExtra("passwordEntry");
            if (requestCode == ACTION_NEW) {
                passwordEntries.add(entry);
            } else {
                passwordEntries.add(data.getIntExtra("index", 0), entry);
            }
            // Sync to database
            database.beginTransaction();
            database.execSQL("DELETE FROM PasswordEntries;");
            for (PasswordEntry p: passwordEntries) {
                SQLiteStatement stmt = database.compileStatement("INSERT INTO PasswordEntries (password, permRead, permAdd, permControl, permAdmin) VALUES (?, ?, ?, ?, ?);");
                stmt.bindString(1, p.getPassword());
                stmt.bindLong(2, (p.read ? 1 : 0));
                stmt.bindLong(3, (p.add ? 1 : 0));
                stmt.bindLong(4, (p.control ? 1 : 0));
                stmt.bindLong(5, (p.admin ? 1 : 0));
                stmt.execute();
                stmt.clearBindings();
            }
            database.setTransactionSuccessful();
            database.endTransaction();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == addPasswordButton) {
            Intent intent = new Intent(this, EditAuthActivity.class);
            intent.putExtra("passwordEntry", new PasswordEntry(getString(R.string.pref_password_default, false, false, false, false)));
            startActivityForResult(intent, ACTION_NEW);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent(getApplicationContext(), EditAuthActivity.class);
        PasswordEntry entry = null;
        int i;
        for (i = 0; i < passwordEntries.size(); i++) {
            if (passwordEntries.get(i).getPassword().equals(preference.getTitle()))
                entry = passwordEntries.get(i);
        }
        intent.putExtra("passwordEntry", entry);
        intent.putExtra("index", i);
        startActivityForResult(intent, ACTION_EDIT);
        return true;
    }
}
