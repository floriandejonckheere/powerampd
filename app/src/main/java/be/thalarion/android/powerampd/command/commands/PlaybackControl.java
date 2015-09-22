package be.thalarion.android.powerampd.command.commands;

import java.util.List;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.protocol.*;
import be.thalarion.android.powerampd.protocol.Connection;

public class PlaybackControl {

    public static class Next extends Command {
        public Next(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(Connection conn)
                throws ProtocolException {
            checkArguments(0, 0);
            be.thalarion.android.powerampd.state.PlaybackControl.next(conn.getContext());
        }
    }

    public static class Pause extends Command {
        public Pause(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(be.thalarion.android.powerampd.protocol.Connection conn)
                throws ProtocolException {
            checkArguments(0, 1);
            if (cmdline.size() > 1) {
                if (cmdline.get(1).equals("0")) {
                    be.thalarion.android.powerampd.state.PlaybackControl.resume(conn.getContext());
                } else if (cmdline.get(1).equals("1")) {
                    be.thalarion.android.powerampd.state.PlaybackControl.pause(conn.getContext());
                } else
                    throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                            conn.getContext().getString(R.string.proto_error_arg_boolean, cmdline.get(1)));
            } else be.thalarion.android.powerampd.state.PlaybackControl.toggle(conn.getContext());
        }
    }

    public static class Previous extends Command {
        public Previous(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(Connection conn)
                throws ProtocolException {
            checkArguments(0, 0);
            be.thalarion.android.powerampd.state.PlaybackControl.previous(conn.getContext());
        }
    }

    public static class Stop extends Command {
        public Stop(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(Connection conn)
                throws ProtocolException {
            checkArguments(0, 0);
            be.thalarion.android.powerampd.state.PlaybackControl.stop(conn.getContext());
        }
    }

}
