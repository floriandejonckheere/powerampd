package be.thalarion.android.powerampd;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.command.CommandLine;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolMessage;
import be.thalarion.android.powerampd.protocol.ProtocolOK;

/**
 * Parser - parse and build commands
 */
public class Parser {

    private static List<String> tokenize(String command) {
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
        STATUS,

        DEBUG
    }

    public static CommandLine parse(String command)
            throws ProtocolException {
        List<String> cmdline = Parser.tokenize(command);
        try {
            switch (COMMAND.valueOf(cmdline.get(0).toUpperCase())) {
                case DEBUG:
                    return new CommandLine(cmdline, Permission.PERMISSION_NONE, 0, 0) {
                        @Override
                        public void executeCommand(State state) throws ProtocolException {
                            state.send(new ProtocolMessage(String.format("authenticated: %s", state.isAuthenticated())));
                            state.send(new ProtocolMessage(String.format("auth_enabled: %s", state.getPreferences().getBoolean("pref_auth_enabled", true))));
                            state.send(new ProtocolMessage(String.format("can_none: %s", state.authorize(Permission.PERMISSION_NONE))));
                            state.send(new ProtocolMessage(String.format("can_read: %s", state.authorize(Permission.PERMISSION_READ))));
                            state.send(new ProtocolMessage(String.format("can_add: %s", state.authorize(Permission.PERMISSION_ADD))));
                            state.send(new ProtocolMessage(String.format("can_control: %s", state.authorize(Permission.PERMISSION_CONTROL))));
                            state.send(new ProtocolMessage(String.format("can_admin: %s", state.authorize(Permission.PERMISSION_ADMIN))));
                            state.send(new ProtocolOK());
                        }
                    };
                case CLOSE:
                    return new CommandLine(cmdline, Permission.PERMISSION_NONE, 0, 0) {
                        @Override
                        public void executeCommand(State state) throws ProtocolException {
                            state.close();
                        }
                    };
                case CURRENTSONG:
                    return new CommandLine(cmdline, Permission.PERMISSION_READ, 0, 0) {
                        @Override
                        public void executeCommand(State state) throws ProtocolException {
                            state.send(new ProtocolMessage(String.format("Title: %s",
                                    State.trackIntent.getBundleExtra(PowerampAPI.TRACK).getString(PowerampAPI.Track.TITLE))));
                            state.send(new ProtocolOK());
                        }
                    };
                case NEXT:
                    return new CommandLine(cmdline, Permission.PERMISSION_CONTROL, 0, 0) {
                        @Override
                        public void executeCommand(State state) throws ProtocolException {
                            state.command(PowerampAPI.Commands.NEXT);
                            state.send(new ProtocolOK());
                        }
                    };
                case PASSWORD:
                    return new CommandLine(cmdline, Permission.PERMISSION_NONE, 1, 1) {
                        @Override
                        public void executeCommand(State state) throws ProtocolException {
                            if (!state.authenticate(cmdline.get(1)))
                                throw new ProtocolException(ProtocolException.ACK_ERROR_PASSWORD, cmdline.get(0),
                                        "incorrect password");

                            state.send(new ProtocolOK());
                        }
                    };
                case PAUSE:
                    return new CommandLine(cmdline, Permission.PERMISSION_CONTROL, 0, 1) {
                        @Override
                        public void executeCommand(State state) throws ProtocolException {
                            if (cmdline.size() > 1) {
                                if (cmdline.get(1).equals("0")) {
                                    state.command(PowerampAPI.Commands.RESUME);
                                } else if (cmdline.get(1).equals("1")) {
                                    state.command(PowerampAPI.Commands.PAUSE);
                                } else throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                                        String.format("Boolean (0/1) expected: %s", cmdline.get(1)));
                            } else state.command(PowerampAPI.Commands.TOGGLE_PLAY_PAUSE);
                            state.send(new ProtocolOK());
                        }
                    };
                case PREVIOUS:
                    return new CommandLine(cmdline, Permission.PERMISSION_CONTROL, 0, 0) {
                        @Override
                        public void executeCommand(State state) throws ProtocolException {
                            state.command(PowerampAPI.Commands.PREVIOUS);
                            state.send(new ProtocolOK());
                        }
                    };
                case STATUS:
                    return new CommandLine(cmdline, Permission.PERMISSION_READ, 0, 0) {
                        @Override
                        public void executeCommand(State state) throws ProtocolException {
                            state.send(new ProtocolMessage(String.format("volume: %d", -1)));
                            state.send(new ProtocolMessage(String.format("repeat: %d", 0)));
                            state.send(new ProtocolMessage(String.format("random: %d", 0)));
                            state.send(new ProtocolMessage(String.format("single: %d", 0)));
                            state.send(new ProtocolMessage(String.format("consume: %d", 0)));
                            state.send(new ProtocolMessage(String.format("playlist: %d", 0)));
                            state.send(new ProtocolMessage(String.format("playlistlength: %d", 0)));
                            state.send(new ProtocolMessage(String.format("mixrampdb: %d", 0)));
                            state.send(new ProtocolMessage(String.format("state: %s", "stop")));
                            state.send(new ProtocolMessage(String.format("song: %s", 0)));
                            state.send(new ProtocolMessage(String.format("songid: %s", 0)));
                            state.send(new ProtocolMessage(String.format("nextsong: %s", 0)));
                            state.send(new ProtocolMessage(String.format("nextsongid: %s", 0)));
                            state.send(new ProtocolOK());
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
