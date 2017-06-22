package com.mumineendownloads.mumineenpdf.Helpers;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.Model.PDFDownload;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class PDM {

    public static Context context;

    public static PDM newInstance(Context context){
        PDM.context = context;
        return new PDM();
    }

    public ArrayList<PDFDownload> getDownloadIds(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(TAG, null);
        Type type = new TypeToken<ArrayList<PDFDownload>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void addDownloadId(PDFDownload pdfDownload){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        ArrayList<PDFDownload> arrayList = getDownloadIds();
        arrayList.add(pdfDownload);
        String json = gson.toJson(arrayList);
        editor.putString(TAG, json);
        editor.apply();
    }


}
