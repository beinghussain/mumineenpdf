package com.mumineendownloads.mumineenpdf.Activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.BuildConfig;
import com.mumineendownloads.mumineenpdf.Fragments.Go;
import com.mumineendownloads.mumineenpdf.Fragments.Home;
import com.mumineendownloads.mumineenpdf.Fragments.Saved;
import com.mumineendownloads.mumineenpdf.Helpers.BottomNavigationViewHelper;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.BackgroundSync;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;
import com.mumineendownloads.mumineenpdf.Service.HandleMessage;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {


    public MainActivity(){

    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          refresh();
        }
    };
    private static final int RC_STORAGE = 1;
    public static BottomNavigationView bottomNavigationView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_STORAGE)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.title_request),
                    RC_STORAGE, perms);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        stopService(intent);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Home home = new Home(MainActivity.this);
            Saved savedFragment = new Saved();
            Go goFragment = new Go();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = home.newInstance(MainActivity.this);
                    break;
                case R.id.navigation_request:
                    selectedFragment = savedFragment.newInstance();
                    break;
                case R.id.navigation_saved:
                    selectedFragment = savedFragment.newInstance();
                    break;
                case R.id.navigation_upload:
                    selectedFragment = goFragment.newInstance();
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, selectedFragment);
            transaction.commit();

            return true;
        }
    };

    public static void toggle(boolean showHide){
        if(showHide) {
            bottomNavigationView.setVisibility(View.GONE);
        }else {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fonty.setFonts(this);
        Intent intent = getIntent();
        try {
            String id = intent.getStringExtra(HandleMessage.UPDATE_APP);
            if(id.equals("update")){
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        } catch (NullPointerException ignored) {

        }
        methodRequiresTwoPermission();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Home home = new Home(MainActivity.this);
        transaction.replace(R.id.fragment, home.newInstance(MainActivity.this));
        transaction.commit();

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .setDisplay(Display.DIALOG)
                .setTitleOnUpdateAvailable("Update Available")
                .start();

    }

    private void openUpdateLink() {

    }

    public void refresh(){
        Home.viewPager.getAdapter().notifyDataSetChanged();
        Fonty.setFonts(Home.viewPager);
    }

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

    public Action getIndexApiAction() {
        return Actions.newView("Main", "https://mumineendownloads.com");
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUserActions.getInstance().start(getIndexApiAction());
    }

    @Override
    public void onStop() {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().end(getIndexApiAction());
        super.onStop();
    }
}
