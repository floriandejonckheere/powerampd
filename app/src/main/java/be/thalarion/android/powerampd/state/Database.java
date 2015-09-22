package be.thalarion.android.powerampd.state;

import android.content.Context;
import android.content.Intent;

import com.maxmpz.poweramp.player.PowerampAPI;

public class Database {

    /**
     * Poweramp is scanning files and tags
     * Set in DaemonService.scanningBroadcastReceiver
     */
    public static boolean scanning = false;

    /**
     * MPD scan job ID
     */
    public static int scanQueue = 0;

    /**
     * update - Updates the music database: find new files, remove deleted files, update modified files
     * @param context
     * @return job number
     */
    public static int update(Context context) {
        Intent intent = new Intent(PowerampAPI.Scanner.ACTION_SCAN_DIRS);
        intent.putExtra("fastScan", true);
        intent.putExtra("fullRescan", true);

        return ++scanQueue;
    }

    /**
     * rescan - Same as update, but also rescans unmodified files
     * @param context
     * @return job number
     */
    public static int rescan(Context context) {
        Intent intent = new Intent(PowerampAPI.Scanner.ACTION_SCAN_DIRS);
        intent.putExtra("fullRescan", true);

        return ++scanQueue;
    }

}
