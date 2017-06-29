package com.mumineendownloads.mumineenpdf.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.aspsine.multithreaddownload.util.L;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD_BROAD_CAST = "com.aspsine.multithreaddownload.demo:action_download_broad_cast";

    public static final String ACTION_DOWNLOAD = "com.aspsine.multithreaddownload.demo:action_download";

    public static final String ACTION_PAUSE = "com.aspsine.multithreaddownload.demo:action_pause";

    public static final String ACTION_CANCEL = "com.aspsine.multithreaddownload.demo:action_cancel";

    public static final String ACTION_PAUSE_ALL = "com.aspsine.multithreaddownload.demo:action_pause_all";

    public static final String ACTION_CANCEL_ALL = "com.aspsine.multithreaddownload.demo:action_cancel_all";

    public static final String EXTRA_POSITION = "extra_position";

    public static final String EXTRA_TAG = "extra_tag";

    public static final String EXTRA_APP_INFO = "extra_app_info";

    private File mDownloadDir;

    private DownloadManager mDownloadManager;

    private NotificationManagerCompat mNotificationManager;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr = null;

    List<String> downloadingList = new ArrayList<>();

    private Context context;

    public static void intentDownload(Context context, int position, String tag, PDF.PdfBean pdf) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_APP_INFO, pdf);
        context.startService(intent);
    }

    public static void intentPause(Context context, String tag) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_PAUSE);
        intent.putExtra(EXTRA_TAG, tag);
        context.startService(intent);
    }

    public static void intentPauseAll(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        context.startService(intent);
    }

    public static void destroy(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        context.stopService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadingList.clear();
        if (intent != null) {
            String action = intent.getAction();
            int position = intent.getIntExtra(EXTRA_POSITION, 0);
            PDF.PdfBean pdf = (PDF.PdfBean) intent.getSerializableExtra(EXTRA_APP_INFO);
            String tag = intent.getStringExtra(EXTRA_TAG);
            switch (action) {
                case ACTION_DOWNLOAD:
                    download(position, pdf, tag);
                    break;
                case ACTION_PAUSE:
                    pause(tag);
                    break;
                case ACTION_CANCEL:
                    cancel(tag);
                    break;
                case ACTION_PAUSE_ALL:
                    pauseAll();
                    break;
                case ACTION_CANCEL_ALL:
                    cancelAll();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void download(final int position, final PDF.PdfBean appInfo, String tag) {
        final DownloadRequest request = new DownloadRequest.Builder()
                .setName(appInfo.getPid() + ".pdf")
                .setUri("http://mumineendownloads.com/downloadFile.php?file="+appInfo.getSource())
                .setFolder(mDownloadDir)
                .build();
        mDownloadManager.download(request, tag, new DownloadCallBack(position, appInfo, mNotificationManager, getApplicationContext()));
    }

    private void pause(String tag) {
        mDownloadManager.pause(tag);
    }

    private void cancel(String tag) {
        mDownloadManager.cancel(tag);
    }

    private void pauseAll() {
        mDownloadManager.pauseAll();
    }

    private void cancelAll() {
        mDownloadManager.cancelAll();
    }

    private class DownloadCallBack implements CallBack {

        private int mPosition;

        private PDF.PdfBean mPdf;

        private LocalBroadcastManager mLocalBroadcastManager;

        private NotificationCompat.Builder mBuilder;

        private NotificationManagerCompat mNotificationManager;

        private long mLastTime;

        DownloadCallBack(int position, PDF.PdfBean pdf, NotificationManagerCompat notificationManager, Context context) {
            mPosition = position;
            mPdf = pdf;
            mNotificationManager = notificationManager;
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
            mBuilder = new NotificationCompat.Builder(context);
        }

        @Override
        public void onStarted() {
            Intent intent = new Intent(getApplicationContext(), BroadcastReceiver.class);
            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(mPdf.getTitle())
                    .setOngoing(true)
                    .setContentText("Init Download")
                    .setProgress(100, 0, true)
                    .setTicker("Start download " + mPdf.getTitle());
            mBuilder.addAction(0, "Cancel", pIntent);
            mBuilder.addAction(0, "Pause", pIntent);
            updateNotification();
        }

        @Override
        public void onConnecting() {
            L.i(TAG, "onConnecting()");
            mBuilder.setContentText("Connecting")
                    .setProgress(100, 0, true);
            updateNotification();

            mPdf.setStatus(PDF.STATUS_CONNECTING);
            sendBroadCast(mPdf);
        }

        @Override
        public void onConnected(long total, boolean isRangeSupport) {
            L.i(TAG, "onConnected()");
            mBuilder.setContentText("Connected")
                    .setProgress(100, 0, true);
            updateNotification();
        }

        @Override
        public void onProgress(long finished, long total, int progress) {

            if (mLastTime == 0) {
                mLastTime = System.currentTimeMillis();
            }

            mPdf.setStatus(PDF.STATUS_DOWNLOADING);
            mPdf.setProgress(progress);
            mPdf.setDownloadPerSize(Utils.getDownloadPerSize(finished, total, progress));

            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastTime > 500) {
                String f, t;
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
                L.i(TAG, "onProgress()");
                mBuilder.setContentText(progress + "%");
                mBuilder.setProgress(100, progress, false);
                updateNotification();

                sendBroadCast(mPdf);

                mLastTime = currentTime;
            }
        }

        @Override
        public void onCompleted() {
            L.i(TAG, "onDownloadCanceled()");
            mBuilder.setContentText("Download completed");
            mBuilder.setTicker(mPdf.getTitle() + " download completed");
            updateNotification();
            downloadingList.add(mPdf.getTitle());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mNotificationManager.cancel(0);
                }
            }, 1000);
            addNotificationCompleted(mPdf);
            mPdf.setStatus(PDF.STATUS_COMPLETE);
            mPdf.setProgress(100);
            sendBroadCast(mPdf);
        }

        @Override
        public void onDownloadPaused() {
            L.i(TAG, "onDownloadPaused()");
            mBuilder.setContentText("Download Paused");
            mBuilder.setTicker(mPdf.getTitle() + " download Paused");
            mBuilder.setProgress(100, mPdf.getProgress(), false);
            updateNotification();

            mPdf.setStatus(PDF.STATUS_PAUSED);
            sendBroadCast(mPdf);
        }

        @Override
        public void onDownloadCanceled() {
            L.i(TAG, "onDownloadCanceled()");
            mBuilder.setContentText("Download Canceled");
            mBuilder.setTicker(mPdf.getTitle() + " download Canceled");
            updateNotification();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mNotificationManager.cancel(0);
                }
            }, 1000);

            mPdf.setStatus(PDF.STATUS_NOT_DOWNLOAD);
            mPdf.setProgress(0);
            mPdf.setDownloadPerSize("");
            sendBroadCast(mPdf);
        }

        @Override
        public void onFailed(DownloadException e) {
            L.i(TAG, "onFailed()");
            e.printStackTrace();
            mBuilder.setContentText("Download Failed");
            mBuilder.setTicker(mPdf.getTitle() + " download failed");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setCategory(Notification.CATEGORY_ERROR);
            }
            updateNotification();
            mPdf.setStatus(PDF.STATUS_DOWNLOAD_ERROR);
            sendBroadCast(mPdf);
        }

        private void updateNotification() {
            mNotificationManager.notify(0, mBuilder.build());
        }

        private void sendBroadCast(PDF.PdfBean pdf) {
            Intent intent = new Intent();
            intent.setAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
            intent.putExtra(EXTRA_POSITION, mPosition);
            intent.putExtra(EXTRA_APP_INFO, pdf);
            mLocalBroadcastManager.sendBroadcast(intent);
        }
    }

    private void addNotificationCompleted(PDF.PdfBean pdfBean) {
        Intent resultIntent = new Intent(this, PDFActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(pdfBean.getTitle())
                        .setContentText("Download Completed");
        mBuilder.setContentIntent(resultPendingIntent);
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(pdfBean.getPid(), mBuilder.build());

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager = DownloadManager.getInstance();
        mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
        mDownloadDir = new File(Environment.getExternalStorageDirectory(), "Mumineen");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadManager.pauseAll();
    }

}