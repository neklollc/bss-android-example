package com.neklo.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;

import com.neklo.beacon.SmartStoreHelper;
import com.neklo.beacon.SmartStoreService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkMessage();

        // Register receiver for action beacons
        IntentFilter filter = new IntentFilter(SmartStoreHelper.getBroadcastAction());
        registerReceiver(actionReceiver, filter);
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
    }

    @Override
    public void onStop(){
        super.onStop();
        // Enable notifications again
        SmartStoreHelper.setNotificationPosting(this, true);
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
