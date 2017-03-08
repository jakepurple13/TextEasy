package app.easy.text.texteasy;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.easy.text.texteasy.ContactList.Contacts;
import app.easy.text.texteasy.Receiver.NotificationReceiver;

/**
 * Created by Jacob on 9/26/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "SERVICE";

    private static final String KEY_TEXT_REPLY = "key_text_reply";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        //
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        notification(remoteMessage.getNotification().getBody(), this);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    public void notification(String message, Context context) {



        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        //.setLargeIcon(R.drawable.texteasyicon)
                        .setSmallIcon(R.drawable.texteasyicon);

        Translator translate = new Translator(this);


        mBuilder.setContentTitle("TextEasy");
        mBuilder.setContentText(translate.translate(message) + "\nClick here to give Feedback.");
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(translate.translate(message)));

        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setLights(Color.BLUE, 5000, 500);
        mBuilder.setAutoCancel(true);
        //mBuilder.setVibrate(new long[]{1000, 1000});
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setColor(R.color.white);



        String url = getResources().getString(R.string.feedback_link);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));



        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, Contacts.class);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        //stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addParentStack(Contacts.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);



        Intent received = new Intent(context, NotificationReceiver.class);
        received.putExtra("Number", "2017854423");
        received.putExtra("cancel", false);

        //Provide receiver class to handle the response
        PendingIntent detailsPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                received,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        String replyLabel = "Reply to text";
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.texteasyicon, replyLabel, detailsPendingIntent)
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();


        Intent cancelled = new Intent(context, NotificationReceiver.class);
        cancelled.putExtra("cancel", true);

        PendingIntent cancelIntent = PendingIntent.getBroadcast(
                context,
                0,
                cancelled,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //mBuilder.addAction(replyAction);
        mBuilder.addAction(android.R.drawable.ic_notification_clear_all, "Cancel", cancelIntent);


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }




}
