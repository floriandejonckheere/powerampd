package be.thalarion.android.powerampd;

import android.content.Context;
import android.content.Intent;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;

/**
 * Parser - parse and build commands
 */
public class Parser {

    public static List<String> tokenize(String command) {
        if(command == null || command.length() == 0)
            return null;

        List<String> list = new ArrayList<String>();

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


    private enum COMMAND {
        NEXT,
        PREVIOUS,
        CURRENTSONG
    }

    public static Command parse(List<String> cmdline)
            throws ProtocolException {
        try {
            switch (COMMAND.valueOf(cmdline.get(0).toUpperCase())) {
                case NEXT:
                    return new Command(cmdline, Permission.PERMISSION_CONTROL, 0, 0) {
                        @Override
                        public void execute(Handle handle, List<String> cmdline) {
                            command(handle.context, PowerampAPI.Commands.NEXT);
                        }
                    };
                case PREVIOUS:
                    return new Command(cmdline, Permission.PERMISSION_CONTROL, 0, 0) {
                        @Override
                        public void execute(Handle handle, List<String> cmdline) {
                            command(handle.context, PowerampAPI.Commands.PREVIOUS);
                        }
                    };
                default:
                    throw new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, cmdline.get(0), "command not implemented");
            }
        } catch(ProtocolException e) {
            // Malformed command
            throw e;
        } catch(IllegalArgumentException e) {
            // Unknown command
            throw new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, cmdline.get(0),
                    String.format("unknown command \"%s\"", cmdline.get(0)));
        }
    }

    private static void command(Context context, int action){
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND).setPackage(PowerampAPI.PACKAGE_NAME).putExtra(PowerampAPI.COMMAND, action));
    }
}
