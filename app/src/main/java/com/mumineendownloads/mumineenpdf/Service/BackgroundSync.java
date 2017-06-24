package com.mumineendownloads.mumineenpdf.Service;


import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;

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

import java.util.ArrayList;
import java.util.Objects;

public class BackgroundSync {
    private PDFListFragment pdfListFragment;
    private Context applicationContext;

    public BackgroundSync(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public BackgroundSync(Context applicationContext, PDFListFragment pdfListFragment) {
        this.applicationContext = applicationContext;
        this.pdfListFragment = pdfListFragment;
    }

    private String executeF() {
        boolean connected = Utils.isConnected(applicationContext);
        if(connected) {
            final RequestQueue queue = Volley.newRequestQueue(applicationContext);
            String url = "http://mumineendownloads.com/app/getPdfApp.php";

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            ArrayList<PDF.PdfBean> pdfBeanArrayList;
                            pdfBeanArrayList = gson.fromJson(response, new TypeToken<ArrayList<PDF.PdfBean>>() {
                            }.getType());
                            PDFHelper pdfHelper = new PDFHelper(applicationContext);
                            for (int i = 0; i < pdfBeanArrayList.size(); i++) {
                                PDF.PdfBean pdfBean = pdfBeanArrayList.get(i);
                                pdfHelper.updateOrInsert(pdfBean);
                            }
                            pdfListFragment.notifyDatasetChanged();
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

    public void taskSync(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                executeF();
            }
        });
    }
}

