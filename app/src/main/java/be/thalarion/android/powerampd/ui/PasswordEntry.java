package be.thalarion.android.powerampd.ui;

import java.io.Serializable;

public class PasswordEntry implements Serializable {

    private String password;
    private boolean read;
    private boolean add;
    private boolean control;
    private boolean admin;

    public PasswordEntry() {
        this.password = "";
        this.read = false;
        this.add = false;
        this.control = false;
        this.admin = false;
    }

    public PasswordEntry(String string) {
        String[] split = string.split("@");
        this.password = split[0];
        for (String str: split[1].split(",")) {
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
