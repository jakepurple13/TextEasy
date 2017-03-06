package app.easy.text.texteasy.Receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import app.easy.text.texteasy.ContactList.Contacts;
import app.easy.text.texteasy.Messages.MainActivity;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Translator;

/**
 * Created by Jacob on 9/12/16.
 */
public class SmsReceiver extends BroadcastReceiver {

    MainActivity inst;

    /**
     * 
     */
    public SmsReceiver() {

    }

    private static SmsListener mListener;

    /**
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Log.i("Text being received!", "SMS RECEIVER \t" + context.getPackageName());

        Translator t = new Translator(context);

        SmsMessage[] smsMess = Telephony.Sms.Intents.getMessagesFromIntent(intent);

        for (int i = 0; i < smsMess.length; i++) {

            Log.e(i + smsMess[i].getDisplayOriginatingAddress(), smsMess[i].getDisplayMessageBody());

        }

        String from = smsMess[0].getDisplayOriginatingAddress();
        String message = t.translate(smsMess[0].getDisplayMessageBody());


        inst = MainActivity.instance();

        try {

            if (!inst.isDestroyed()) {
                inst.updateList(getContactName(context, from) + ": " + message, 1, true);
            }

        } catch (NullPointerException e) {
            Log.e("Null point on line 70", e.toString());

            if (!inst.isDestroyed()) {
                inst.updateList(from + ": " + message, 1, true);
            }

        }


        final String myPackageName = context.getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName)) {

            // App is not default.
            // Show the "not currently set as the default SMS app" interface

        } else {
            // App is the default.
            // Hide the "not currently set as the default SMS app" interface
            notification(from, message, context);

        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    /**
     *
     * @param context
     * @param phoneNumber
     */
    public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    /**
     *
     * @param from
     * @param message
     * @param context
     */
    public void notification(String from, String message, Context context) {

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.texteasyicon);

        mBuilder.setContentTitle(getContactName(context, from));
        mBuilder.setContentText(message);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message));

        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setLights(Color.BLUE, 5000, 500);
        mBuilder.setAutoCancel(true);
        mBuilder.setVibrate(new long[]{1000});

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);
        Log.e("NUMBERS", from + "\t" + message);
        resultIntent.putExtra("Number", from);
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
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

    }


}


