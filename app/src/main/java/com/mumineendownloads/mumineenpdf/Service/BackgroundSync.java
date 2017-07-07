package com.mumineendownloads.mumineenpdf.Service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aspsine.multithreaddownload.util.L;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Fragments.PDFListFragment;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;

public class BackgroundSync extends Service {
    public static final String ACTION_BROADCAST_SYNC = "background_sync";
    public static final String ACTION = "com.backgroundSync" ;
    private PDFListFragment pdfListFragment;
    private Context applicationContext;
    public BackgroundSync() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        taskSync();
        return super.onStartCommand(intent,flags,startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean isThere(ArrayList<PDF.PdfBean> pdfList, int pid){
        for(PDF.PdfBean pdfBean : pdfList){
            if(pdfBean.getPid()==pid){
                return true;
            }
        }
        return false;
    }

    private String executeF() {
        boolean connected = Utils.isConnected(getApplicationContext());
        if(connected) {
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://mumineendownloads.com/app/getPdfApp.php";
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            final ArrayList<PDF.PdfBean> pdfBeanArrayList;
                            pdfBeanArrayList = gson.fromJson(response, new TypeToken<ArrayList<PDF.PdfBean>>() {
                            }.getType());
                            final PDFHelper pdfHelper = new PDFHelper(getApplicationContext());

                            final Thread task = new Thread()
                            {
                                @Override
                                public void run()
                                {
                                    ArrayList<PDF.PdfBean> localArray = pdfHelper.getAllPDFS("all");
                                    if(pdfBeanArrayList.size()<pdfHelper.getAllPDFS("all").size()){
                                        for(PDF.PdfBean pdfBean : localArray){
                                            if(!isThere(pdfBeanArrayList,pdfBean.getPid())){
                                                pdfHelper.deletePDF(pdfBean);
                                            }
                                        }
                                    }
                                    for (int i = 0; i < pdfBeanArrayList.size(); i++) {
                                        PDF.PdfBean pdfBean = pdfBeanArrayList.get(i);
                                        pdfHelper.updateOrInsert(pdfBean);
                                    }
                                    updateRefresh();

                                }
                            };

                            task.start();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            queue.add(stringRequest);
        }

        return "Done";
    }

    private void updateRefresh() {
        Intent intent = new Intent(
        ACTION);
        intent.putExtra("setting", true);
        sendBroadcast(intent);
    }

    public void taskSync(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                executeF();
            }
        });
    }
}

