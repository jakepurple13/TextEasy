package app.easy.text.texteasy.Receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import app.easy.text.texteasy.Messages.MainActivity;

/**
 * Created by Jacob on 3/7/17.
 */

public class NotificationReceiver extends BroadcastReceiver {

    String phoneNumber;

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean cancel = intent.getBooleanExtra("cancel", false);

        if(cancel) {

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.cancel(1);

        } else {

            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {

                phoneNumber = intent.getStringExtra("Number");

                CharSequence id = remoteInput.getCharSequence(NotificationActivity.KEY_TEXT_REPLY);

                /*NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                    .setContentTitle("Thank you --- " + id);

                NotificationManager notificationManager = (NotificationManager) context.
                    getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(112, mBuilder.build());*/

                String toSend;

                if(id==null) {
                    toSend = remoteInput.getCharSequence(Intent.EXTRA_TEXT).toString();
                    Log.i("dsakfj", "onReceive: " + toSend);
                } else {
                    toSend = id.toString();
                }

                sendSMS(phoneNumber, toSend, context);

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.cancel(1);
            }
        }
    }

    private void sendSMS(String phoneNumber, String message, Context context) {

        String SENT = "SENT_SMS_ACTION";
        String DELIVERED = "DELIVERED_SMS_ACTION";

        Intent sentIntent = new Intent(SENT);
        PendingIntent sentPI = PendingIntent.getBroadcast(context.getApplicationContext(), 0, sentIntent, 0);

        Intent deliveryIntent = new Intent(DELIVERED);
        PendingIntent deliver = PendingIntent.getBroadcast(context.getApplicationContext(), 0, deliveryIntent, 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliver);

    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(NotificationActivity.KEY_TEXT_REPLY);
        }
        return null;
    }
}
