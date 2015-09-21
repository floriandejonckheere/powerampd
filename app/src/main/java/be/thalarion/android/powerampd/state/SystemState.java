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
import be.thalarion.android.powerampd.command.commands.PlaybackControl;

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
                PlaybackControlState.stop(context);
            }
        }
    };

    public static Bundle getTrack() {
        return trackIntent.getBundleExtra(PowerampAPI.TRACK);
    }
}
