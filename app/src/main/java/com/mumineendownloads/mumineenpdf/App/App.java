package com.mumineendownloads.mumineenpdf.App;

import android.app.Application;

import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadManager;
import com.marcinorlowski.fonty.Fonty;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initDownloader();
        Fonty
                .context(this)
                .regularTypeface("myfonts.ttf")
                .italicTypeface("myfonts.ttf")
                .boldTypeface("myfonts.ttf")
                .done();
    }

    private void initDownloader() {
        DownloadConfiguration configuration = new DownloadConfiguration();
        configuration.setMaxThreadNum(10);
        configuration.setThreadNum(4);
        DownloadManager.getInstance().init(getApplicationContext(), configuration);
    }
}
