package com.mumineendownloads.mumineenpdf.Service;


import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Helpers.PDFHelper;
import com.mumineendownloads.mumineenpdf.Model.PDF;

import java.util.ArrayList;

public class BackgroundSync  {
    private MainActivity activity;

    public BackgroundSync(MainActivity activity) {
        this.activity = activity;
    }

    public void execute() {
        RequestQueue queue = Volley.newRequestQueue(activity.getApplicationContext());
        String url ="http://192.168.43.217/app/getPdf.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        ArrayList<PDF.PdfBean> pdfBeanArrayList;
                        pdfBeanArrayList = gson.fromJson(response, new TypeToken<ArrayList<PDF.PdfBean>>(){}.getType());
                        PDFHelper pdfHelper = new PDFHelper(activity.getApplicationContext());
                        for(int i=0;i <pdfBeanArrayList.size(); i++) {
                           pdfHelper.addPDF(pdfBeanArrayList.get(i));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Log.e("Error", error.toString());
            }
        });
        queue.add(stringRequest);
    }
}

