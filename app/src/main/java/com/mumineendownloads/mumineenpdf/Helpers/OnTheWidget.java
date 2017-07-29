package com.mumineendownloads.mumineenpdf.Helpers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity_;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Service.WidgetService;

public class OnTheWidget extends AppWidgetProvider {


    private static final String TOAST_ACTION = "com.mumineendownloads.com.toast";
    public static final String EXTRA_ITEM = "extra_item.mumineendownloads.com";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
        }
    }


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        rv.setRemoteAdapter(R.id.list_view, intent);
        CharSequence s  = OnTheWidgetConfigureActivity.loadTitlePref(context,appWidgetId);
        rv.setTextViewText(R.id.list_title,s);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        Intent toastIntent = new Intent(context, WidgetRemoteViewsFactory.class);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.list_view  , toastPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.new_app_widget);

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            rv.setRemoteAdapter(R.id.list_view, intent);
            CharSequence s  = OnTheWidgetConfigureActivity.loadTitlePref(context,appWidgetId);
            rv.setTextViewText(R.id.list_title,s);

            Intent startActivityIntent = new Intent(context, PDFActivity_.class);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_ONE_SHOT);
            rv.setPendingIntentTemplate(R.id.list_view, startActivityPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            OnTheWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

