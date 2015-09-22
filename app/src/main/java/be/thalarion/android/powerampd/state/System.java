package be.thalarion.android.powerampd.state;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.maxmpz.poweramp.player.PowerampAPI;

import be.thalarion.android.powerampd.protocol.ProtocolException;

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

    public static ProtocolException error;

    // Single mode
    protected static boolean single;
    protected static BroadcastReceiver stopBroadcastReceiver = new BroadcastReceiver() {

        private long trackId = -1;

        @Override
        public void onReceive(Context context, Intent intent) {
            long newTrackId = trackIntent.getBundleExtra(PowerampAPI.TRACK).getLong(PowerampAPI.Track.REAL_ID);
            Log.i("stopBroadcastReceiver", String.format("%d -> %d", trackId, newTrackId));
            if (trackId == -1) {
                Log.i("stopBroadcastReceiver", "New track");
            } else if (trackId != newTrackId) {
                // Track ID changed, so we must stop now.
                Log.i("stopBroadcastReceiver", "Stopping playback");
                PlaybackControl.stop(context);
            }
            trackId = newTrackId;
        }
    };

    public static Bundle getTrack() {
        return trackIntent.getBundleExtra(PowerampAPI.TRACK);
    }
}
