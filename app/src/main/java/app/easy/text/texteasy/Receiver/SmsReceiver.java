package app.easy.text.texteasy.Receiver;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import app.easy.text.texteasy.MainActivity;
import app.easy.text.texteasy.R;
import app.easy.text.texteasy.Translator;

/**
 * Created by Jacob on 9/12/16.
 */
public class SmsReceiver extends BroadcastReceiver {
    private String TAG = SmsReceiver.class.getSimpleName();

    public static final String SMS_BUNDLE = "pdus";

    MainActivity inst;

    public SmsReceiver() {

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Log.i("AKSDJL:ASKDJ:ASJ", "SMS RECEIVER");

        Translator t = new Translator();

        SmsMessage[] smsMess = Telephony.Sms.Intents.getMessagesFromIntent(intent);

        for(int i=0;i<smsMess.length;i++) {

            Log.e(i + smsMess[i].getDisplayOriginatingAddress(), smsMess[i].getDisplayMessageBody());

        }

        String from = smsMess[0].getDisplayOriginatingAddress();
        String message = t.translate(smsMess[0].getDisplayMessageBody());

        notification(getContactName(context,from), message, context);

        inst = MainActivity.instance();

        inst.updateList(getContactName(context,from) + ": " + message, 1, true);

        //Toast.makeText(context, from + ": " + t.translate(message), Toast.LENGTH_SHORT).show();

    }

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

    public void notification(String from, String message, Context context) {

        //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.myrect);
        //   if(high == top) {
        mBuilder.setContentTitle(from);
        mBuilder.setContentText(message);
        //mBuilder.setLights(Color.BLUE, 5000, 1);
        //mBuilder.setLights(Color.MAGENTA, 5000, 1);
        //mBuilder.setLights(Color.rgb(200, 100, 210), 5000, 1);
        //v.vibrate(5*100);
        //    }

        mBuilder.setOnlyAlertOnce(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context,  MainActivity.class);
        resultIntent.putExtra("Number", PhoneNumberUtils.normalizeNumber(from));
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
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