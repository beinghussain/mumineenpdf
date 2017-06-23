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

public class BackgroundSync extends AsyncTask<Void, Void,Void> {
    private PDFListFragment pdfListFragment;
    private Context applicationContext;

    public BackgroundSync(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public BackgroundSync(Context applicationContext, PDFListFragment pdfListFragment) {
        this.applicationContext = applicationContext;
        this.pdfListFragment = pdfListFragment;
    }

    public void executeF() {
        boolean connected = Utils.isConnected(applicationContext);
        if(connected) {
            RequestQueue queue = Volley.newRequestQueue(applicationContext);
            String url = "http://mumineendownloads.com/app/getPdfApp.php";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
                                Log.e("PDF",pdfHelper.getPDF(pdfBean.getPid()).getTitle());
//                                if(pdfBeanArrayList.get(i).getAlbum().equals("0")){
//                                    pdfHelper.deleteContact(pdfBeanArrayList.get(i));
//                                }
//                                else if(pdfHelper.getPDF(pdfBeanArrayList.get(i).getPid())!=null) {
//                                    pdfHelper.updatePDF(pdfBeanArrayList.get(i));
//                                }
//                                else {
//                                    pdfHelper.addPDF(pdfBeanArrayList.get(i));
//                                }
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

    @Override
    protected Void doInBackground(Void... params) {
        executeF();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(pdfListFragment!=null){
            pdfListFragment.notifyDatasetChanged();
        }
    }
}

