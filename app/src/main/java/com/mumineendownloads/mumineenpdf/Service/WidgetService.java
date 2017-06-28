package com.mumineendownloads.mumineenpdf.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mumineendownloads.mumineenpdf.Helpers.WidgetRemoteViewsFactory;
import com.mumineendownloads.mumineenpdf.R;
import com.mumineendownloads.mumineenpdf.Widget.NewAppWidget;

import java.util.ArrayList;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetRemoteViewsFactory(this.getApplicationContext(), intent));
    }
}
