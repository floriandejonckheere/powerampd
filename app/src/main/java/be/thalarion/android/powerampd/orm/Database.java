package be.thalarion.android.powerampd.orm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;

public class Database {

    private static Database instance;

    private SQLiteDatabase database;

    private Database(Context context) {
        String databaseName = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            databaseName = String.valueOf(ai.metaData.get("foo"));
        } catch (PackageManager.NameNotFoundException e) {
            databaseName = "database";
        }

        database = context.openOrCreateDatabase(databaseName, context.MODE_PRIVATE, null);

        database.execSQL("CREATE TABLE IF NOT EXISTS ORMDatabase (" +
                "version INTEGER NOT NULL," +
                "");
    }

    private void destroy() {
        if (database != null)
            database.close();
    }

    public static void init(Context context) {
        instance = new Database(context);
    }

    public static void terminate() {
        if (instance != null)
            instance.destroy();
    }

    public static Database getDatabase() {
        return instance;
    }

}
