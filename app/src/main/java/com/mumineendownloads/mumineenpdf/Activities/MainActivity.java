package com.mumineendownloads.mumineenpdf.Activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Fragments.Home;
import com.mumineendownloads.mumineenpdf.Fragments.SavedFragment;
import com.mumineendownloads.mumineenpdf.Helpers.BottomNavigationViewHelper;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.BackgroundSync;

import java.lang.reflect.Type;
import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

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

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Home home = new Home(MainActivity.this);
            SavedFragment savedFragment = new SavedFragment();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = home.newInstance();
                    break;
                case R.id.navigation_request:
                    selectedFragment = savedFragment.newInstance();
                    break;
                case R.id.navigation_saved:
                    selectedFragment = savedFragment.newInstance();
                    break;
                case R.id.navigation_upload:
                    selectedFragment = savedFragment.newInstance();
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
        methodRequiresTwoPermission();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Home home = new Home();
        transaction.replace(R.id.fragment, home.newInstance());
        transaction.commit();

        BackgroundSync backgroundSync = new BackgroundSync(MainActivity.this);
       // backgroundSync.execute();
    }
}
