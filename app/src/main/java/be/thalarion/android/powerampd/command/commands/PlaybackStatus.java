package be.thalarion.android.powerampd.command.commands;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.util.List;

import be.thalarion.android.powerampd.state.SystemState;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.command.State;
import be.thalarion.android.powerampd.protocol.Permission;
import be.thalarion.android.powerampd.protocol.ProtocolException;
import be.thalarion.android.powerampd.protocol.ProtocolMessage;

public class PlaybackStatus {

    public static class CurrentSong extends Command {
        public CurrentSong(List<String> cmdline) { super(cmdline, Permission.PERMISSION_READ); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 0);
            state.send(new ProtocolMessage(String.format("Title: %s",
                    SystemState.getTrack().getString(PowerampAPI.Track.TITLE))));
        }
    }

    public static class Status extends Command {
        public Status(List<String> cmdline) { super(cmdline, Permission.PERMISSION_READ); }

        @Override
        public void executeCommand(State state)
                throws ProtocolException {
            checkArguments(0, 0);
            state.send(new ProtocolMessage(String.format("volume: %d", Math.round(SystemState.getVolume(state.getContext())))));
            state.send(new ProtocolMessage(String.format("repeat: %d", (SystemState.getRepeat() ? 1 : 0))));
            state.send(new ProtocolMessage(String.format("random: %d", (SystemState.getRandom() ? 1 : 0))));
            state.send(new ProtocolMessage(String.format("single: %d", (SystemState.getSingle() ? 1 : 0))));
            // Consume mode is not supported in Poweramp
            state.send(new ProtocolMessage(String.format("consume: %d", 0)));
            state.send(new ProtocolMessage(String.format("playlist: %d", 0)));
            state.send(new ProtocolMessage(String.format("playlistlength: %d", 0)));
            state.send(new ProtocolMessage(String.format("mixrampdb: %d", 0)));
            state.send(new ProtocolMessage(String.format("state: %s", SystemState.getState(state.getContext()))));
            state.send(new ProtocolMessage(String.format("song: %s", 0)));
            state.send(new ProtocolMessage(String.format("songid: %s", 0)));
            state.send(new ProtocolMessage(String.format("nextsong: %s", 0)));
            state.send(new ProtocolMessage(String.format("nextsongid: %s", 0)));
        }
    }
}
