package be.thalarion.android.powerampd.state;

import android.content.Context;

import com.maxmpz.poweramp.player.PowerampAPI;

import be.thalarion.android.powerampd.R;

public class PlaybackStatusState {
    /**
     * getState - get Poweramp state
     * @param context
     * @return 'pause', 'play' or 'stop'
     */
    public static String getState(Context context) {
        if (SystemState.statusIntent.getIntExtra(PowerampAPI.STATUS, -1) == PowerampAPI.Status.TRACK_PLAYING) {
            if (SystemState.statusIntent.getBooleanExtra("paused", false))
                return context.getString(R.string.proto_status_pause);
            return context.getString(R.string.proto_status_play);
        } else return context.getString(R.string.proto_status_stop);
    }
}
