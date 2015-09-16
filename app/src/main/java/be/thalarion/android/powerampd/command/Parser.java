package be.thalarion.android.powerampd.command;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.commands.Connection;
import be.thalarion.android.powerampd.command.commands.Meta;
import be.thalarion.android.powerampd.command.commands.PlaybackControl;
import be.thalarion.android.powerampd.command.commands.PlaybackOptions;
import be.thalarion.android.powerampd.command.commands.PlaybackStatus;
import be.thalarion.android.powerampd.protocol.ProtocolException;

/**
 * Parser - parse and build commands
 */
public class Parser {

    private final Context context;
    private CommandList commandList;

    private enum COMMAND {
        CLOSE,
        COMMAND_LIST_BEGIN,
        COMMAND_LIST_END,
        COMMAND_LIST_OK_BEGIN,
        CONSUME,
        CURRENTSONG,
        NEXT,
        PASSWORD,
        PAUSE,
        PING,
        PREVIOUS,
        SETVOL,
        STATUS,
        VOLUME,

        DEBUG
    }

    public Parser(Context context) {
        this.context = context;
    }

    public Executable parse(String commandline)
            throws ProtocolException {
        List<String> cmdline = tokenize(commandline);

        Command command;

        try {
            switch (COMMAND.valueOf(cmdline.get(0).toUpperCase())) {
                case COMMAND_LIST_BEGIN:
                    if (commandList != null)
                        throw new IllegalArgumentException();

                    commandList = new CommandList(CommandList.MODE.LIST);
                    return new Connection.Null();
                case COMMAND_LIST_OK_BEGIN:
                    if (commandList != null)
                        throw new IllegalArgumentException();

                    commandList = new CommandList(CommandList.MODE.LIST_OK);
                    return new Connection.Null();
                case COMMAND_LIST_END:
                    if (commandList == null)
                        throw new IllegalArgumentException();

                    Executable list = commandList;
                    commandList = null;
                    return list;
                default:
                    command = toCommand(cmdline);
            }
        } catch (ProtocolException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            // Unknown command
            command = new Meta.DelayedException(
                    new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, cmdline.get(0),
                            String.format(context.getString(R.string.proto_error_command_unknown), cmdline.get(0))));
        }

        if (commandList == null) {
            CommandList singleList = new CommandList(CommandList.MODE.LIST);
            singleList.add(command);
            return singleList;
        } else {
            commandList.add(command);
            return new Connection.Null();
        }
    }

    public Command toCommand(List<String> cmdline)
            throws ProtocolException {
        switch (COMMAND.valueOf(cmdline.get(0).toUpperCase())) {
            case CLOSE:
                return new Connection.Close();
            case CONSUME:
                return new PlaybackOptions.Consume();
            case CURRENTSONG:
                return new PlaybackStatus.CurrentSong(cmdline);
            case DEBUG:
                return new Meta.Debug();
            case NEXT:
                return new PlaybackControl.Next(cmdline);
            case PASSWORD:
                return new Connection.Password(cmdline);
            case PAUSE:
                return new PlaybackControl.Pause(cmdline);
            case PING:
                return new Connection.Ping(cmdline);
            case PREVIOUS:
                return new PlaybackControl.Previous(cmdline);
            case SETVOL:
            case VOLUME:
                return new PlaybackOptions.Volume(cmdline);
            case STATUS:
                return new PlaybackStatus.Status(cmdline);
            default:
                throw new ProtocolException(ProtocolException.ACK_ERROR_UNKNOWN, cmdline.get(0),
                        context.getString(R.string.proto_error_command_implemented));
        }
    }

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
}
