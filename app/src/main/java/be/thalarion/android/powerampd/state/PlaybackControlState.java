package be.thalarion.android.powerampd.state;

import android.content.Context;
import android.content.Intent;

import com.maxmpz.poweramp.player.PowerampAPI;

public class PlaybackControlState {

    /**
     * stop - Stop playback
     * @param context
     */
    public static void stop(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.STOP));
    }

    /**
     * previous - Previous song
     * @param context
     */
    public static void previous(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.PREVIOUS));
    }

    /**
     * next - Next song
     * @param context
     */
    public static void next(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.NEXT));
    }

    /**
     * pause - Pause playback
     * @param context
     */
    public static void pause(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.PAUSE));
    }

    /**
     * resume - Resume playback
     * @param context
     */
    public static void resume(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.RESUME));
    }

    /**
     * toggle - Toggle play/pause
     * @param context
     */
    public static void toggle(Context context) {
        context.startService(new Intent(PowerampAPI.ACTION_API_COMMAND)
                .putExtra(PowerampAPI.COMMAND, PowerampAPI.Commands.TOGGLE_PLAY_PAUSE));
    }
}
