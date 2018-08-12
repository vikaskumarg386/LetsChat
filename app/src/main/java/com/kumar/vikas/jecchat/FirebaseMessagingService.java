package com.kumar.vikas.jecchat;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by vikas on 13/3/18.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String title=remoteMessage.getNotification().getTitle();
        String content=remoteMessage.getNotification().getBody();
        String clickAction=remoteMessage.getNotification().getClickAction();

        String id=remoteMessage.getData().get("key_noti");





        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);



        Intent intent = new Intent(clickAction);
        intent.putExtra("key_noti",id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);








        int notificationId;
        if (clickAction.equals("JecChat.Request.Notification")){
            notificationId=0;
        }
        else if (clickAction.equals("JecChat.Notification.Notification")){
            notificationId=1;
        }
        else {
            notificationId=2;
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Uri uri=Uri.parse("android.resource://"+getPackageName()+"/raw/pull_out");
        mBuilder.setSound(uri);
        //vibrate
        //long[] v = {500,1000};
       // mBuilder.setVibrate(v);
        notificationManager.notify(notificationId, mBuilder.build());





    }
}
