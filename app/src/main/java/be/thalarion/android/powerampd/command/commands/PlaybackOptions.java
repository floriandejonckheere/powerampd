package be.thalarion.android.powerampd.command.commands;

import android.preference.PreferenceManager;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.util.List;

import be.thalarion.android.powerampd.R;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.protocol.*;
import be.thalarion.android.powerampd.protocol.Connection;

public class PlaybackOptions {

    public static class Random extends Command {
        public Random(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(Connection conn) throws ProtocolException {
            checkArguments(1, 1);
            boolean random = getBoolean(1);

            if (random) {
                String mode = PreferenceManager.getDefaultSharedPreferences(conn.getContext())
                        .getString("pref_shuffle", conn.getContext().getString(R.string.pref_shuffle_default));

                if (mode.equals("SHUFFLE_ALL")) {
                    be.thalarion.android.powerampd.state.PlaybackOptions.setRandom(conn.getContext(), PowerampAPI.ShuffleMode.SHUFFLE_ALL);
                } else if (mode.equals("SHUFFLE_SONGS")) {
                    be.thalarion.android.powerampd.state.PlaybackOptions.setRandom(conn.getContext(), PowerampAPI.ShuffleMode.SHUFFLE_SONGS);
                } else if (mode.equals("SHUFFLE_CATS")) {
                    be.thalarion.android.powerampd.state.PlaybackOptions.setRandom(conn.getContext(), PowerampAPI.ShuffleMode.SHUFFLE_CATS);
                } else {
                    be.thalarion.android.powerampd.state.PlaybackOptions.setRandom(conn.getContext(), PowerampAPI.ShuffleMode.SHUFFLE_SONGS_AND_CATS);
                }
            } else {
                be.thalarion.android.powerampd.state.PlaybackOptions.setRandom(conn.getContext(), PowerampAPI.ShuffleMode.SHUFFLE_NONE);
            }
        }
    }

    public static class Repeat extends Command {
        public Repeat(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(Connection conn) throws ProtocolException {
            checkArguments(1, 1);
            boolean repeat = getBoolean(1);
            boolean single = be.thalarion.android.powerampd.state.PlaybackOptions.getSingle();

            if (repeat) {
                if (single) {
                    // Repeat, single -> repeat single song
                    be.thalarion.android.powerampd.state.PlaybackOptions.setRepeat(conn.getContext(), PowerampAPI.RepeatMode.REPEAT_SONG);
                } else {
                    // Repeat, no single -> user defined repeat mode
                    String repeatPreference = PreferenceManager.getDefaultSharedPreferences(conn.getContext())
                            .getString("pref_repeat", conn.getContext().getString(R.string.pref_repeat_default));
                    if (repeatPreference.equals("REPEAT_ON")) {
                        be.thalarion.android.powerampd.state.PlaybackOptions.setRepeat(conn.getContext(), PowerampAPI.RepeatMode.REPEAT_ON);
                    } else be.thalarion.android.powerampd.state.PlaybackOptions.setRepeat(conn.getContext(), PowerampAPI.RepeatMode.REPEAT_ADVANCE);
                }
            } else {
                // No repeat, no single -> no repeat
                be.thalarion.android.powerampd.state.PlaybackOptions.setRepeat(conn.getContext(), PowerampAPI.RepeatMode.REPEAT_NONE);
                // No repeat, single -> play single song and stop
                be.thalarion.android.powerampd.state.PlaybackOptions.setSingle(conn.getContext(), single);
            }
        }
    }

    public static class Single extends Command {
        public Single(List<String> cmdline) { super(cmdline, Permission.PERMISSION_CONTROL); }

        @Override
        public void executeCommand(be.thalarion.android.powerampd.protocol.Connection conn) throws ProtocolException {
            checkArguments(1, 1);
            boolean repeat = be.thalarion.android.powerampd.state.PlaybackOptions.getRepeat();
            boolean single = getBoolean(1);

            if (repeat) {
                be.thalarion.android.powerampd.state.PlaybackOptions.setSingle(conn.getContext(), false);
                if (single) {
                    // Repeat, single -> repeat single song
                    be.thalarion.android.powerampd.state.PlaybackOptions.setRepeat(conn.getContext(), PowerampAPI.RepeatMode.REPEAT_SONG);
                } else {
                    // Repeat, no single -> user defined repeat mode
                    String repeatPreference = PreferenceManager.getDefaultSharedPreferences(conn.getContext())
                            .getString("pref_repeat", conn.getContext().getString(R.string.pref_repeat_default));
                    if (repeatPreference.equals("REPEAT_ON")) {
                        be.thalarion.android.powerampd.state.PlaybackOptions.setRepeat(conn.getContext(), PowerampAPI.RepeatMode.REPEAT_ON);
                    } else be.thalarion.android.powerampd.state.PlaybackOptions.setRepeat(conn.getContext(), PowerampAPI.RepeatMode.REPEAT_ADVANCE);
                }
            } else {
                // No repeat, no single -> no repeat
                be.thalarion.android.powerampd.state.PlaybackOptions.setRepeat(conn.getContext(), PowerampAPI.RepeatMode.REPEAT_NONE);
                // No repeat, single -> play single song and stop
                be.thalarion.android.powerampd.state.PlaybackOptions.setSingle(conn.getContext(), single);
            }
        }
    }

    public static class Volume extends Command {
        public Volume(List<String> cmdline) { super(cmdline, Permission.PERMISSION_NONE); }

        @Override
        public void executeCommand(Connection conn) throws ProtocolException {
            checkArguments(1, 1);
            try {
                int volume = Integer.parseInt(cmdline.get(1));
                if (volume > 100)
                    throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                            conn.getContext().getString(R.string.proto_error_volume_invalid));

                if (volume < 0)
                    throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                            conn.getContext().getString(R.string.proto_error_volume_integer, cmdline.get(1)));
                be.thalarion.android.powerampd.state.PlaybackOptions.setVolume(conn.getContext(), volume);
            } catch (NumberFormatException e) {
                throw new ProtocolException(ProtocolException.ACK_ERROR_ARG, cmdline.get(0),
                        conn.getContext().getString(R.string.proto_error_volume_integer, cmdline.get(1)));
            }
        }
    }
}
