package be.thalarion.android.powerampd.command.commands;

import android.net.Uri;
import android.os.Bundle;

import com.maxmpz.poweramp.player.PowerampAPI;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.List;

import be.thalarion.android.powerampd.protocol.*;
import be.thalarion.android.powerampd.state.PlaybackOptions;
import be.thalarion.android.powerampd.state.System;
import be.thalarion.android.powerampd.command.Command;
import be.thalarion.android.powerampd.protocol.Connection;
import be.thalarion.android.powerampd.state.Database;
import wseemann.media.FFmpegMediaMetadataRetriever;
import wseemann.media.Metadata;

public class PlaybackStatus {

    public static class CurrentSong extends Command {
        public CurrentSong(List<String> cmdline) { super(cmdline, Permission.PERMISSION_READ); }

        @Override
        public void executeCommand(Connection conn)
                throws ProtocolException {
            checkArguments(0, 0);
            Bundle trackInfo = System.getTrack();

            File file = new File(trackInfo.getString(PowerampAPI.Track.PATH));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

            FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
            mmr.setDataSource(trackInfo.getString(PowerampAPI.Track.PATH));
            String artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
            String composer = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_COMPOSER);
            String genre = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_GENRE);
            String date = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DATE);
            String track = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TRACK);
            String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
            String albumArtist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM_ARTIST);
            String performer = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_PERFORMER);
            String disc = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DISC);
            String comment = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_COMMENT);
            String duration = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
            String title = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);

            conn.print(String.format("file: %s", file.getName()));
            conn.print(String.format("Last-Modified: %s", format.format(file.lastModified())));
            if (artist != null)     conn.print(String.format("Artist: %s", artist));
            if (albumArtist != null)conn.print(String.format("AlbumArtist: %s", albumArtist));
            if (title != null)      conn.print(String.format("Title: %s", title));
            if (album != null)      conn.print(String.format("Album: %s", album));
            if (track != null)      conn.print(String.format("Track: %s", track));
            if (date != null)       conn.print(String.format("Date: %s", date));
            if (genre != null)      conn.print(String.format("Genre: %s", genre));
            if (composer != null)   conn.print(String.format("Composer: %s", composer));
            if (performer != null)  conn.print(String.format("Performer: %s", performer));
            if (disc != null)       conn.print(String.format("Disc: %s", disc));
            if (comment != null)    conn.print(String.format("Comment: %s", comment));

            conn.print(String.format("Time: %s", duration));
            conn.print(String.format("Pos: %d", trackInfo.getInt(PowerampAPI.Track.POS_IN_LIST)));
            conn.print(String.format("Id: %d", trackInfo.getLong(PowerampAPI.Track.REAL_ID)));

            mmr.release();
        }
    }

    public static class Status extends Command {
        public Status(List<String> cmdline) { super(cmdline, Permission.PERMISSION_READ); }

        @Override
        public void executeCommand(be.thalarion.android.powerampd.protocol.Connection conn)
                throws ProtocolException {
            checkArguments(0, 0);
            conn.print(String.format("volume: %d", Math.round(PlaybackOptions.getVolume(conn.getContext()))));
            conn.print(String.format("repeat: %d", (PlaybackOptions.getRepeat() ? 1 : 0)));
            conn.print(String.format("random: %d", (PlaybackOptions.getRandom() ? 1 : 0)));
            // Single mode is currently not supported
            conn.print(String.format("single: %d", 0));
//            conn.print(String.format("single: %d", (PlaybackOptions.getSingle() ? 1 : 0))));
            // Consume mode is not supported in Poweramp
            conn.print(String.format("consume: %d", 0));
            conn.print(String.format("playlist: %d", 0));
            conn.print(String.format("playlistlength: %d", 0));
            conn.print(String.format("state: %s", be.thalarion.android.powerampd.state.PlaybackStatus.getState(conn.getContext())));
            conn.print(String.format("song: %s", 0));
            conn.print(String.format("songid: %s", 0));
            conn.print(String.format("nextsong: %s", 0));
            conn.print(String.format("nextsongid: %s", 0));
            conn.print(String.format("time: %s", 0));
            conn.print(String.format("elapsed: %s", 0));
            conn.print(String.format("duration: %s", 0));
            conn.print(String.format("bitrate: %s", 0));

            // No API support for crossfade
//            conn.print(String.format("xfade: %s", PlaybackOptions.getCrossfade(conn.getContext()))));
            conn.print(String.format("mixrampdb: %d", 0));
            conn.print(String.format("mixrampdelay: %d", 0));
            conn.print(String.format("audio: %d", 0));

            if (Database.scanning)
                conn.print(String.format("updating_db: %d", Database.scanQueue));

            if (System.error != null)
                conn.print(String.format("error: %s", System.error.getMessage()));
        }
    }
}
