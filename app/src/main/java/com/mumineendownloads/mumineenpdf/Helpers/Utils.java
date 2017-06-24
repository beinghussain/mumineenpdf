package com.mumineendownloads.mumineenpdf.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mumineendownloads.mumineenpdf.Model.PDFDownload;
import com.mumineendownloads.mumineenpdf.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class Utils {
    private Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        BadgeDrawable badge;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }


    public ArrayList<PDFDownload> getArrayList() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(TAG, null);
        Type type = new TypeToken<ArrayList<PDFDownload>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public boolean CheckIfExists(int pid) {
        for (PDFDownload object : getArrayList()) {
            if (object.getPid() == pid) {
                return true;
            }
        }
        return false;
    }

    public PDFDownload getStatus(int pid) {
        for (PDFDownload pdfDownload : getArrayList()) {
            if (pdfDownload.getPid() == pid) {
                {
                    return pdfDownload;
                }
            }
        }
        return null;
    }


    public void addToArrayList(PDFDownload pdfDownload){
        ArrayList arrayList = getArrayList();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        getArrayList().add(pdfDownload);
        String json = gson.toJson(arrayList);
        editor.putString(TAG, json);
        editor.apply();
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


}