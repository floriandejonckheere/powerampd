package be.thalarion.android.powerampd.state;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

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

    public static Bundle getTrack() {
        return trackIntent.getBundleExtra(PowerampAPI.TRACK);
    }

    public static boolean getRepeat() {
        switch (playingModeIntent.getIntExtra(PowerampAPI.REPEAT, -1)) {
            case PowerampAPI.RepeatMode.REPEAT_NONE:
                return false;
            default:
                return true;
        }
    }

    public static boolean getRandom() {
        switch (playingModeIntent.getIntExtra(PowerampAPI.SHUFFLE, -1)) {
            case PowerampAPI.ShuffleMode.SHUFFLE_NONE:
                return false;
            default:
                return true;
        }
    }

    public static void setRandom(Context context, boolean shuffle) {
        Intent intent = new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.SHUFFLE);
        if (shuffle) {
            String mode = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("pref_shuffle", context.getString(R.string.pref_shuffle_default));

            int shuffleMode;
            if (mode.equals("SHUFFLE_ALL")) {
                shuffleMode = PowerampAPI.ShuffleMode.SHUFFLE_ALL;
            } else if (mode.equals("SHUFFLE_SONGS")) {
                shuffleMode = PowerampAPI.ShuffleMode.SHUFFLE_SONGS;
            } else if (mode.equals("SHUFFLE_CATS")) {
                shuffleMode = PowerampAPI.ShuffleMode.SHUFFLE_CATS;
            } else {
                shuffleMode = PowerampAPI.ShuffleMode.SHUFFLE_SONGS_AND_CATS;
            }
            intent.putExtra(PowerampAPI.SHUFFLE, shuffleMode);
        } else {
            intent.putExtra(PowerampAPI.SHUFFLE, PowerampAPI.ShuffleMode.SHUFFLE_NONE);
        }
        context.startService(intent);
    }

    public static int getSingle() {
        if (playingModeIntent.getIntExtra(PowerampAPI.REPEAT, -1) == PowerampAPI.RepeatMode.REPEAT_SONG)
            return 1;
        return 0;
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
     * System actions
     */

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
