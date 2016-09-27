package app.easy.text.texteasy.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jacob on 9/13/16.
 */
public class MmsReceiver extends BroadcastReceiver {

    public final String TAG = "ASKL:DJALKSF";

    public static final String SMS_BUNDLE = "pdus";

    /**
     * 
     * @param context 
     * @param intent 
     */
    /**
     * 
     * @param context 
     * @param intent 
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AKSDJL:ASKDJ:ASJ", "MMS");
        /*Bundle intentExtras = intent.getExtras();
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


        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs = null;

        String str = "";

        if (bundle != null){
            // Retrieve the Binary SMS data
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // For every SMS message received (although multipart is not supported with binary)
            for (int i=0; i<msgs.length; i++) {
                byte[] data = null;

                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                str += "Binary SMS from " + msgs[i].getOriginatingAddress() + " :";

                str += "\nBINARY MESSAGE: ";

                // Return the User Data section minus the
                // User Data Header (UDH) (if there is any UDH at all)
                data = msgs[i].getUserData();

                // Generally you can do away with this for loop
                // You'll just need the next for loop
                for (int index=0; index < data.length; index++) {
                    str += Byte.toString(data[index]);
                }

                str += "\nTEXT MESSAGE (FROM BINARY): ";

                for (int index=0; index < data.length; index++) {
                    str += Character.toString((char) data[index]);
                }

                str += "\n";
            }

            // Dump the entire message
            // Toast.makeText(context, str, Toast.LENGTH_LONG).show();
            Log.d(TAG, str);
        }*/




    }
}


