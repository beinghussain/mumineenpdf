package com.mumineendownloads.mumineenpdf.Helpers;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mumineendownloads.mumineenpdf.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class Utils {
    private Context context;
    private static List<String> sections;

    public Utils(Context context) {
        this.context = context;
    }

    public static void deleteList(Context ctx, String sectionName){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor mEdit1 = sp.edit();
        mEdit1.remove(sectionName);
        mEdit1.apply();
        removeFromList(ctx,sectionName);
    }

    private static void removeFromList(Context ctx, String sectionName) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor mEdit1 = sp.edit();
        List<String> a = getSections(ctx);
        a.remove(sectionName);
        Gson gson = new Gson();
        mEdit1.putString("list_strings",gson.toJson(a));
        mEdit1.apply();
    }

    private static void saveArray(Context context, ArrayList<Integer> goList, String sectionName) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor mEdit1 = sp.edit();
        Gson gson = new Gson();
        String listJson = gson.toJson(goList);
        mEdit1.putString(sectionName,listJson);
        mEdit1.apply();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, OnTheWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.list_view);
    }

    public static ArrayList<Integer> loadArray(Context mContext, String sectionName) {
        SharedPreferences mSharedPreference1 =   PreferenceManager.getDefaultSharedPreferences(mContext);
        String listJson = mSharedPreference1.getString(sectionName, "[]");
        Gson gson = new Gson();

        return gson.fromJson(listJson, new TypeToken<ArrayList<Integer>>() {
        }.getType());

    }

    public static String getDownloadPerSize(long finished, long total, int progress) {
        String t,f;
        int sizeF = (int) (finished / 1024);
        int sizeT = (int) (total / 1024);
        if (total < 1000000) {
            t = total / 1024 + " KB  ";
        } else {
            Float size = (float) sizeT / 1024;
            t = new DecimalFormat("##.##").format(size) + " MB  ";
        }
        if (finished < 1000000) {
            f = finished / 1024 + "KB / ";
        } else {
            Float size = (float) sizeF / 1024;
            f = new DecimalFormat("##.##").format(size) + " MB / ";
        }
        return  f + t + progress + "%";
    }

    public static boolean isConnected(Context context) {
        try {
            ConnectivityManager connec =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connec != null) {
                if (connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTED || connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTING || connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTING || connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTED) {
                    return true;

                } else if (
                        connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                                connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.DISCONNECTED) {
                    return false;
                }
            }
        }catch (NullPointerException ignored){

        }
        return false;
    }

    public static void addToSpecificList(Context ctx, ArrayList<Integer> list, String sectionName){
        ArrayList<Integer> oldArrayList = loadArray(ctx, sectionName);
        for(int i = 0; i<list.size(); i++){
            if(!oldArrayList.contains(list.get(i))){
                oldArrayList.add(list.get(i));
            }
        }
        saveArray(ctx,oldArrayList,sectionName);
    }

    public static int getPDFCount(Context mCtx, String sectionName){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mCtx);
        String json = sp.getString(sectionName, "[]");
        Gson gson = new Gson();
        List<Integer> list = gson.fromJson(json, new TypeToken<ArrayList<Integer>>() {}.getType());
        return list.size();
    }

    public static void addSectionToList(Context mContext, String sectionString){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor mEdit1 = sp.edit();
        List<String> sections = getSections(mContext);
        if(!sections.contains(sectionString)){
            sections.add(sectionString);
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(sections);
        mEdit1.putString("list_strings",jsonString);
        mEdit1.apply();
    }

    public static List<String> getSections(Context mContext) {
        SharedPreferences mSharedPreference1 = PreferenceManager.getDefaultSharedPreferences(mContext);
        String listJson = mSharedPreference1.getString("list_strings", "[]");
        Gson gson = new Gson();

        return gson.fromJson(listJson, new TypeToken<List<String>>() {
        }.getType());
    }


}