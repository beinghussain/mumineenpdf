package com.mumineendownloads.mumineenpdf.Helpers;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class PDM {

    private final Context context;

    public PDM(Context context){
        this.context = context;
    }


    public ArrayList<Long> getDownloadIds(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(TAG, null);
        Type type = new TypeToken<ArrayList<Long>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void addDownloadId(Long downloadId){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        ArrayList<Long> arrayList = getDownloadIds();
        arrayList.add(downloadId);
        String json = gson.toJson(arrayList);
        editor.putString(TAG, json);
        editor.apply();
    }
}
