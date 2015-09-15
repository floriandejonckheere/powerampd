package be.thalarion.android.powerampd.command.commands;

import java.util.List;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.SystemState;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;

public class PlaybackOptions {

    public static class Consume extends Command {

        public Consume(List<String> cmdline) throws ProtocolException { super(cmdline, Permission.PERMISSION_CONTROL, 1, 1); }
        @Override
        public void executeCommand(State state) throws ProtocolException {
            throw new ProtocolException(
                    ProtocolException.ACK_ERROR_SYSTEM,
                    cmdline.get(0),
                    state.context.getString(R.string.proto_error_consume));
        }
    }

    public static class Volume extends Command {
        public Volume(List<String> cmdline) throws ProtocolException { super(cmdline, Permission.PERMISSION_NONE, 1, 1); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            try {
                int volume = Integer.parseInt(cmdline.get(1));
                if (volume > 100)
                    throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                            state.context.getString(R.string.proto_error_volume_invalid));

                if (volume < 0)
                    throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                            String.format(state.context.getString(R.string.proto_error_volume_integer), cmdline.get(1)));
                SystemState.setVolume(state.context, volume);
            } catch (NumberFormatException e) {
                throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                        String.format(state.context.getString(R.string.proto_error_volume_integer), cmdline.get(1)));
            }
        }
    }
}
