package testing;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.globalfriends.com.aroundme.logging.Logger;

/**
 * Created by vishal on 10/24/2015.
 */
public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private IBinder mBinder;

    public void test() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * class for client to access
     */
    public class ServiceBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }
    }
}
