package be.thalarion.android.powerampd;

import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PasswordEntry implements Serializable {

    private String password;
    protected boolean read = false;
    protected boolean add = false;
    protected boolean control = false;
    protected boolean admin = false;

    public PasswordEntry(String string) {
        if(string != null) {
            String[] split = string.split("@");
            this.password = split[0];
            if (split.length > 1)
                for (String str : split[1].split(",")) {
                    if (str.equals("read"))
                        read = true;
                    else if (str.equals("add"))
                        add = true;
                    else if (str.equals("control"))
                        control = true;
                    else if (str.equals("admin"))
                        admin = true;
                }
        }
    }

    public PasswordEntry(String password, boolean read, boolean add, boolean control, boolean admin) {
        this.password = password;
        this.read = read;
        this.add = add;
        this.control = control;
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public String getPermissionSummary() {
        List<String> list = new ArrayList<String>();
        if(read)    list.add("read");
        if(add)     list.add("add");
        if(control) list.add("control");
        if(admin)   list.add("admin");

        return TextUtils.join(", ", list);
    }

    @Override
    public String toString() {
        return String.format("%s@%s", password, getPermissionSummary());
    }
}
