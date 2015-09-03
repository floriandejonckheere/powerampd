package be.thalarion.android.powerampd;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.WorkerThread.Handle;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolMessage;
import be.thalarion.android.powerampd.protocol.ProtocolOK;

/**
 * Parser - parse and build commands
 */
public class Parser {

    public static List<String> tokenize(String command) {
        if (command == null || command.length() == 0)
            return null;

        List<String> list = new ArrayList<String>();

        String[] cmdline = command.split("[ \t]");
        String string = "";
        for (int i = 0; i < cmdline.length; i++) {
            if (cmdline[i].startsWith("\"")) {
                string += cmdline[i];
                for (i++; i < cmdline.length; i++) {
                    string += ' ';
                    string += cmdline[i];
                    if (cmdline[i].endsWith("\""))
                        break;
                }
                list.add(string);
                string = "";
            } else list.add(cmdline[i]);
        }

        return list;
    }


    private enum COMMAND {
        CLOSE,
        CURRENTSONG,
        NEXT,
        PASSWORD,
        PAUSE,
        PREVIOUS,
        STATUS
    }

    public static Command parse(List<String> cmdline)
            throws ProtocolException {
        try {
            switch (COMMAND.valueOf(cmdline.get(0).toUpperCase())) {
                case CLOSE:
                    return new Command(cmdline, Permission.PERMISSION_NONE, 0, 0) {
                        @Override
                        public void execute(Handle handle) throws ProtocolException {
                            handle.exit();
                        }
                    };
                case CURRENTSONG:
                    return new Command(cmdline, Permission.PERMISSION_READ, 0, 0) {
                        @Override
                        public void execute(Handle handle) throws ProtocolException {
                            handle.send(new ProtocolMessage(String.format("Title: %s",
                                    State.trackIntent.getBundleExtra(PowerampAPI.TRACK).getString(PowerampAPI.Track.TITLE))));
                            handle.send(new ProtocolOK());
                        }
                    };
                case NEXT:
                    return new Command(cmdline, Permission.PERMISSION_CONTROL, 0, 0) {
                        @Override
                        public void execute(Handle handle) {
                            handle.command(PowerampAPI.Commands.NEXT);
                            handle.send(new ProtocolOK());
                        }
                    };
                case PASSWORD:
                    return new Command(cmdline, Permission.PERMISSION_NONE, 1, 1) {
                        @Override
                        public void execute(Handle handle) throws ProtocolException {
                            handle.send(new ProtocolMessage("NANANANANA BATMAN"));
                            handle.send(new ProtocolOK());
                        }
                    };
                case PAUSE:
                    return new Command(cmdline, Permission.PERMISSION_CONTROL, 0, 1) {
                        @Override
                        public void execute(Handle handle) throws ProtocolException {
                            if (cmdline.size() > 1) {
                                if (cmdline.get(1).equals("0")) {
                                    handle.command(PowerampAPI.Commands.RESUME);
                                } else if (cmdline.get(1).equals("1")) {
                                    handle.command(PowerampAPI.Commands.PAUSE);
                                } else throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                                        String.format("Boolean (0/1) expected: %s", cmdline.get(1)));
                            } else handle.command(PowerampAPI.Commands.TOGGLE_PLAY_PAUSE);
                            handle.send(new ProtocolOK());
                        }
                    };
                case PREVIOUS:
                    return new Command(cmdline, Permission.PERMISSION_CONTROL, 0, 0) {
                        @Override
                        public void execute(Handle handle) throws ProtocolException {
                            handle.command(PowerampAPI.Commands.PREVIOUS);
                            handle.send(new ProtocolOK());
                        }
                    };
                case STATUS:
                    return new Command(cmdline, Permission.PERMISSION_READ, 0, 0) {
                        @Override
                        public void execute(Handle handle) throws ProtocolException {
                            handle.send(new ProtocolOK());
                        }
                    };
                default:
                    throw new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, cmdline.get(0), "command not implemented");
            }
        } catch (ProtocolException e) {
            // Malformed command
            throw e;
        } catch (IllegalArgumentException e) {
            // Unknown command
            throw new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, cmdline.get(0),
                    String.format("unknown command \"%s\"", cmdline.get(0)));
        }
    }
}
