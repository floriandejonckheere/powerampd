package be.thalarion.android.powerampd.command.commands;

import android.preference.PreferenceManager;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.util.List;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.state.SystemState;

public class PlaybackOptions {

    public static class Random extends Command {
        public Random(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            checkArguments(1, 1);
            boolean random = getBoolean(1);

            if (random) {
                String mode = PreferenceManager.getDefaultSharedPreferences(state.getContext())
                        .getString("pref_shuffle", state.getContext().getString(R.string.pref_shuffle_default));

                if (mode.equals("SHUFFLE_ALL")) {
                    SystemState.setRandom(state.getContext(), PowerampAPI.ShuffleMode.SHUFFLE_ALL);
                } else if (mode.equals("SHUFFLE_SONGS")) {
                    SystemState.setRandom(state.getContext(), PowerampAPI.ShuffleMode.SHUFFLE_SONGS);
                } else if (mode.equals("SHUFFLE_CATS")) {
                    SystemState.setRandom(state.getContext(), PowerampAPI.ShuffleMode.SHUFFLE_CATS);
                } else {
                    SystemState.setRandom(state.getContext(), PowerampAPI.ShuffleMode.SHUFFLE_SONGS_AND_CATS);
                }
            } else {
                SystemState.setRandom(state.getContext(), PowerampAPI.ShuffleMode.SHUFFLE_NONE);
            }
        }
    }

    public static class Repeat extends Command {
        public Repeat(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            checkArguments(1, 1);
            boolean repeat = getBoolean(1);
            boolean single = SystemState.getSingle();

            if (repeat) {
                if (single) {
                    // Repeat, single -> repeat single song
                    SystemState.setRepeat(state.getContext(), PowerampAPI.RepeatMode.REPEAT_SONG);
                } else {
                    // Repeat, no single -> user defined repeat mode
                    String repeatPreference = PreferenceManager.getDefaultSharedPreferences(state.getContext())
                            .getString("pref_repeat", state.getContext().getString(R.string.pref_repeat_default));
                    if (repeatPreference.equals("REPEAT_ON")) {
                        SystemState.setRepeat(state.getContext(), PowerampAPI.RepeatMode.REPEAT_ON);
                    } else SystemState.setRepeat(state.getContext(), PowerampAPI.RepeatMode.REPEAT_ADVANCE);
                }
            } else {
                // No repeat, no single -> no repeat
                SystemState.setRepeat(state.getContext(), PowerampAPI.RepeatMode.REPEAT_NONE);
                // No repeat, single -> play single song and stop
                SystemState.setSingle(state.getContext(), single);
            }
        }
    }

    public static class Single extends Command {
        public Single(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            checkArguments(1, 1);
            boolean repeat = SystemState.getRepeat();
            boolean single = getBoolean(1);

            if (repeat) {
                if (single) {
                    // Repeat, single -> repeat single song
                    SystemState.setRepeat(state.getContext(), PowerampAPI.RepeatMode.REPEAT_SONG);
                } else {
                    // Repeat, no single -> user defined repeat mode
                    String repeatPreference = PreferenceManager.getDefaultSharedPreferences(state.getContext())
                            .getString("pref_repeat", state.getContext().getString(R.string.pref_repeat_default));
                    if (repeatPreference.equals("REPEAT_ON")) {
                        SystemState.setRepeat(state.getContext(), PowerampAPI.RepeatMode.REPEAT_ON);
                    } else SystemState.setRepeat(state.getContext(), PowerampAPI.RepeatMode.REPEAT_ADVANCE);
                }
            } else {
                // No repeat, no single -> no repeat
                SystemState.setRepeat(state.getContext(), PowerampAPI.RepeatMode.REPEAT_NONE);
                // No repeat, single -> play single song and stop
                SystemState.setSingle(state.getContext(), single);
            }
        }
    }

    public static class Consume extends Command {

        public Consume() { super(null, Permission.PERMISSION_CONTROL); }
        @Override
        public void executeCommand(State state) throws ProtocolException {
            throw new ProtocolException(
                    ProtocolException.ACK_ERROR_SYSTEM,
                    cmdline.get(0),
                    state.getContext().getString(R.string.proto_error_consume));
        }
    }

    public static class Volume extends Command {
        public Volume(List<String> cmdline) { super(cmdline, Permission.PERMISSION_NONE); }

        @Override
        public void executeCommand(State state) throws ProtocolException {
            checkArguments(1, 1);
            try {
                int volume = Integer.parseInt(cmdline.get(1));
                if (volume > 100)
                    throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                            state.getContext().getString(R.string.proto_error_volume_invalid));

                if (volume < 0)
                    throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                            state.getContext().getString(R.string.proto_error_volume_integer, cmdline.get(1)));
                SystemState.setVolume(state.getContext(), volume);
            } catch (NumberFormatException e) {
                throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                        state.getContext().getString(R.string.proto_error_volume_integer, cmdline.get(1)));
            }
        }
    }
}
