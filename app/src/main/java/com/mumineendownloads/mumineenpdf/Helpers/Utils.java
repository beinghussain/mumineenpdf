package com.mumineendownloads.mumineenpdf.Helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;

import com.mumineendownloads.mumineenpdf.R;


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