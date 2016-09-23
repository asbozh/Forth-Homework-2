package com.asbozh.forthhomework2;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import java.sql.Time;

public class BatteryLevelService extends Service {

    int initialPercentage, currentPercentage, droppedPercentage;
    String result;
    int initialHour, hour;

    BatteryLevelReceiver mReceiver;

    private final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        BatteryLevelService getService() {
            return BatteryLevelService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialPercentage = -1;
        result = "Not enough data";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mReceiver = new BatteryLevelReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver, filter);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setPercentage(int percentage) {
        if (initialPercentage == -1) {
            initialPercentage = percentage;
            initialHour = new Time(System.currentTimeMillis()).getHours();
            setTextView(-1);
        } else {
            currentPercentage = percentage;
            droppedPercentage = initialPercentage - currentPercentage;
            hour = new Time(System.currentTimeMillis()).getHours();
            if (hour - initialHour == 1) {
                initialPercentage = -1;
                setTextView(initialPercentage);
            } else {
                setTextView(droppedPercentage);
            }

        }
    }

    public void setTextView(int i) {
        if (i == -1) {
            result = "Not enough data";
        } else {
            result = "In the last hour battery has dropped with " + i + "%";
        }
    }

    public String getTextView() {
        return result;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            initialPercentage = -1;
            result = "Not enough data";
        }

    }
}
