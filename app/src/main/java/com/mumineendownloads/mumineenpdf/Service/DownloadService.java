package com.mumineendownloads.mumineenpdf.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.aspsine.multithreaddownload.util.L;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.Activities.PDFActivity;
import com.mumineendownloads.mumineenpdf.Helpers.Status;
import com.mumineendownloads.mumineenpdf.Helpers.Utils;
import com.mumineendownloads.mumineenpdf.Model.PDF;
import com.mumineendownloads.mumineenpdf.R;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

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
    public static final String EXTRA_APP_INFO_START = "start_download";
    public static final String EXTRA_APP_INFO_OLD = "extra_app_info_old";
    private static final String EXTRA_APP_POSITION_LIST = "extra_pdf_position_list";
    private static final String EXTRA_APP_INFO_LIST = "extra_pdf_list";
    private File mDownloadDir;
    private DownloadManager mDownloadManager;
    List<Integer> positionList = new ArrayList<>();
    List<String> downloadedList = new ArrayList<>();
    boolean downloading;
    private NotificationCompat.Builder mBuilder;
    private NotificationManagerCompat mNotificationManager;


    private LocalBroadcastManager mLocalBroadcastManager;
    private ArrayList<PDF.PdfBean> downloadList = new ArrayList<>();
    private ArrayList<String> failedList = new ArrayList<>();
    private String failedMessage= "Download failed";

    public static void intentDownload(ArrayList<Integer> positionList, ArrayList<PDF.PdfBean> downloadingList, Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_APP_POSITION_LIST, positionList);
        intent.putExtra(EXTRA_APP_INFO_LIST, downloadingList);
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
        mDownloadManager = DownloadManager.getInstance();
        mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        if (intent != null) {
            String action = intent.getAction();
            ArrayList<PDF.PdfBean> downloadList =  (ArrayList<PDF.PdfBean>) intent.getSerializableExtra(EXTRA_APP_INFO_LIST);
            ArrayList<Integer> position =  (ArrayList<Integer>) intent.getIntegerArrayListExtra(EXTRA_APP_POSITION_LIST);
            String tag = intent.getStringExtra(EXTRA_TAG);
            switch (action) {
                case ACTION_DOWNLOAD:
                    startDownloading(downloadList,position);
                    break;
                case ACTION_PAUSE:
                    pause(tag);
                    break;
                case ACTION_CANCEL:
                    Log.e("Received","Action Cancel");
                    PDF.PdfBean pdfBean = (PDF.PdfBean) intent.getSerializableExtra(EXTRA_APP_INFO);
                    cancel(String.valueOf(pdfBean .getPid()));
                    break;
                case ACTION_PAUSE_ALL:
                    pauseAll();
                    break;
                case ACTION_CANCEL_ALL:
                    cancelAll();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private boolean Exist(int pid){
       for(PDF.PdfBean p : downloadList){
           if(p.getPid()==pid){
               return true;
           }
       }

       return false;
    }

    private String calculateEllapsedTime(long startTime, long allBytes, long downloadedBytes){
        Long elapsedTime = System.currentTimeMillis() - startTime;
        Long allTimeForDownloading = (elapsedTime * allBytes / downloadedBytes);
        Long remainingTime = allTimeForDownloading - elapsedTime;
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(remainingTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime)));
        int minute = (int) (TimeUnit.MILLISECONDS.toMinutes(remainingTime) -
                        TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime)));
        if(minute<=0){
            return String.format("%2d seconds left",
                    TimeUnit.MILLISECONDS.toSeconds(remainingTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime)));
        } else {
            return String.format("%d minutes left",
                    TimeUnit.MILLISECONDS.toMinutes(remainingTime) -
                            TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime))+1);
        }
    }

    private void startDownloading(ArrayList<PDF.PdfBean> downloadList1, ArrayList<Integer> positionList1) {
        downloadedList.clear();
        for (int i =0; i<downloadList1.size();i++){
            if (!Exist(downloadList1.get(i).getPid())) {
                downloadList.add(downloadList1.get(i));
                positionList.add(positionList1.get(i));
            }
        }

        if(!downloading){
            download(downloadList.get(0),positionList.get(0));
        }
    }

    private void download(final PDF.PdfBean appInfo, final int position) {

        File mDownloadDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File mFile = new File(mDownloadDir + "/Mumineen/");
        if(!mFile.isDirectory()){
            mFile.mkdir();
        }
        final DownloadRequest request = new DownloadRequest.Builder()
                .setName(appInfo.getPid() + ".pdf")
                .setUri("http://mumineendownloads.com/downloadFile.php?file=" + appInfo.getSource())
                .setFolder(mFile)
                .build();

        DownloadManager.getInstance().download(request, String.valueOf(appInfo.getPid()), new CallBack() {
            private long mLastTime;
            Long startTime;
            @Override
            public void onStarted() {
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setSmallIcon(android.R.drawable.stat_sys_download)
                        .setContentTitle(appInfo.getTitle())
                        .setShowWhen(false)
                        .setContentText("Please wait..")
                        .setProgress(100, 0, true)
                        .setOngoing(true)
                        .setContentIntent(resultPendingIntent)
                        .setTicker("Start download " + appInfo.getTitle());
                appInfo.setStatus(Status.STATUS_DOWNLOADING);
                updateNotification();
                sendBroadCast(appInfo,position);
            }

            @Override
            public void onConnecting() {
                mBuilder.setContentText("Connecting..")
                        .setProgress(100, 0, true);
                updateNotification();
                appInfo.setStatus(Status.STATUS_LOADING);
                sendBroadCast(appInfo,position);
            }

            @Override
            public void onConnected(long total, boolean isRangeSupport) {
                startTime = System.currentTimeMillis();
                mBuilder.setContentText("Downloading..")
                        .setProgress(100, 0, true);
                updateNotification();
                appInfo.setStatus(Status.STATUS_CONNECTED);
                sendBroadCast(appInfo,position);
            }

            @Override
            public void onProgress(long finished, long total, final int progress) {
                appInfo.setStatus(Status.STATUS_DOWNLOADING);
                appInfo.setProgress(progress);
                appInfo.setDownloadPerSize(Utils.getDownloadPerSize(finished,total,progress));

                if (mLastTime == 0) {
                    mLastTime = System.currentTimeMillis();
                }

                long currentTime = System.currentTimeMillis();
                if (currentTime - mLastTime > 500) {
                    mBuilder.setContentText(calculateEllapsedTime(startTime,total,finished));
                    mBuilder.setProgress(100, progress, false);
                    sendBroadCast(appInfo,position);
                    updateNotification();
                    mLastTime = currentTime;
                }
            }

            int i = 0;
            @Override
            public void onCompleted() {
                i++;
                if(i==1){
                    downloadNext(1);
                }

                appInfo.setStatus(Status.STATUS_DOWNLOADED);
                sendBroadCast(appInfo,position);
                clearNotification();
                notificationSuccess(appInfo);
            }

            @Override
            public void onDownloadPaused() {
                appInfo.setStatus(Status.STATUS_PAUSED);
                sendBroadCast(appInfo,position);
            }

            @Override
            public void onDownloadCanceled() {
                appInfo.setStatus(Status.STATUS_NULL);
                sendBroadCast(appInfo,position);

                clearNotification();
                downloadNext(0);

            }

            @Override
            public void onFailed(DownloadException e) {
                Log.e("failed",e.toString());
                if(e.getErrorCode()==108){
                    failedMessage = "Download failed. Server error.";
                }
                appInfo.setStatus(Status.STATUS_NULL);
                sendBroadCast(appInfo,position);
                downloadNext(-1);
                clearNotification();
                notificationFailed(appInfo);
            }
        });
    }

    public void updateNotification(){
        try {
            mNotificationManager.notify(0, mBuilder.build());
        }catch (IllegalArgumentException ignored){

        }
    }

    public void clearNotification(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNotificationManager.cancel(0);
            }
        }, 10);
    }

    public void notificationFailed(PDF.PdfBean pdfBean) {
        String contentInfo = "";


        for(int i = 0; i<failedList.size(); i++){
            if(i!=failedList.size()-1){
                contentInfo += failedList.get(i) + "\n";
            } else {
                contentInfo += failedList.get(i);
            }
        }
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(EXTRA_APP_INFO,pdfBean);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        String pdfLabel = " PDF file failed to download";
        if(failedList.size()>1){
            pdfLabel = " PDF files failed to download";
        }

        NotificationCompat.Builder  mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(failedList.size() + pdfLabel)
                        .setAutoCancel(true)
                        .setShowWhen(false)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(contentInfo).setSummaryText(failedMessage))
                        .setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary))
                        .setContentInfo(failedMessage);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, mBuilder.build());
    }

    public void notificationSuccess(PDF.PdfBean pdfBean){
        String contentInfo = "";

        for(int i = 0; i<downloadedList.size(); i++){
            if(i!=downloadedList.size()-1){
                contentInfo += downloadedList.get(i) + "\n";
            } else {
                contentInfo += downloadedList.get(i);
            }
        }
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(EXTRA_APP_INFO,pdfBean);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        String pdfLabel = " PDF file downloaded";
        if(downloadedList.size()>1){
            pdfLabel = " PDF files downloaded";
        }
        NotificationCompat.Builder  mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.noti)
                        .setContentTitle(downloadedList.size() + pdfLabel)
                        .setShowWhen(false)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(contentInfo))
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary))
                        .setContentText("Download Successful");
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(2, mBuilder.build());
    }

    private void downloadNext(int prevFailed) {
      if(!downloading) {
          if (downloadList.size()!=0) {
              if (downloadList.contains(downloadList.get(0))) {
                  if(prevFailed==1) {
                      downloadedList.add(downloadList.get(0).getTitle());
                  }else if(prevFailed==-1) {
                      try {
                          if (!failedList.contains(downloadList.get(0).getTitle())) {
                              failedList.add(downloadList.get(0).getTitle());
                          }
                      }catch (IndexOutOfBoundsException ignored){

                      }
                  }
                  downloadList.remove(downloadList.get(0));
                  positionList.remove(positionList.get(0));
              }
          }

          if (!downloadList.isEmpty()) {
              download(downloadList.get(0), positionList.get(0));
          }
      }
    }

    private void sendBroadCast(PDF.PdfBean pdf, int position) {
        Intent intent = new Intent();
        intent.setAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
        intent.putExtra(EXTRA_APP_INFO, pdf);
        intent.putExtra(EXTRA_POSITION, position);
        mLocalBroadcastManager.sendBroadcast(intent);
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

    private void addNotificationCompleted(PDF.PdfBean pdfBean, String status) {
        Intent resultIntent = new Intent(this, PDFActivity.class);
        resultIntent.putExtra(EXTRA_APP_INFO,pdfBean);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder  mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(downloadedList.size()+1 + " files downloaded")
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary))
                        .setContentText(status);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(2222, mBuilder.build());

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        mDownloadDir = new File(Environment.getExternalStorageDirectory(), "Mumineen");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadManager.pauseAll();
    }

}