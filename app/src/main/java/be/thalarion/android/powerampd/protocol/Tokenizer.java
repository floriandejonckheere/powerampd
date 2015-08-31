package be.thalarion.android.powerampd.protocol;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public static List<String> tokenize(String command) {
        List<String> list = new ArrayList<String>();
        // TODO: UTF-8?

        String[] cmdline = command.split("[ \t]");
        String string = "";
        for(int i = 0; i < cmdline.length; i++) {
            if(cmdline[i].startsWith("\"")) {
                string += cmdline[i];
                for(i++; i < cmdline.length; i++) {
                    string += ' ';
                    string += cmdline[i];
                    if(cmdline[i].endsWith("\""))
                        break;
                }
                list.add(string);
                string = "";
            } else list.add(cmdline[i]);
        }

        return list;
    }

}
