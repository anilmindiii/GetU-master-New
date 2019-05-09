package getu.app.com.getu.fcm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import getu.app.com.getu.R;
import getu.app.com.getu.app_session.Session;
import getu.app.com.getu.chat.activity.ChatActivity;
import getu.app.com.getu.common_activity.MyCustomApplication;
import getu.app.com.getu.common_activity.NotificationUserDetailActivity;
import getu.app.com.getu.util.Constant;


/**
 * Created by abc on 15/11/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    private String className;
    public static String CHAT_HISTORY = "";

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "getFrom : " + remoteMessage.getFrom().toString());
        Log.d(TAG, "getData : " + remoteMessage.getData().toString());
        Log.d(TAG, "getNotification : " + remoteMessage.getNotification().toString());

        try {
            MyCustomApplication app = (MyCustomApplication) getApplication();
            className = app.getActiveActivity().getClass().getSimpleName();
            if (className == null) {
                className = "";
            }
        } catch (NullPointerException e) {

        }

        if (remoteMessage.getData() != null){ notificationHandle(remoteMessage);}
    }

    private void notificationHandle(RemoteMessage remoteMessage) {
        // {type=viewProfile, title=GetU, click_action=chatActivity, notificationId=0, body=Chetan Thakur visited your profile, senderId=8}

        String type = remoteMessage.getData().get("type");

        if (!type.equals("chat")) {

            String title = remoteMessage.getData().get("title");
            String click_action = remoteMessage.getData().get("click_action");
            String notificationId = remoteMessage.getData().get("notificationId");
            String message = remoteMessage.getData().get("body");
            String senderId = remoteMessage.getData().get("senderId");

            if (type != null) {
                sendNotification(message, title, senderId, type);
            }
        }else {
            String username = remoteMessage.getData().get("title");
            String uid = remoteMessage.getData().get("uid");
            String click_action = remoteMessage.getData().get("click_action");
            String message = remoteMessage.getData().get("body");
            String image = remoteMessage.getData().get("profilepic");
            if (!CHAT_HISTORY.equals("1")) {
                sendNotificationChat(username, uid, message, image);
            }
        }
    }

    private void sendNotificationChat(String username, String uid, String message, String image) {
        Intent intent = null;

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra("USER_ID",uid);
        intent.putExtra("FULLNAME",username);
        intent.putExtra("PROFILE_PIC",image);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);

        Uri notificaitonSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(username)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(notificaitonSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotification(String message, String title ,String senderId , String type) {
        Intent intent = null;

            if (type.equals("viewProfile")) {
                    intent = new Intent(this, NotificationUserDetailActivity.class);
                    intent.putExtra("senderId", senderId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }

            PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);

            Uri notificaitonSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(notificaitonSound)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
    }
}
