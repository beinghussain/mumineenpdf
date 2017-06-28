package com.mumineendownloads.mumineenpdf.Helpers;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.marcinorlowski.fonty.Fonty;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Widget.NewAppWidget;

import java.util.ArrayList;

import static android.R.style.Widget;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String EXTRA_PID = "com.mumineen.pdf";
    private Context context = null;
    private int appWidgetId;

    private ArrayList<PDF.PdfBean> widgetList = new ArrayList<PDF.PdfBean>();
    private PDFHelper dbhelper;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.d("AppWidgetId", String.valueOf(appWidgetId));
        dbhelper = new PDFHelper(this.context);
    }

    private void updateWidgetListView() {
        this.widgetList = dbhelper.getAllPDFS("Marasiya");
    }

    @Override
    public void onCreate() {
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged() {
        updateWidgetListView();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return widgetList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.list_row);
        remoteView.setTextViewText(R.id.item, widgetList.get(position).getTitle());
        Bundle extras = new Bundle();
        extras.putInt(NewAppWidget.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        remoteView.setOnClickFillInIntent(R.id.item, fillInIntent);
        return remoteView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

}