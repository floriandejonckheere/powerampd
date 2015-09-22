package be.thalarion.android.powerampd.password;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.orm.Record;
import be.thalarion.android.powerampd.protocol.Permission;

public class Entry extends Record<Entry> {

    public String password;
    public boolean readPerm = false;
    public boolean addPerm = false;
    public boolean controlPerm = false;
    public boolean adminPerm = false;

    public Entry(String password, boolean readPerm, boolean addPerm, boolean controlPerm, boolean adminPerm) {
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
