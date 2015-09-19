package be.thalarion.android.powerampd.state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private static boolean single;
    private static BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(PowerampAPI.STATUS, -1);
            if (status == PowerampAPI.Status.TRACK_ENDED)
                stop(context);
        }
    };

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

    public static void setRepeat(Context context, int mode) {
        Intent intent = new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.REPEAT)
                .putExtra(PowerampAPI.REPEAT, mode);
        context.startService(intent);
    }

    public static boolean getRandom() {
        switch (playingModeIntent.getIntExtra(PowerampAPI.SHUFFLE, -1)) {
            case PowerampAPI.ShuffleMode.SHUFFLE_NONE:
                return false;
            default:
                return true;
        }
    }

    public static void setRandom(Context context, int mode) {
        Intent intent = new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.SHUFFLE)
                .putExtra(PowerampAPI.SHUFFLE, mode);
        context.startService(intent);
    }

    public static boolean getSingle() {
        // REPEAT_SONG <=> single = 1
        if (single || playingModeIntent.getIntExtra(PowerampAPI.REPEAT, -1) == PowerampAPI.RepeatMode.REPEAT_SONG)
            return true;

        return false;
    }

    public static void setSingle(Context context, boolean single) {
        SystemState.single = single;
        if (single) {
            context.registerReceiver(stopBroadcastReceiver, new IntentFilter(PowerampAPI.ACTION_STATUS_CHANGED));
        } else context.unregisterReceiver(stopBroadcastReceiver);
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
