package be.thalarion.android.powerampd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<PasswordEntry> {

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<PasswordEntry> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row, null);
        }

        PasswordEntry p = getItem(position);

        if (p != null) {
            ((TextView) v.findViewById(android.R.id.title)).setText(p.getPassword());
            ((TextView) v.findViewById(android.R.id.summary)).setText(p.getPermissionSummary());
        }

        return v;
    }
}