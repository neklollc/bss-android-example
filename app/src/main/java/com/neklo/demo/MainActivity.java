package com.neklo.demo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;

import com.neklo.beacon.SmartStoreHelper;
import com.neklo.beacon.SmartStoreService;
import com.neklo.beacon.stats.SmartStoreEvents;
import com.neklo.beacon.stats.StatsHelper;
import com.neklo.beacon.stats.params.StatStateParams;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkMessage();

        // Register receiver for action beacons
        IntentFilter filter = new IntentFilter(SmartStoreHelper.getBroadcastAction());
        registerReceiver(actionReceiver, filter);

        // Force update beacons and companies from server, this is not necessary because we have autoupdate interval in library
        SmartStoreHelper.forceUpdateData(this);

        // Ask M permissions only if we target API 23 and launch on M+ device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Coarse location permission granted");
                } else {
                    Log.d(TAG, "Functionality limited");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Don't forget unregister receiver
        unregisterReceiver(actionReceiver);
    }

    /**
     * Check intent on message
     */
    private boolean checkMessage() {
        if (getIntent().hasExtra(SmartStoreService.INTENT_FIELD_TITLE)){
            showPopup(
                    getIntent().getStringExtra(SmartStoreService.INTENT_FIELD_TITLE),
                    getIntent().getStringExtra(SmartStoreService.INTENT_FIELD_TEXT),
                    getIntent().getStringExtra(SmartStoreService.INTENT_FIELD_LINK));
            return true;
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Check incoming message
        checkMessage();
    }

    @Override
    public void onStart(){
        super.onStart();
        // Disable notifications because we start activity and will handle it self
        SmartStoreHelper.setNotificationPosting(this, false);
        StatsHelper.getInstance().postStat(SmartStoreEvents.APPLICATION_STATE, new StatStateParams(StatStateParams.STATE_ACTIVATED));
    }

    @Override
    public void onStop(){
        super.onStop();
        // Enable notifications again
        SmartStoreHelper.setNotificationPosting(this, true);
        StatsHelper.getInstance().postStat(SmartStoreEvents.APPLICATION_STATE, new StatStateParams(StatStateParams.STATE_DEACTIVATED));
    }

    /**
     * Alert popup
     * @param title
     * @param text
     */
    private void showPopup(final String title, final String text, final String link) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(Html.fromHtml(text))
                .setNegativeButton("Cancel", null)
                .setPositiveButton("More", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        showFragment(NotificationFragment.getInstance(title, text, link), true);
                    }
                })
                .create()
                .show();
    }

    /**
     * Receiver for action beacon
     */
    BroadcastReceiver actionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ActionReceiver", "received beacon " + intent.getStringExtra(SmartStoreService.INTENT_FIELD_TITLE));
            String title = intent.getStringExtra(SmartStoreService.INTENT_FIELD_TITLE);
            String text = intent.getStringExtra(SmartStoreService.INTENT_FIELD_TEXT);
            String link = intent.getStringExtra(SmartStoreService.INTENT_FIELD_LINK);
            if (title != null && text != null){
                showPopup(title, text, link);
            }else{
                String params = intent.getStringExtra(SmartStoreService.INTENT_FIELD_DATA);
                if (params != null){
                    // TODO actions
                }
            }
        }
    };
}
