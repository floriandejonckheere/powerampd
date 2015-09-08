package be.thalarion.android.powerampd;

import android.text.TextUtils;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.protocol.Permission;

public class PasswordEntry extends SugarRecord<PasswordEntry> {

    protected String password;
    protected boolean readPerm = false;
    protected boolean addPerm = false;
    protected boolean controlPerm = false;
    protected boolean adminPerm = false;

    public PasswordEntry() {
    }

    public PasswordEntry(String string) {
        if(string != null) {
            String[] split = string.split("@");
            this.password = split[0];
            if (split.length > 1)
                for (String str : split[1].split(",")) {
                    if (str.equals("read"))
                        readPerm = true;
                    else if (str.equals("add"))
                        addPerm = true;
                    else if (str.equals("control"))
                        controlPerm = true;
                    else if (str.equals("admin"))
                        adminPerm = true;
                }
        }
    }

    public PasswordEntry(String password, boolean readPerm, boolean addPerm, boolean controlPerm, boolean adminPerm) {
        this.password = password;
        this.readPerm = readPerm;
        this.addPerm = addPerm;
        this.controlPerm = controlPerm;
        this.adminPerm = adminPerm;
    }

    public String getPermissionSummary() {
        List<String> list = new ArrayList<String>();
        if(readPerm)    list.add("read");
        if(addPerm)     list.add("add");
        if(controlPerm) list.add("control");
        if(adminPerm)   list.add("admin");

        return TextUtils.join(", ", list);
    }

    public boolean can(Permission permission) {
        switch (permission) {
            case PERMISSION_NONE:
                return true;
            case PERMISSION_READ:
                return readPerm;
            case PERMISSION_ADD:
                return addPerm;
            case PERMISSION_CONTROL:
                return controlPerm;
            case PERMISSION_ADMIN:
                return adminPerm;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%s@%s", password, getPermissionSummary());
    }
}
