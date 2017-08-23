package com.mumineendownloads.mumineenpdf.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Fragments.Go;
import com.mumineendownloads.mumineenpdf.Fragments.Home;
import com.mumineendownloads.mumineenpdf.Fragments.LibraryFragment;
import com.mumineendownloads.mumineenpdf.Fragments.RequestPage;
import com.mumineendownloads.mumineenpdf.Fragments.Saved;
import com.mumineendownloads.mumineenpdf.Helpers.BottomNavigationViewHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.BackgroundSync;
import com.mumineendownloads.mumineenpdf.Service.DownloadService;
import com.mumineendownloads.mumineenpdf.Service.HandleMessage;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {


    public static FrameLayout cor;

    public MainActivity(){

    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          refresh();
        }
    };
    public static final int RC_STORAGE = 1;
    public static BottomNavigationView bottomNavigationView;
    public static FrameLayout frameLayout;



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
            Home home = new Home();
            Saved savedFragment = new Saved();
            RequestPage requestPage = new RequestPage();
            Go goFragment = new Go();
            LibraryFragment library = new LibraryFragment();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = home.newInstance(MainActivity.this);
                    break;
                case R.id.navigation_request:
                    selectedFragment = requestPage.newInstance();
                    break;
                case R.id.navigation_saved:
                    selectedFragment = savedFragment.newInstance();
                    break;
                case R.id.navigation_upload:
                    selectedFragment = goFragment.newInstance();
                    break;
                case R.id.navigation_library:
                    Log.e("Clicked","Library");
                    selectedFragment = library.newInstance();
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                RC_STORAGE);

                    }
                }
            }
        },10000);

        Fonty.setFonts(this);
        Intent intent = getIntent();
        try {
            String id = intent.getStringExtra(HandleMessage.UPDATE_APP);
            if(id.equals("update")){
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
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
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        cor = (FrameLayout) findViewById(R.id.fragment);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Home home = new Home();
        transaction.replace(R.id.fragment, home.newInstance(MainActivity.this));
        transaction.commit();

        if(!Utils.isConnected(getApplicationContext())){
            Snackbar snackbar = Snackbar
                    .make(bottomNavigationView, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
            snackbar.show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void refresh(){
        Home.viewPager.getAdapter().notifyDataSetChanged();
        Fonty.setFonts(Home.tabLayout);
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
