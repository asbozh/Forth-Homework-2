package com.asbozh.forthhomework2;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    private Button mButtonStartService, mButtonStopService;
    private TextView mTextViewDisplay;
    Intent serviceIntent;
    final Context context = this;
    private BatteryLevelService mService;
    boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BatteryLevelService.LocalBinder binder = (BatteryLevelService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isMyServiceRunning()) {
            setContentView(R.layout.activity_main_running);
            setViews();
        } else {
            setInitialLayout();
        }
        BatteryLevelHandler.getInstance().addObserver(this);
    }

    private void setInitialLayout() {
        setContentView(R.layout.activity_main);
        mButtonStartService = (Button) findViewById(R.id.btnStartService);
        mButtonStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceIntent = new Intent(context, BatteryLevelService.class);
                startService(serviceIntent);
                setContentView(R.layout.activity_main_running);
                setViews();
            }
        });
    }

    private void setViews() {
        mTextViewDisplay = (TextView) findViewById(R.id.tvDisplay);
        mButtonStopService = (Button) findViewById(R.id.btnStopService);
        mButtonStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceIntent = new Intent(context, BatteryLevelService.class);
                if (isMyServiceRunning()) {
                    stopService(serviceIntent);
                    setInitialLayout();
                }
            }
        });
    }


    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.asbozh.forthhomework2.BatteryLevelService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            Intent intent = new Intent(this, BatteryLevelService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        mService.setPercentage((Integer) arg);
        mTextViewDisplay.setText(mService.getTextView());
    }
}
