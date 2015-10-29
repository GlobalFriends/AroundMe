package testing;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;

import java.util.logging.Handler;

import Logging.Logger;

/**
 * Created by vishal on 10/27/2015.
 */
public class LocationMessengerService extends Service {
    private static final String TAG = "LocationMessengerService";
    public static final int MESSAGE_INIT = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    class LocationHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MESSAGE_INIT:
                    Logger.i(TAG, "MESSAGE_INIT");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }


    Messenger mLocationMessagenger = new Messenger(new LocationHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocationMessagenger.getBinder();
    }
}
