package com.neklo.demo;

import android.app.Application;

import com.neklo.beacon.SmartStoreHelper;

/**
 * Created by Max on 28/10/15.
 */
public class DemoApp extends Application {

    private final static int UPDATE_INTERVAL_DEFAULT = 6 * 60 * 60 * 1000; // 6 hours
    private final static int PUSH_TIMEOUT_DEFAULT = 30 * 60 * 1000; // 30 mins

    @Override
    public void onCreate() {
        super.onCreate();
        // init service
        SmartStoreHelper.initService(this,
                getString(R.string.api_key),
                getString(R.string.object_key),
                MainActivity.class,
                R.mipmap.ic_launcher,
                UPDATE_INTERVAL_DEFAULT,
                PUSH_TIMEOUT_DEFAULT,
                true);
    }
}
