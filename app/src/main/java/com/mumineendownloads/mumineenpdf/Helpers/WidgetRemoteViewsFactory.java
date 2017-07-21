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

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

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
        dbhelper = new PDFHelper(context);
    }

    private void updateWidgetListView() {
        widgetList.clear();
        String title = OnTheWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        List<Integer> a = Utils.loadArray(context,title);
        for(Integer i : a){
            PDF.PdfBean p = dbhelper.getPDF(i);
            widgetList.add(p);
        }
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
        String downloaded = "";
        boolean downloadBool = false;
        if(widgetList.get(position).getStatus()==Status.STATUS_DOWNLOADED){
            downloaded = "Downloaded";
            downloadBool = true;
        }else {
            downloaded = "Not Downloaded";
            downloadBool = false;
        }
        remoteView.setTextViewText(R.id.item, widgetList.get(position).getTitle());
        remoteView.setTextViewText(R.id.album, widgetList.get(position).getAlbum());
        remoteView.setTextViewText(R.id.downloaded,downloaded);
        Intent intent = new Intent();
        PDF.PdfBean pdf = widgetList.get(position);
        intent.putExtra("mode", 2);
        intent.putExtra("pid", pdf.getPid());
        intent.putExtra("title", pdf.getTitle());
        if(downloadBool) {
            remoteView.setOnClickFillInIntent(R.id.mainView, intent);
        }
        return remoteView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

}