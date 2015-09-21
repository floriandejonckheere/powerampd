package be.thalarion.android.powerampd.state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.maxmpz.poweramp.player.PowerampAPI;

import be.thalarion.android.powerampd.R;

/**
 * Resource state of system
 */
public class SystemState {

    /**
     * Poweramp state
     */
    public static Intent trackIntent;
    public static Intent statusIntent;
    public static Intent playingModeIntent;

    // Single mode
    private static boolean single;
    private static BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {

        Long trackID;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("stopBroadcastReceiver", String.format("%d", intent.getBundleExtra(PowerampAPI.TRACK).getLong(PowerampAPI.Track.ID)));
            Long newTrackID = trackIntent.getBundleExtra(PowerampAPI.TRACK).getLong(PowerampAPI.Track.ID);
            if (this.trackID == null) {
                this.trackID = newTrackID;
            } else if (!this.trackID.equals(newTrackID)) {
                // Track ID changed, so we must stop now.
                this.trackID = newTrackID;
                stop(context);
            }
        }
    };

    public static Bundle getTrack() {
        return trackIntent.getBundleExtra(PowerampAPI.TRACK);
    }

    public static boolean getRepeat() {
        return !(playingModeIntent.getIntExtra(PowerampAPI.REPEAT, -1) == PowerampAPI.RepeatMode.REPEAT_NONE);
    }

    public static void setRepeat(Context context, int mode) {
        Intent intent = new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.REPEAT)
                .putExtra(PowerampAPI.REPEAT, mode);
        context.startService(intent);
    }

    public static boolean getRandom() {
        return !(playingModeIntent.getIntExtra(PowerampAPI.SHUFFLE, -1) == PowerampAPI.ShuffleMode.SHUFFLE_NONE);
    }

    public static void setRandom(Context context, int mode) {
        Intent intent = new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.SHUFFLE)
                .putExtra(PowerampAPI.SHUFFLE, mode);
        context.startService(intent);
    }

    public static boolean getSingle() {
        /**
         * Single mode is enabled on two conditions:
         * 1. if repeat mode is REPEAT_SONG
         * 2. if the single boolean is true
         */
        return (single || playingModeIntent.getIntExtra(PowerampAPI.REPEAT, -1) == PowerampAPI.RepeatMode.REPEAT_SONG);
    }

    public static void setSingle(Context context, boolean single) {
        Log.i("powerampd", String.format("setSingle(%s)", Boolean.valueOf(single).toString()));

        if (single) {
            if (getRepeat()) {
                setRepeat(context, PowerampAPI.RepeatMode.REPEAT_SONG);
            } else {
                Log.i("powerampd", "Registering stopBroadcastReceiver");
                context.registerReceiver(stopBroadcastReceiver, new IntentFilter(PowerampAPI.ACTION_TRACK_CHANGED));
            }
        } else {
            if (getRepeat()) {
                setRepeat(context, PreferenceManager.getDefaultSharedPreferences(context).getInt("pref_repeat", Integer.valueOf(context.getString(R.string.pref_repeat))));
            } else {
                Log.i("powerampd", "Unregistering stopBroadcastReceiver");
                context.unregisterReceiver(stopBroadcastReceiver);
            }
        }
        SystemState.single = single;
    }

    public static void stop(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.STOP));
    }

    public static void previous(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.PREVIOUS));
    }

    public static void next(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.NEXT));
    }

    public static void pause(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.PAUSE));
    }

    public static void resume(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.RESUME));
    }

    public static void toggle(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.TOGGLE_PLAY_PAUSE));
    }

    public static String getState(Context context) {
        if (statusIntent.getIntExtra(PowerampAPI.STATUS, -1) == PowerampAPI.Status.TRACK_PLAYING) {
            if (statusIntent.getBooleanExtra("paused", false))
                return context.getString(R.string.proto_status_pause);
            return context.getString(R.string.proto_status_play);
        } else return context.getString(R.string.proto_status_stop);
    }


    /**
     * System state
     */

    /**
     * getVolume
     * @param context
     * @return volume in percent
     */
    public static double getVolume(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return ((double) audio.getStreamVolume(AudioManager.STREAM_MUSIC) / audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) * 100;
    }

    /**
     * setVolume
     * @param context
     * @param volume volume in percent
     */
    public static void setVolume(Context context, double volume) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Long absVolume = Math.round((volume / 100) * audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, absVolume.intValue(), AudioManager.FLAG_SHOW_UI);
    }
}
