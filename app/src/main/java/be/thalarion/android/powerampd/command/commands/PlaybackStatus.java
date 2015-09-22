package be.thalarion.android.powerampd.command.commands;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.util.List;

import be.thalarion.android.powerampd.protocol.*;
import be.thalarion.android.powerampd.state.PlaybackOptions;
import be.thalarion.android.powerampd.state.System;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.protocol.Connection;
import be.thalarion.android.powerampd.state.Database;

public class PlaybackStatus {

    public static class CurrentSong extends Command {
        public CurrentSong(List<String> cmdline) { super(cmdline, Permission.PERMISSION_READ); }

        @Override
        public void executeCommand(Connection conn)
                throws ProtocolException {
            checkArguments(0, 0);
            conn.send(new ProtocolMessage(String.format("Title: %s",
                    System.getTrack().getString(PowerampAPI.Track.TITLE))));
        }
    }

    public static class Status extends Command {
        public Status(List<String> cmdline) { super(cmdline, Permission.PERMISSION_READ); }

        @Override
        public void executeCommand(be.thalarion.android.powerampd.protocol.Connection conn)
                throws ProtocolException {
            checkArguments(0, 0);
            conn.send(new ProtocolMessage(String.format("volume: %d", Math.round(PlaybackOptions.getVolume(conn.getContext())))));
            conn.send(new ProtocolMessage(String.format("repeat: %d", (PlaybackOptions.getRepeat() ? 1 : 0))));
            conn.send(new ProtocolMessage(String.format("random: %d", (PlaybackOptions.getRandom() ? 1 : 0))));
            // Single mode is currently not supported
            conn.send(new ProtocolMessage(String.format("single: %d", 0)));
//            conn.send(new ProtocolMessage(String.format("single: %d", (PlaybackOptions.getSingle() ? 1 : 0))));
            // Consume mode is not supported in Poweramp
            conn.send(new ProtocolMessage(String.format("consume: %d", 0)));
            conn.send(new ProtocolMessage(String.format("playlist: %d", 0)));
            conn.send(new ProtocolMessage(String.format("playlistlength: %d", 0)));
            conn.send(new ProtocolMessage(String.format("state: %s", be.thalarion.android.powerampd.state.PlaybackStatus.getState(conn.getContext()))));
            conn.send(new ProtocolMessage(String.format("song: %s", 0)));
            conn.send(new ProtocolMessage(String.format("songid: %s", 0)));
            conn.send(new ProtocolMessage(String.format("nextsong: %s", 0)));
            conn.send(new ProtocolMessage(String.format("nextsongid: %s", 0)));
            conn.send(new ProtocolMessage(String.format("time: %s", 0)));
            conn.send(new ProtocolMessage(String.format("elapsed: %s", 0)));
            conn.send(new ProtocolMessage(String.format("duration: %s", 0)));
            conn.send(new ProtocolMessage(String.format("bitrate: %s", 0)));

            // No API support for crossfade
//            conn.send(new ProtocolMessage(String.format("xfade: %s", PlaybackOptions.getCrossfade(conn.getContext()))));
            conn.send(new ProtocolMessage(String.format("mixrampdb: %d", 0)));
            conn.send(new ProtocolMessage(String.format("mixrampdelay: %d", 0)));
            conn.send(new ProtocolMessage(String.format("audio: %d", 0)));

            if (Database.scanning)
                conn.send(new ProtocolMessage(String.format("updating_db: %d", Database.scanQueue)));

            if (System.error != null)
                conn.send(new ProtocolMessage(String.format("error: %s", System.error.getMessage())));
        }
    }
}
