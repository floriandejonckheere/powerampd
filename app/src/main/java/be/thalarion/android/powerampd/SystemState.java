package be.thalarion.android.powerampd;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import com.maxmpz.poweramp.player.PowerampAPI;

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

    public static int getRepeat() {
        switch (playingModeIntent.getIntExtra(PowerampAPI.REPEAT, -1)) {
            case PowerampAPI.RepeatMode.REPEAT_NONE:
                return 0;
            default:
                return 1;
        }
    }

    public static int getShuffle() {
        switch (playingModeIntent.getIntExtra(PowerampAPI.SHUFFLE, -1)) {
            case PowerampAPI.ShuffleMode.SHUFFLE_NONE:
                return 0;
            default:
                return 1;
        }
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
