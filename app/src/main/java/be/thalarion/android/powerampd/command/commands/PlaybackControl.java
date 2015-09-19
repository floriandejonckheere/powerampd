package be.thalarion.android.powerampd.command.commands;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.util.List;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.state.SystemState;

public class PlaybackControl {

    public static class Next extends Command {
        public Next(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 0);
            SystemState.next(state.getContext());
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
                    SystemState.resume(state.getContext());
                } else if (cmdline.get(1).equals("1")) {
                    SystemState.pause(state.getContext());
                } else
                    throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                            state.getContext().getString(R.string.proto_error_arg_boolean, cmdline.get(1)));
            } else SystemState.toggle(state.getContext());
        }
    }

    public static class Previous extends Command {
        public Previous(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 0);
            SystemState.previous(state.getContext());
        }
    }

    public static class Stop extends Command {
        public Stop(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 0);
            SystemState.stop(state.getContext());
        }
    }

}
