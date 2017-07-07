package com.mumineendownloads.mumineenpdf.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mumineendownloads.mumineenpdf.Activities.MainActivity;
import com.mumineendownloads.mumineenpdf.R;


public class HandleMessage extends FirebaseMessagingService {
    public static final String UPDATE_APP = "update_app.mumineenpdf";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message_type = remoteMessage.getData().get("m_t");
        if(message_type.equals("updateNotification")){
            showUpdateNotification();
        }
    }

    private void showUpdateNotification() {
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent resultIntent = new Intent(this, MainActivity.class);
                    resultIntent.putExtra(UPDATE_APP,"update");
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.Builder  mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("New update available")
                        .setAutoCancel(true)
                        .setSound(uri)
                        .setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary))
                        .setContentText("Mumineen PDF has new update available on google play store");
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, mBuilder.build());
    }
}
