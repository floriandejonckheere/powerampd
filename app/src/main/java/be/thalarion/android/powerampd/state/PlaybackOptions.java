package be.thalarion.android.powerampd.state;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.maxmpz.poweramp.player.PowerampAPI;

import be.thalarion.android.powerampd.R;

public class PlaybackOptions {
    /**
     * getRepeat - Playback repeat status
     * @return boolean
     */
    public static boolean getRepeat() {
        return !(System.playingModeIntent.getIntExtra(PowerampAPI.REPEAT, -1) == PowerampAPI.RepeatMode.REPEAT_NONE);
    }

    /**
     * setRepeat - Set playback repeat mode
     * @param context
     * @param mode PowerampAPI.RepeatMode
     */
    public static void setRepeat(Context context, int mode) {
        Intent intent = new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.REPEAT)
                .putExtra(PowerampAPI.REPEAT, mode);
        context.startService(intent);
    }

    /**
     * getRandom - Playback random mode
     * @return
     */
    public static boolean getRandom() {
        return !(System.playingModeIntent.getIntExtra(PowerampAPI.SHUFFLE, -1) == PowerampAPI.ShuffleMode.SHUFFLE_NONE);
    }

    /**
     * setRandom - Set playback random mode
     * @param context
     * @param mode PowerampAPI.ShuffleMode
     */
    public static void setRandom(Context context, int mode) {
        Intent intent = new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.SHUFFLE)
                .putExtra(PowerampAPI.SHUFFLE, mode);
        context.startService(intent);
    }

    /**
     * getSingle - Get single playback status
     * @return
     */
    public static boolean getSingle() {
        /**
         * Single mode is enabled on two conditions:
         * 1. if repeat mode is REPEAT_SONG
         * 2. if the single boolean is true
         */
        return (System.single || System.playingModeIntent.getIntExtra(PowerampAPI.REPEAT, -1) == PowerampAPI.RepeatMode.REPEAT_SONG);
    }

    /**
     * setSingle - Set single playback
     * @param context
     * @param single
     */
    public static void setSingle(Context context, boolean single) {
        Log.i("powerampd", String.format("setSingle(%s)", Boolean.valueOf(single).toString()));

        if (single) {
            if (getRepeat()) {
                setRepeat(context, PowerampAPI.RepeatMode.REPEAT_SONG);
            } else {
                Log.i("powerampd", "Registering stopBroadcastReceiver");
                context.registerReceiver(System.stopBroadcastReceiver, new IntentFilter(PowerampAPI.ACTION_TRACK_CHANGED));
            }
        } else {
            if (getRepeat()) {
                setRepeat(context, PreferenceManager.getDefaultSharedPreferences(context).getInt("pref_repeat", Integer.valueOf(context.getString(R.string.pref_repeat))));
            } else {
                Log.i("powerampd", "Unregistering stopBroadcastReceiver");
                context.unregisterReceiver(System.stopBroadcastReceiver);
            }
        }
        System.single = single;
    }

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
