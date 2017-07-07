package com.mumineendownloads.mumineenpdf.Service;
import android.content.Intent;
import android.widget.RemoteViewsService;

import com.mumineendownloads.mumineenpdf.Helpers.WidgetRemoteViewsFactory;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetRemoteViewsFactory(this.getApplicationContext(), intent));
    }
}
