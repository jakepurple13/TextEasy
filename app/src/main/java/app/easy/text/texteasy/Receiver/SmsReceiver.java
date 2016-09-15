package app.easy.text.texteasy.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import app.easy.text.texteasy.MainActivity;

/**
 * Created by Jacob on 9/12/16.
 */
public class SmsReceiver extends BroadcastReceiver {
    private String TAG = SmsReceiver.class.getSimpleName();

    public static final String SMS_BUNDLE = "pdus";


    public SmsReceiver() {

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Log.i("AKSDJL:ASKDJ:ASJ", "ASKLDJHALSKHDJ");
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            //this will update the UI with message
            MainActivity inst = MainActivity.instance();
            inst.updateList(smsMessageStr);
            inst.unregisterReceiver(this);
        }






           /* if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String messageBody = smsMessage.getMessageBody();
                    Log.e(smsMessage.getDisplayOriginatingAddress(), messageBody);
                    Log.i("Test", "test");
                }
                Log.i("TESTING", "GOT IT");

                Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
                SmsMessage[] messages = new SmsMessage[pduArray.length];
                for (int i = 0; i < pduArray.length; i++)
                    messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);

                String SideNumber = messages[0].getDisplayOriginatingAddress();
                long Timestamp = messages[0].getTimestampMillis();

                StringBuilder bt = new StringBuilder();
                for (SmsMessage message : messages)
                    bt.append(message.getMessageBody());

                String Smsbody = bt.toString();

                Log.i(SideNumber, Smsbody);

            }*/
                    /*Intent i = new Intent(context, MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);

                    String uri = "tel:" + str2;
                    Intent iph = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
                    PendingIntent pCall = PendingIntent.getActivity(context, 0, iph, 0);

                    // Build notification

                    Notification noti = new Notification.Builder(context)
                            .setStyle(new Notification.BigTextStyle().bigText(str))
                            .setContentTitle("New sms from " + str2)
                            .setContentText(str)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pIntent)
                            .addAction(R.mipmap.ic_launcher, "Call", pCall)
                            .addAction(R.mipmap.ic_launcher, "Read", pIntent)
                            .addAction(R.mipmap.ic_launcher, "Delete", pIntent)
                            .build();

                    NotificationManager notificationManager = (NotificationManager) context
                            .getSystemService(context.NOTIFICATION_SERVICE);
                    // hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    // notification
                    notificationManager.notify(0, noti);
                */




    }

}