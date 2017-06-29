package com.mumineendownloads.mumineenpdf.Helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;

import com.mumineendownloads.mumineenpdf.R;

import java.text.DecimalFormat;


public class Utils {
    private Context context;
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    public Utils(Context context) {
        this.context = context;
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