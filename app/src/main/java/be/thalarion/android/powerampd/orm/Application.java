package be.thalarion.android.powerampd.orm;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Database.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Database.terminate();
    }
}
