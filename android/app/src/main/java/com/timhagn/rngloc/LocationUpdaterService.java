package com.timhagn.rngloc;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by quatrix on 6/7/16.
 */
public class LocationUpdaterService extends Service {
    public static final int MINUTE = 1000 * 60;
    private static LocationProvider locationProvider;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (locationProvider != null) {
            locationProvider.requestLocation();
        }
        stopSelf();
        return START_NOT_STICKY;
    }

    public static void setCallback(LocationProvider lp) {
        locationProvider = lp;
    }

    @Override
    public void onDestroy() {
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
            alarm.RTC_WAKEUP,
            System.currentTimeMillis() + MINUTE,
            PendingIntent.getService(this, 0, new Intent(this, LocationUpdaterService.class), 0)
        );
    }
}
