package be.thalarion.android.powerampd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class AuthActivity extends PreferenceActivity
        implements View.OnClickListener,
                    Preference.OnPreferenceClickListener {

    private List<PasswordEntry> passwordEntries;

    private PreferenceCategory entryListView;
    private Button addPasswordButton;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        addPreferencesFromResource(R.xml.settings_auth);

        entryListView = (PreferenceCategory) findPreference("pref_auth_entries");
        addPasswordButton = (Button) findViewById(R.id.button_add_password);
        addPasswordButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        entryListView.removeAll();

        passwordEntries = PasswordEntry.listAll(PasswordEntry.class);

        for (PasswordEntry p: passwordEntries) {
            Preference pref = new IdentifierPreference(this, p.getId());
            pref.setTitle(p.password);
            pref.setSummary(p.getPermissionSummary());
            pref.setOnPreferenceClickListener(this);

            entryListView.addPreference(pref);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == addPasswordButton) {
            Intent intent = new Intent(this, EditAuthActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference instanceof IdentifierPreference) {
            Intent intent = new Intent(this, EditAuthActivity.class);
            intent.putExtra("id", ((IdentifierPreference) preference).getIdentifier());
            startActivity(intent);
        }
        return true;
    }

    private class IdentifierPreference extends Preference {

        private long identifier;

        public IdentifierPreference(Context context, long identifier) {
            super(context);
            this.identifier = identifier;
        }

        public long getIdentifier() {
            return identifier;
        }
    }
}
