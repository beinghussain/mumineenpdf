package com.mumineendownloads.mumineenpdf.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.PrefManager;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.BackgroundSync;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class Startup extends AppCompatActivity {

    private PrefManager prefManager;
    private ProgressView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_startup);
        Fonty.setFonts(this);
        loading = (ProgressView) findViewById(R.id.loading);
        fetch();

        changeStatusBarColor();

    }

    private void fetch() {
        boolean connected = Utils.isConnected(getApplicationContext());
        if (connected) {
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://mumineendownloads.com/app/getPdfApp.php";
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Gson gson = new Gson();
                                final ArrayList<PDF.PdfBean> pdfBeanArrayList;
                                pdfBeanArrayList = gson.fromJson(response, new TypeToken<ArrayList<PDF.PdfBean>>() {
                                }.getType());
                                final PDFHelper pdfHelper = new PDFHelper(getApplicationContext());
                                final Thread task = new Thread() {
                                    @Override
                                    public void run() {
                                        ArrayList<PDF.PdfBean> localArray = pdfHelper.getAllPDFS("all");
                                        if (pdfBeanArrayList.size() < pdfHelper.getAllPDFS("all").size()) {
                                            for (PDF.PdfBean pdfBean : localArray) {
                                                if (!isThere(pdfBeanArrayList, pdfBean.getPid())) {
                                                    pdfHelper.deletePDF(pdfBean);
                                                }
                                            }
                                        }
                                        for (int i = 0; i < pdfBeanArrayList.size(); i++) {
                                            PDF.PdfBean pdfBean = pdfBeanArrayList.get(i);
                                            pdfHelper.updateOrInsert(pdfBean);
                                            if (i == pdfBeanArrayList.size() - 1) {
                                                Startup.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        launchHomeScreen();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                };

                                task.start();
                            }catch (Exception ignored){
                                Toasty.normal(getApplicationContext(),"Some error occured").show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Running", "No");
                    Toasty.normal(getApplicationContext(), "Some Error Occured").show();
                }
            });
            queue.add(stringRequest);
        }
    }

    private boolean isThere(ArrayList<PDF.PdfBean> pdfList, int pid){
        for(PDF.PdfBean pdfBean : pdfList){
            if(pdfBean.getPid()==pid){
                return true;
            }
        }
        return false;
    }



    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            launchHomeScreen();
        }
    };

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

    private void launchHomeScreen() {
         prefManager.setFirstTimeLaunch(false);

         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 loading.setVisibility(View.GONE);
                 finish();
                 startActivity(new Intent(Startup.this, MainActivity.class));
             }
         }, 5000);

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
