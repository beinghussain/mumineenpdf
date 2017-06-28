package com.mumineendownloads.mumineenpdf.Service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.util.Util;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ConnectivityReceiver extends JobService {
        private static final String TAG = "SyncService";

        @Override
        public boolean onStartJob(JobParameters params) {
            Intent service = new Intent(getApplicationContext(), LocalWordService.class);
            getApplicationContext().startService(service);
            return true;
        }

        @Override
        public boolean onStopJob(JobParameters params) {
            return true;
        }
}
