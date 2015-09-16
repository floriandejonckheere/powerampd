package be.thalarion.android.powerampd.command.commands;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.util.List;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public class PlaybackControl {

    public static class Next extends Command {
        public Next(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 0);
            state.command(PowerampAPI.Commands.NEXT);
        }
    }

    public static class Pause extends Command {
        public Pause(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 1);
            if (cmdline.size() > 1) {
                if (cmdline.get(1).equals("0")) {
                    state.command(PowerampAPI.Commands.RESUME);
                } else if (cmdline.get(1).equals("1")) {
                    state.command(PowerampAPI.Commands.PAUSE);
                } else
                    throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                            String.format(state.getContext().getString(R.string.proto_error_pause_arg), cmdline.get(1)));
            } else state.command(PowerampAPI.Commands.TOGGLE_PLAY_PAUSE);
        }
    }

    public static class Previous extends Command {
        public Previous(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 0);
            state.command(PowerampAPI.Commands.PREVIOUS);
        }
    }

    public static class Stop extends Command {
        public Stop(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 0);
            state.command(PowerampAPI.Commands.STOP);
        }
    }

}
