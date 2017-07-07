package com.mumineendownloads.mumineenpdf.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.PrefManager;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.BackgroundSync;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;

public class Splash extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent1);
            finish();
        }
    };
    private PrefManager prefManager;

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(BackgroundSync.ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            openMainScreen();
            finish();
        } else {
           openStartupScreen();
            launchSync();
        }
    }

    public void openStartupScreen(){
        startActivity(new Intent(Splash.this, Startup.class));
        finish();
    }

    public void openMainScreen(){
        startActivity(new Intent(Splash.this, MainActivity  .class));
        finish();
    }

    public void launchSync(){
        startService(new Intent(Splash.this, BackgroundSync.class));
    }
}
