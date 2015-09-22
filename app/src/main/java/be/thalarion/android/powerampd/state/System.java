package be.thalarion.android.powerampd.state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.maxmpz.poweramp.player.PowerampAPI;

/**
 * Resource state of system
 */
public class System {

    /**
     * Poweramp state
     */
    public static Intent trackIntent;
    public static Intent statusIntent;
    public static Intent playingModeIntent;

    // Single mode
    protected static boolean single;
    protected static BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {

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
                PlaybackControl.stop(context);
            }
        }
    };

    public static Bundle getTrack() {
        return trackIntent.getBundleExtra(PowerampAPI.TRACK);
    }
}
